package com.scrip.main.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;

public class SeedDataMySql {

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

	public static void seedScripData(Date fromDate, Date toDate, String[] dateFormat, String instrumentToken,
			String instrumentName, boolean continuous) {
		
		if(fromDate.after(new Date())) {
			return;
		}

		HistoricalData data = null;
		try {
			data = Util.getKiteConnect().getHistoricalData(fromDate, toDate, instrumentToken, dateFormat[0],
					continuous, false);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KiteException e) {
			e.printStackTrace();
		}

		seedData(data, instrumentName, dateFormat[1]);

	}

	private static void seedData(HistoricalData historicalData, String instrumentName, String dbTable) {
		Connection conn = Util.getDbConnection();
		Statement statement;
		try {
			statement = conn.createStatement();
			for (HistoricalData data : historicalData.dataArrayList) {
				ZonedDateTime date = ZonedDateTime.parse(data.timeStamp, DATE_FORMAT);
				String query = " insert into " + dbTable + " (scripName, open, high, low, close, volume, time)"
						+ " values ('" + instrumentName + "', " + data.open + ", " + data.high + ", " + data.low + ", "
						+ data.close + ", " + data.volume + ", '" + date.toLocalDateTime() + "')";

				statement.addBatch(query);

			}
			statement.executeBatch();
			statement.close();
			conn.close();
		} catch (SQLException e) {
			System.out.println("Insert failed for scrip: " + instrumentName);
			e.printStackTrace();
			System.exit(0);
		}

	}

	public static void main(String[] args) throws Exception {
		String[] dateFormat = Constants.dataDbMapping30min;
		List<Symbol> getSymbolList = Util.getSymbolList();
		for (Symbol s : getSymbolList) {

			Date[] dateWindow = getDateWindow(s.getTradingSymbol(), dateFormat);
			seedScripData(dateWindow[0], dateWindow[1], dateFormat, s.getInstrumentToken(), s.getTradingSymbol(),
					false);
		}
		System.exit(3);
	}

	private static Date[] getDateWindow(String instrumentName, String[] dateFormat) {
		Calendar cal = Calendar.getInstance();
		Date toDate = cal.getTime();

		Date fromDate = getMaxTimeForScrip(instrumentName, dateFormat[1]);
		
		if (fromDate == null) {
			cal.set(Calendar.YEAR, Integer.valueOf(dateFormat[2]));
			cal.set(Calendar.MONTH, Integer.valueOf(dateFormat[3]));
			cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(dateFormat[4]));
			cal.set(Calendar.HOUR_OF_DAY, Integer.valueOf(dateFormat[5]));

			fromDate = cal.getTime();
		}else {
			if(dateFormat[0].equals("day")) {
				fromDate.setMinutes(fromDate.getMinutes() + Integer.valueOf(dateFormat[6]));
				fromDate.setDate(fromDate.getDate() + 1);
			} else {
				fromDate.setMinutes(fromDate.getMinutes() + Integer.valueOf(dateFormat[6]));
			}
		}
		Date[] dateWindow = { fromDate, toDate };
		System.out.println(instrumentName + " - " +fromDate + " - " + toDate);
		return dateWindow;
	}
	
	private static Date getMaxTimeForScrip(String instrumentName, String tableName) {
		Connection conn = Util.getDbConnection();
		Date dateTime = null;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(
					"select max(time) as timeData from " + tableName + " where scripName='" + instrumentName + "'");
			while (rs.next()) {
				java.sql.Timestamp ts = rs.getTimestamp("timeData");
				if (null != ts) {
					dateTime = new Date(ts.getTime());
				}
			}
			rs.close();
			stmt.close();
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return dateTime;
	}
	

}
