package com.scrip.live;

import com.opencsv.CSVWriter;
import com.scrip.main.pojo.Symbol;
import com.scrip.main.strategy.failedbreakout.Utils;
import com.scrip.main.util.Constants;
import com.scrip.main.util.CsvTimeSeries;
import com.scrip.main.util.Util;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;
import org.json.JSONException;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.Num;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Helper {

    public static HashMap<String, Num> getPreviousHigh(List<Symbol> getSymbolList) {
        HashMap<String, Num> prevHigh = new HashMap<>();
        for (Symbol symbol :
                getSymbolList) {
            BarSeries barSeries = CsvTimeSeries.csvTimeSeries(
                    Constants.rootDirectoryLive + "\\" + symbol.getTradingSymbol() + "\\" + "5minute\\data.csv");
            Map<ZonedDateTime, List<Bar>> barDataRun = Utils.getBarData(barSeries);
            List<ZonedDateTime> dataSet = new ArrayList<>(barDataRun.keySet());
            List<ZonedDateTime> dataSetSub = dataSet.subList(dataSet.size()-2, dataSet.size());

            Num dayHighOne = Utils.getHighestCloseOfDay(barDataRun.get(dataSetSub.get(0)));
            Num dayHighTwo = Utils.getHighestCloseOfDay(barDataRun.get(dataSetSub.get(1)));

            Num dayHigh = (dayHighOne.isGreaterThanOrEqual(dayHighTwo)) ? dayHighOne : dayHighTwo;
            prevHigh.put(symbol.getInstrumentToken(), dayHigh);
        }
        return prevHigh;
    }

    public static void seedScripData(LocalDateTime toDate, String duration
            , String instrumentToken, String instrumentName, boolean continuous) {

        LocalDateTime fromDate = toDate.minusDays(5);
        HistoricalData historicalData = null;
        try {
             historicalData = Util.getKiteConnect()
                    .getHistoricalData(Date.from(fromDate.atZone(ZoneId.systemDefault()).toInstant()),
                    Date.from(toDate.atZone(ZoneId.systemDefault()).toInstant()), instrumentToken, duration, continuous, false);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KiteException e) {
            e.printStackTrace();
        }

        String scripLocation = Constants.rootDirectoryLive + "\\" + instrumentName + "\\" + duration;
        Path folderPath = Paths.get(scripLocation);
        try {
            if(!Files.exists(folderPath)) {
                Files.createDirectories(folderPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        seedData(historicalData, scripLocation);
    }

    private static void seedData(HistoricalData historicalData, String scripLocation) {
        FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(scripLocation + "\\data.csv");
            CSVWriter writer = new CSVWriter(fileWriter);
            List<String[]> dataList = new ArrayList<String[]>();

            for (HistoricalData data : historicalData.dataArrayList) {
                dataList.add(new String[]{data.timeStamp, String.valueOf(data.open),
                        String.valueOf(data.high), String.valueOf(data.low), String.valueOf(data.close), String.valueOf(data.volume)});
            }
            writer.writeAll(dataList);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updatePrevHigh(List<Symbol> getSymbolList) {
        LocalDateTime toDate = LocalDateTime.now().minusDays(1);
        for (Symbol s : getSymbolList) {
            Helper.seedScripData(toDate, "5minute"
                    , s.getInstrumentToken(), s.getTradingSymbol(), false);
        }
    }

    public static void fetchAllBarsTemplate(List<Symbol> symbolList, ConcurrentHashMap<String, BarSeries> allBars) {
        for (Symbol s :
                symbolList) {
            allBars.put(s.getInstrumentToken(), new BaseBarSeries(s.getTradingSymbol()));
        }
    }
}
