package com.scrip.main.util;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONException;

import com.opencsv.CSVWriter;
import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;

public class SeedDataIndices {

public static void seedScripData(Date fromDate, Date toDate, String duration, String instrumentToken, String instrumentName, boolean continuous) {
		
		HistoricalData data = null;
		try {
			data = Util.getKiteConnect().getHistoricalData(fromDate, toDate, instrumentToken, duration, continuous, false);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KiteException e) {
			e.printStackTrace();
		}
		
		String scripLocation = Constants.rootDirectoryIndices + "\\" + instrumentName + "\\" + duration;
		Path folderPath = Paths.get(scripLocation);
		try {
			if(!Files.exists(folderPath)) {
				Files.createDirectories(folderPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		seedData(data, scripLocation);
		
	}
	
	private static void seedData(HistoricalData historicalData, String scripLocation) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(scripLocation + "\\data.csv");

			CSVWriter writer = new CSVWriter(fileWriter);
			List<String[]> dataList = new ArrayList<String[]>();

			for (HistoricalData data : historicalData.dataArrayList) {
				dataList.add(new String[] { data.timeStamp, String.valueOf(data.open), String.valueOf(data.high), String.valueOf(data.low), String.valueOf(data.close), String.valueOf(data.volume)});
			}

			writer.writeAll(dataList);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		Calendar cal = Calendar.getInstance();
		Date toDate = cal.getTime();
		
		cal.set(Calendar.YEAR, 2016);
		cal.set(Calendar.MONTH, 00);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.set(Calendar.HOUR_OF_DAY, 06);
		
		Date fromDate = cal.getTime();
		System.out.println(fromDate);
		
		List<Symbol> getSymbolList = Util.getIndicesList();
		for(Symbol s : getSymbolList) {
			seedScripData(fromDate, toDate, "week", s.getInstrumentToken(), s.getTradingSymbol(), false);
		}
		System.exit(3);
	}


}
