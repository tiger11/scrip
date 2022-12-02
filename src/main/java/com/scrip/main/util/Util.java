package com.scrip.main.util;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.scrip.main.pojo.ConnectionProperties;
import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.SessionExpiryHook;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Instrument;

public class Util {

	private static KiteConnect kiteSdk = null;

	public static Connection getDbConnection() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Connection con = null;
		try {
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/scrip?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=IST", "gaurav", "gaurav");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return con;
	}

	public static KiteConnect getKiteConnect() {
		if (kiteSdk == null) {
			ConnectionProperties props = getConnectionProperties();
			kiteSdk = new KiteConnect(props.getApiKey(), false);
			kiteSdk.setUserId(props.getUserId());

			kiteSdk.setAccessToken(props.getAccessToken());
			kiteSdk.setPublicToken(props.getPublicToken());

			kiteSdk.setSessionExpiryHook(new SessionExpiryHook() {
				public void sessionExpired() {
					System.out.println("session expired");
				}
			});
		}
		return kiteSdk;
	}

	public static List<Symbol> getSymbolList() {
		List<Symbol> symbolList = new ArrayList<Symbol>();

		File file = new File("D:\\files\\code\\scrip\\src\\main\\java\\symbolList.csv");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				if (!reader.ready()) break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] line = new String[0];
			try {
				line = reader.readLine().split(",");
			} catch (IOException e) {
				e.printStackTrace();
			}
			Symbol sym = new Symbol();
			sym.setExchangeToken(line[1]);
			sym.setInstrumentToken(line[0]);
			sym.setTradingSymbol(line[2]);
			symbolList.add(sym);
		}
		return symbolList;
	}

	public static List<Symbol> getIndicesList() throws Exception {
		List<Symbol> indicesList = new ArrayList<Symbol>();
		File file = new File("D:\\files\\code\\scrip\\src\\main\\java\\indicesList.csv");

		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		while (true) {
			try {
				if (!reader.ready()) break;
			} catch (IOException e) {
				e.printStackTrace();
			}
			String[] line = new String[0];
			try {
				line = reader.readLine().split(",");
			} catch (IOException e) {
				e.printStackTrace();
			}
			Symbol sym = new Symbol();
			sym.setExchangeToken(line[1]);
			sym.setInstrumentToken(line[0]);
			sym.setTradingSymbol(line[2]);
			indicesList.add(sym);
		}
		return indicesList;
	}

	public static void getAllInstruments() throws KiteException, IOException {
		// Get all instruments list. This call is very expensive as it involves
		// downloading of large data dump.
		// Hence, it is recommended that this call be made once and the results stored
		// locally once every morning before market opening.
		List<Instrument> instruments = getKiteConnect().getInstruments();
		for (Iterator iterator = instruments.iterator(); iterator.hasNext();) {
			Instrument instrument = (Instrument) iterator.next();
			if (instrument.getSegment().contains("MCX") && !instrument.getTradingsymbol().contains("CE")
					&& !instrument.getTradingsymbol().contains("PE")) {
				System.out.println(instrument.getInstrument_token() + "," + instrument.getExchange_token() + ","
						+ instrument.getTradingsymbol());
			}
		}
	}

	public static void main(String[] args) throws IOException, KiteException {
		getAllInstruments();
		System.exit(0);
	}

	public static ConnectionProperties getConnectionProperties() {
		ConnectionProperties props = new ConnectionProperties();
		InputStream in = null;
		try {
			in = new FileInputStream(new File("D:\\files\\code\\scrip\\src\\main\\java\\application.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		Properties prop = new Properties();

		try {
			if (in != null)
				prop.load(in);
			props.setAccessToken(prop.getProperty("access_token"));
			props.setApiKey(prop.getProperty("api_key"));
			props.setApiSecret(prop.getProperty("api_secret"));
			props.setPublicToken(prop.getProperty("public_token"));
			props.setUserId(prop.getProperty("user_Id"));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return props;
	}

}
