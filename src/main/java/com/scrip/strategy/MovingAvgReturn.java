package com.scrip.strategy;

import com.scrip.main.util.CsvTimeSeries;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.util.List;

public class MovingAvgReturn {

    public static void main(String[] args) {

        BarSeries barSeries = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\ACC\\5minute\\data.csv");
        List<Bar> barList = barSeries.getBarData();
        barList.forEach(o -> {
            checkBarChange(o);
        });

    }

    private static boolean checkBarChange(Bar bar){
        Num openPrice = bar.getOpenPrice();
        Num closePrice = bar.getClosePrice();
        Num perc = DecimalNum.valueOf(100);
        Num percTop = DecimalNum.valueOf(0.8);

        if (bar.isBullish() && (((closePrice.minus(openPrice)).multipliedBy(perc).dividedBy(openPrice)).isGreaterThanOrEqual(percTop))) {
            System.out.println("bar = " + bar);
        }

        return false;
    }


}
