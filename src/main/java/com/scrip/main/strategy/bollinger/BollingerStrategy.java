package com.scrip.main.strategy.bollinger;

import com.scrip.main.util.CsvTimeSeries;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandWidthIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

import java.util.HashMap;
import java.util.SortedMap;
import java.util.TreeMap;

public class BollingerStrategy {

    public static void main(String[] args) {
        BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\NIFTY BANK\\5minute\\data.csv");

        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        StandardDeviationIndicator standardDeviationIndicator = new StandardDeviationIndicator(closePriceIndicator, 20);
        SMAIndicator emaIndicator = new SMAIndicator(closePriceIndicator, 20);

        BollingerBandsMiddleIndicator bbm = new BollingerBandsMiddleIndicator(emaIndicator);
        BollingerBandsUpperIndicator bbu = new BollingerBandsUpperIndicator(bbm, standardDeviationIndicator);

        BollingerBandsLowerIndicator bbl = new BollingerBandsLowerIndicator(bbm, standardDeviationIndicator);
        BollingerBandWidthIndicator bandWidthIndicator = new BollingerBandWidthIndicator(bbu, bbm, bbl);

        SortedMap<Num, Bar> numBarHashMap = new TreeMap<>();

        for (int i= series.getBarCount()-1; i>=series.getBarCount()-100; i--) {
            numBarHashMap.put(bandWidthIndicator.getValue(i), series.getBar(i));
        }

        for (Num key :numBarHashMap.keySet()) {
            System.out.println("key = " + key);
            System.out.println("numBarHashMap = " + numBarHashMap.get(key));
            System.out.println(" ======================== ");
        }


    }

}
