package com.scrip.main.strategy;

import com.scrip.main.strategy.failedbreakout.Utils;
import com.scrip.main.util.CsvTimeSeries;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DayLowStrategy {

    public static void main(String[] args) {
        BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\NIFTY BANK\\5minute\\data.csv");

        Map<ZonedDateTime, List<Bar>> barData = Utils.getBarData(series);
        ClosePriceIndicator closePriceIndicator = new ClosePriceIndicator(series);
        SMAIndicator smaIndicator = new SMAIndicator(closePriceIndicator, 200);

        for (Map.Entry<ZonedDateTime, List<Bar>> data : barData.entrySet()) {
            ZonedDateTime date = data.getKey();
            List<Bar> bars = data.getValue();

            boolean isFirstPivotMet = false;
            Num prevPilotLow = null;
            for (int i=0; i< bars.size()-4; i++) {
                boolean isPivot = isCurrentCandlePivot(i, bars);

                if (isFirstPivotMet && isPivot) {
                    boolean isBullishCandle = checkForBullishCandle(i, bars);
                    if (isBullishCandle && bars.get(i).getLowPrice().isLessThan(prevPilotLow)) {
                        System.out.println("bars.get(i) = " + bars.get(i));
                        prevPilotLow = null;
                        break;
                    }
                }

                if (!isFirstPivotMet && isPivot) {
                    isFirstPivotMet = true;
                    prevPilotLow = bars.get(i).getLowPrice();
                }
            }
        }
    }

    private static boolean checkForBullishCandle(int index, List<Bar> bars){
        if (bars.get(index+1).getClosePrice().isGreaterThan(bars.get(index).getHighPrice())) {
            return true;
        } else if (bars.get(index+2).getClosePrice().isGreaterThan(bars.get(index).getHighPrice())) {
            return true;
        }

        return false;
    }

    private static boolean isCurrentCandlePivot(int index, List<Bar> bars){
        if (index < 4) {
            return false;
        }
        for (int i=index; i >= index-4; i--) {
            if (bars.get(index).getLowPrice().isGreaterThan(bars.get(i).getLowPrice())) {
                return false;
            }
        }

        for (int i=index+1; i <= index+2; i++) {
            if (bars.get(index).getLowPrice().isGreaterThan(bars.get(i).getLowPrice())) {
                return false;
            }
        }
        return true;
    }
}
