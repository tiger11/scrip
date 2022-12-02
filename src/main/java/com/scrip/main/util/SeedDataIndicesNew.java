package com.scrip.main.util;

import com.opencsv.CSVWriter;
import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import org.json.JSONException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SeedDataIndicesNew {
	
	public static void seedScripData(LocalDateTime toDate, String duration, String instrumentToken, String instrumentName, boolean continuous) {

		LocalDateTime fromDate = toDate.minusDays(10);
		ArrayList<HistoricalData> data = new ArrayList<>();
		try {
			while (fromDate.getYear() >= 2020) {
				HistoricalData historicalData = Util.getKiteConnect().getHistoricalData(Date.from(fromDate.atZone(ZoneId.systemDefault()).toInstant()),
						Date.from(toDate.atZone(ZoneId.systemDefault()).toInstant()), instrumentToken, duration, continuous, false);
				data.add(historicalData);
				toDate = fromDate.minusDays(1);
				fromDate = toDate.minusDays(10);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KiteException e) {
			e.printStackTrace();
		}
		
		String scripLocation = Constants.rootDirectory + "\\" + instrumentName + "\\" + duration;
		Path folderPath = Paths.get(scripLocation);
		try {
			if(!Files.exists(folderPath)) {
				Files.createDirectories(folderPath);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		Collections.reverse(data);
		seedData(data, scripLocation);
		
	}
	
	private static void seedData(ArrayList<HistoricalData> historicalData, String scripLocation) {
		FileWriter fileWriter;
		try {
			fileWriter = new FileWriter(scripLocation + "\\data.csv");

			CSVWriter writer = new CSVWriter(fileWriter);
			List<String[]> dataList = new ArrayList<String[]>();

			for (HistoricalData dataMain : historicalData) {
				for (HistoricalData data : dataMain.dataArrayList) {
					dataList.add(new String[]{data.timeStamp, String.valueOf(data.open), String.valueOf(data.high), String.valueOf(data.low), String.valueOf(data.close), String.valueOf(data.volume)});
				}
			}

			writer.writeAll(dataList);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		LocalDateTime toDate = LocalDateTime.now();

		List<Symbol> getSymbolList = Util.getSymbolList();
		for(Symbol s : getSymbolList) {
			seedScripData(toDate, "minute", s.getInstrumentToken(), s.getTradingSymbol(), false);
		}
		System.exit(0);
	}

}
