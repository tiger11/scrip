package com.scrip.main.util;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;

import com.opencsv.CSVReader;

public class CsvTimeSeries {
	
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

    public static BarSeries csvTimeSeries(String filename) {

    	Reader reader = null;
		try {
			reader = Files.newBufferedReader(Paths.get(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}

        BarSeries series =  new BaseBarSeries("bars");

        try (CSVReader csvReader = new CSVReader(reader)) {
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                ZonedDateTime date = ZonedDateTime.parse(line[0], DATE_FORMAT);
                double open = Double.parseDouble(line[1]);
                double high = Double.parseDouble(line[2]);
                double low = Double.parseDouble(line[3]);
                double close = Double.parseDouble(line[4]);
                double volume = Double.parseDouble(line[5]);

                series.addBar(date, open, high, low, close, volume);
            }
        } catch (IOException ioe) {
            Logger.getLogger(CsvTimeSeries.class.getName()).log(Level.SEVERE, "Unable to load bars from CSV", ioe);
        } catch (NumberFormatException nfe) {
            Logger.getLogger(CsvTimeSeries.class.getName()).log(Level.SEVERE, "Error while parsing value", nfe);
        }
        return series;
    }
}
