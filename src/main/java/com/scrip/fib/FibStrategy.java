package com.scrip.fib;

import com.scrip.main.strategy.failedbreakout.Utils;
import com.scrip.main.util.CsvTimeSeries;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FibStrategy {

    private static final Double [] fibLevels = {.236, .382, .50, .618, .786, 1.618};
    private static final DecimalNum per100 = DecimalNum.valueOf(100);

    public static void main(String[] args) {
        BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\NIFTY BANK\\5minute\\data.csv");
        Map<ZonedDateTime, List<Bar>> getBarData = Utils.getBarData(series);

        List<Bar> lastBar = getBarData.get(getBarData.keySet().toArray()[getBarData.size()-1]);

        Map<Double, Num> levelsLow = computeLevelsFromLow(lastBar);
        Map<Double, Num> levelsHigh = computeLevelsFromHigh(lastBar);
        System.out.println("levels.size() = " + levelsLow.size());
    }

    private static Map<Double, Num> computeLevelsFromLow(List<Bar> bar) {
        Map<Double, Num> fibMap = new HashMap();
        Num low = Utils.getLowOfDay(bar);
        Num high = Utils.getHighOfDay(bar);
        Num diff = high.minus(low);
        fibMap.put(0.0, low);
        fibMap.put(1.0, high);

        for (Double d : fibLevels) {
            fibMap.put(d, low.plus(diff.multipliedBy(DecimalNum.valueOf(d))));
        }

        return fibMap;
    }

    private static Map<Double, Num> computeLevelsFromHigh(List<Bar> bar) {
        Map<Double, Num> fibMap = new HashMap();
        Num low = Utils.getLowOfDay(bar);
        Num high = Utils.getHighOfDay(bar);
        Num diff = high.minus(low);
        fibMap.put(0.0, low);
        fibMap.put(1.0, high);

        for (Double d : fibLevels) {
            fibMap.put(d, high.minus(diff.multipliedBy(DecimalNum.valueOf(d))));
        }

        return fibMap;
    }

}
