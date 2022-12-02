package com.scrip.main.tic;

import com.scrip.main.pojo.Symbol;
import com.scrip.main.util.CsvTimeSeries;
import com.scrip.main.util.Util;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TicStrategy {

    private static final Num HUNDRED = DecimalNum.valueOf(100.0);
    private static int lossCount = 0;
    private static int profitCount = 0;
    private static int neutralCount = 0;
    private static Num loss = DecimalNum.valueOf(0);
    private static Num profit = DecimalNum.valueOf(0);

    private static Num investmentAmount = DecimalNum.valueOf(20000);


    public static void main(String[] args) {
        List<Symbol> symbols = Util.getSymbolList();
        for (Symbol s:
             symbols) {
            runStrategy(s.getTradingSymbol());
        }
        System.out.println("neutralCount = " + neutralCount);
        System.out.println("profitCount = " + profitCount);
        System.out.println("lossCount = " + lossCount);
        System.out.println("loss = " + loss);
        System.out.println("profit = " + profit);
    }

    public static void runStrategy(String scripName) {

        BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\"+scripName+"\\5minute\\data.csv");
        BarSeries seriesMin = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\"+scripName+"\\minute\\data.csv");
        List<Bar> barData = series.getBarData();
        List<Bar> barDataMin = seriesMin.getBarData();

        ClosePriceIndicator closePrice = new ClosePriceIndicator(seriesMin);
        EMAIndicator sma = new EMAIndicator(closePrice, 20);

        for (Bar b : barData) {
            Num oPrice = b.getOpenPrice();
            Num hPrice = b.getHighPrice();
            Num lPrice = b.getLowPrice();

            Num perChange = (hPrice.minus(lPrice)).dividedBy(oPrice).multipliedBy(HUNDRED);

            if (b.isBullish() && perChange.isGreaterThanOrEqual(DecimalNum.valueOf(0.75))) {
                Num prevDayHigh = prevDayHigh(barData, b.getEndTime());
                if (prevDayHigh != null && oPrice.isGreaterThan(prevDayHigh)) {
                    List<Bar> getMinuteBar = getMinuteBar(b, barDataMin);
                    for (Bar bMin : getMinuteBar) {
                        Num smaVal = getSmaVal(bMin, barDataMin, sma);
                        if (bMin.getLowPrice().isLessThanOrEqual(smaVal)) {
                            int bearishBars = checkPrevBarsForBears(bMin, barDataMin);
                            if (bearishBars >= 4) {
                                break;
                            }
                            List<Bar> minuteBarForTrade = getMinuteBarForTrade(bMin, barDataMin);
                            Num stopLossVal = smaVal.multipliedBy(DecimalNum.valueOf(1)).dividedBy(HUNDRED);
                            Num stopLoss = smaVal.minus(stopLossVal);
                            Num targetVal = smaVal.multipliedBy(DecimalNum.valueOf(1)).dividedBy(HUNDRED);
                            Num target = smaVal.plus(targetVal);
                            Num noOfShare = investmentAmount.dividedBy(smaVal).ceil();
                            ZonedDateTime tradeStartTime = bMin.getEndTime();
                            for (Bar bMinTrade : minuteBarForTrade) {
                                if (tradeStartTime.plusHours(3).isAfter(bMinTrade.getEndTime())) {
                                    stopLossVal = smaVal.multipliedBy(DecimalNum.valueOf(0.7)).dividedBy(HUNDRED);
                                    stopLoss = smaVal.minus(stopLossVal);
                                }
                                if (bMinTrade.getEndTime().getHour()>=11) {
                                    break;
                                }
                                if (bMinTrade.getLowPrice().isLessThanOrEqual(stopLoss)) {
                                    System.out.println("sma = " + smaVal);
                                    System.out.println("scripName = " + scripName);
                                    System.out.println("b = " + b);
                                    System.out.println("bMin = " + bMin);
                                    System.out.println("bMinTrade Loss= " + bMinTrade);
                                    System.out.println("stopLoss = " + stopLoss);
                                    System.out.println("target = " + target);
                                    System.out.println("================================ ");
                                    loss = loss.plus(stopLossVal.multipliedBy(noOfShare));
                                    lossCount++;
                                    break;
                                } else if (bMinTrade.getHighPrice().isGreaterThanOrEqual(target)) {
                                    System.out.println("sma = " + smaVal);
                                    System.out.println("scripName = " + scripName);
                                    System.out.println("b = " + b);
                                    System.out.println("bMin = " + bMin);
                                    System.out.println("bMinTrade Profit= " + bMinTrade);
                                    System.out.println("stopLoss = " + stopLoss);
                                    System.out.println("target = " + target);
                                    System.out.println("================================ ");
                                    profit = profit.plus(targetVal.multipliedBy(noOfShare));
                                    profitCount++;
                                    break;
                                } else if (bMinTrade.getEndTime().getHour()==3 && bMinTrade.getEndTime().getMinute()==29) {
                                    System.out.println("sma = " + smaVal);
                                    System.out.println("scripName = " + scripName);
                                    System.out.println("bMinTrade Neutral= " + bMinTrade);
                                    System.out.println("b = " + b);
                                    System.out.println("bMin = " + bMin);
                                    System.out.println("================================ ");
                                    neutralCount++;
                                    break;
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    private static int checkPrevBarsForBears(Bar startBar, List<Bar> barDataMin) {
        ZonedDateTime endTime = startBar.getEndTime();
        ZonedDateTime startTime = endTime.minusMinutes(6);

        List<Bar> barList = barDataMin.stream().filter(o -> o.getEndTime().isAfter(startTime) &&
                o.getEndTime().isBefore(endTime)).collect(Collectors.toList());
        int count = 0;
        for(Bar b : barList) {
            if (b.isBearish()) {
                count++;
            }
        }
        return  count;
    }

    private static List<Bar> getMinuteBarForTrade(Bar startBar, List<Bar> barDataMin) {
        ZonedDateTime startTime = startBar.getEndTime();
        ZonedDateTime endTime = startTime.withMinute(30).withHour(15);

        return barDataMin.stream().filter(o -> o.getEndTime().isAfter(startTime) &&
                o.getEndTime().isBefore(endTime)).collect(Collectors.toList());
    }

    private static Num getSmaVal(Bar bMin, List<Bar> barDataMin, EMAIndicator sma) {
        for (int i=0; i<barDataMin.size(); i++){
            if (barDataMin.get(i).equals(bMin)) {
                return sma.getValue(i);
            }
        }
        return null;
    }

    private static List<Bar> getMinuteBar(Bar startBar, List<Bar> barDataMin) {
        ZonedDateTime startTime = startBar.getEndTime().plusMinutes(4);
        ZonedDateTime endTime = startTime.withMinute(30).withHour(15);

        return barDataMin.stream().filter(o -> o.getEndTime().isAfter(startTime) &&
                o.getEndTime().isBefore(endTime)).collect(Collectors.toList());
    }

    private static Num prevDayHigh (List<Bar> barData, ZonedDateTime dateTime) {
        ZonedDateTime startTime = dateTime.minusDays(5).withMinute(00).withHour(9);
        ZonedDateTime endTime = dateTime.minusDays(1).withMinute(30).withHour(15);

        List<Bar> data = barData.stream().filter(o -> o.getEndTime().isAfter(startTime) &&
                o.getEndTime().isBefore(endTime)).collect(Collectors.toList());
        if (data.size() > 150) {
            data = data.subList(data.size()-150, data.size());
        }

        if (data.size() == 0) {
            return null;
        } else {
            return data.stream().max(Comparator.comparing(Bar::getHighPrice)).get().getHighPrice();
        }
    }

}
