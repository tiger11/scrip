package com.scrip.main.strategy.failedbreakout;

import com.scrip.main.charting.CreateCandleStickChart;
import com.scrip.main.charting.JfreeCandlestickChart;
import com.scrip.main.pojo.Symbol;
import com.scrip.main.util.CsvTimeSeries;
import com.scrip.main.util.Util;
import org.checkerframework.checker.units.qual.A;
import org.jfree.chart.ChartUtils;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.io.File;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class FailedBreakOut implements Runnable{

    private String scripName;

    public FailedBreakOut(String scripName){
        this.scripName = scripName;
    }

    public void run() {
        BarSeries barSeries = CsvTimeSeries.csvTimeSeries("D:\\files\\data\\" +scripName+ "\\5minute\\data.csv");
        //BarSeries barSeries = CsvTimeSeries.csvTimeSeries("D:\\files\\scrip\\dataIndices\\NIFTY BANK\\5minute\\data.csv");

        Map<ZonedDateTime, List<Bar>> barDataRun = Utils.getBarData(barSeries);
        ArrayList<ZonedDateTime> dataSet = new ArrayList<>(barDataRun.keySet());

        dataSet.forEach(
                a -> {
                    int index = dataSet.indexOf(a);
                    if (index < 2) return;

                    List<Bar> workData = barDataRun.get(a);
                    List<Bar> workDataPrevOneDay = barDataRun.get(dataSet.get(index-1));
                    List<Bar> workDataPrevTwoDay = barDataRun.get(dataSet.get(index-2));
                    Num previousDayHigh = Utils.getHighOfDay(workDataPrevOneDay);
                    Num previousTwoDayHigh = Utils.getHighOfDay(workDataPrevTwoDay);
                    Num prevHighVal = previousDayHigh.isGreaterThan(previousTwoDayHigh) ? previousDayHigh : previousTwoDayHigh;

                    for (int i=0; i < workData.size(); i++) {
                        Bar bar = workData.get(i);
                        if (bar.isBearish() && bar.getClosePrice().isLessThan(prevHighVal)
                                && bar.getHighPrice().isGreaterThan(prevHighVal)
                                && bar.getEndTime().getHour() < 12
                                && (bar.getHighPrice().minus(bar.getLowPrice())
                                .multipliedBy(DecimalNum.valueOf(100).dividedBy(bar.getOpenPrice()))
                                .isLessThanOrEqual(DecimalNum.valueOf(0.6)))) {
                            StrategyExecutor.counter.merge(scripName, 1, Integer::sum);

                            BarSeries series =  new BaseBarSeries("bars");
                            workDataPrevTwoDay.forEach(w -> series.addBar(w));
                            workDataPrevOneDay.forEach(w -> series.addBar(w));
                            workData.forEach(w -> series.addBar(w));

                            boolean tradeResult = calculateTradeTypeCandle((i==0) ? null : workData.get(i-1),
                                    bar, workData.subList(++i, workData.size()), series);
                        }
                    }
                }
        );

    }

    private boolean calculateTradeTypeCandle(Bar prevBar, Bar currentBar, List<Bar> subList, BarSeries series) {
        Num highPrice = currentBar.getHighPrice();
        if (prevBar != null) {
            highPrice = (prevBar.getHighPrice().isGreaterThan(
                    currentBar.getHighPrice())) ? prevBar.getHighPrice() : currentBar.getHighPrice();
        }
        Num profitTarget = currentBar.getClosePrice()
                .minus((highPrice.minus(currentBar.getLowPrice())).multipliedBy(DecimalNum.valueOf(1)));
        Num stopLoss = highPrice.plus(DecimalNum.valueOf(0.5).multipliedBy(highPrice.dividedBy(DecimalNum.valueOf(100))));

        boolean result = false;
        Bar b = null;
        for (Bar bar : subList) {
            b = bar;
            if (bar.getHighPrice().isGreaterThanOrEqual(stopLoss)) {
                StrategyExecutor.updateLoss(scripName, stopLoss);
                StrategyExecutor.lossCount.merge(scripName, 1, Integer::sum);
                result = false;
                break;
            } else if (bar.getLowPrice().isLessThanOrEqual(profitTarget)){
                StrategyExecutor.updateProfit(scripName, profitTarget);
                StrategyExecutor.successCount.merge(scripName, 1, Integer::sum);
                result = true;
                break;
            } else if (bar.getEndTime().getHour() == 15 && bar.getEndTime().getMinute() >= 20) {
                if (bar.getClosePrice().isLessThanOrEqual(currentBar.getClosePrice())) {
                    StrategyExecutor.updateProfit(scripName, (currentBar.getClosePrice()).minus(bar.getClosePrice()));
                    StrategyExecutor.successCount.merge(scripName, 1, Integer::sum);
                    result = true;
                    break;
                } else {
                    StrategyExecutor.updateLoss(scripName, bar.getClosePrice().minus(currentBar.getClosePrice()));
                    StrategyExecutor.lossCount.merge(scripName, 1, Integer::sum);
                    result = false;
                    break;
                }
            }
        }

        //if (result) {
            /*new JfreeCandlestickChart(profitTarget + " - " + currentBar.getClosePrice() + " - "+ stopLoss + " - "+ b.getEndTime()
                    ,result + " - " +scripName + "_"
                    + currentBar.getEndTime().getHour() + "_"
                    + currentBar.getEndTime().getMinute() + "_" + UUID.randomUUID().toString(), series);*/
       // }
        return result;
    }

/*
    private synchronized boolean calculateTradeTypePer(String scripName, Num closePrice, List<Bar> subList) {
        Num profitTarget = closePrice.minus(
                StrategyExecutor.profitPercentage.multipliedBy(closePrice).dividedBy(DecimalNum.valueOf(100.0)));
        Num stopLoss = closePrice.plus(
                StrategyExecutor.lossBracket.multipliedBy(closePrice).dividedBy(DecimalNum.valueOf(100.0)));

        for (Bar bar : subList) {
            if (bar.getHighPrice().isGreaterThanOrEqual(stopLoss)) {
                StrategyExecutor.updateLoss(stopLoss);
                StrategyExecutor.lossCount.getAndIncrement();
                //System.out.println("failed = " + bar);
                return false;
            } else if (bar.getLowPrice().isLessThanOrEqual(profitTarget)){
                StrategyExecutor.updateProfit(profitTarget);
                StrategyExecutor.successCount.getAndIncrement();
                //System.out.println("success = " + bar);
                return true;
            } else if (bar.getEndTime().getHour() == 15 && bar.getEndTime().getMinute() >= 20) {
                if (bar.getClosePrice().isLessThanOrEqual(closePrice)) {
                    StrategyExecutor.updateProfit(closePrice.minus(bar.getClosePrice()));
                    StrategyExecutor.successCount.getAndIncrement();
                    //System.out.println("profit at close = " + bar);
                    return true;
                } else {
                    StrategyExecutor.updateLoss(bar.getClosePrice().minus(closePrice));
                    StrategyExecutor.lossCount.getAndIncrement();
                    return false;
                }
            }
        }
        return false;
    }

*/

}
