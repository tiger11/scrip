package com.scrip.main.util;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import com.opencsv.CSVReader;

public class ValidateData {
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	private final static SimpleDateFormat sdfWeekend = new SimpleDateFormat("EEEE"); 
	
	public static void main(String[] args) throws IOException {
		validateDailyData();
	}

	private static void validateDailyData() throws IOException {

		File[] directories = new File(Constants.rootDirectory).listFiles(File::isDirectory);

		for (int i = 0; i < directories.length; i++) {
			
			String file = directories[i].getAbsolutePath() + "//day//data.csv";
			logDuplicateEntries(file, directories[i].getAbsolutePath());
			logWeekendData(file, directories[i].getAbsolutePath());
			
			file = directories[i].getAbsolutePath() + "//5minute//data.csv";

			logDuplicateEntries(file, directories[i].getAbsolutePath());
			logWeekendData(file, directories[i].getAbsolutePath());
		}

	}
	
	private static void logWeekendData(String file, String instrumentPath) {
		String[] nextRecord;
		Calendar c = Calendar.getInstance();

		try {
			Reader reader = Files.newBufferedReader(Paths.get(file));
			CSVReader csvReader = new CSVReader(reader);
			while ((nextRecord = csvReader.readNext()) != null) {
				String dateStr = nextRecord[0];
				if(dateStr.contains("2016-10-30"))continue;
				try {
					Date d = sdf.parse(dateStr);
					String dayOfWeek = sdfWeekend.format(d);
					if(dayOfWeek.equals("Sunday") || dayOfWeek.equals("Saturday")) {
						System.out.println("Weekend date found: "+ dayOfWeek + " Instrument : "+file + " Record : "+ nextRecord[0]);
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void logDuplicateEntries(String file, String instrumentPath) {
		int dupEntriesCount = 0;
		HashSet<String> dupEntries = new HashSet<>();
		String[] nextRecord;
		int count = 0;
		try {
			Reader reader = Files.newBufferedReader(Paths.get(file));
			CSVReader csvReader = new CSVReader(reader);
			while ((nextRecord = csvReader.readNext()) != null) {
				count++;
				if (dupEntries.contains(nextRecord[0])) {
					dupEntriesCount++;
				} else {
					dupEntries.add(nextRecord[0]);
				}
			}
			csvReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(dupEntriesCount > 0) {
			System.out.println("Duplicate Entries Count is: "+ dupEntriesCount + " for : " + instrumentPath);
		}
	}

}
