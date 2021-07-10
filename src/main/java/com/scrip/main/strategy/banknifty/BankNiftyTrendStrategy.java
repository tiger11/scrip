package com.scrip.main.strategy.banknifty;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.DecimalNum;

import com.scrip.main.util.CsvTimeSeries;

public class BankNiftyTrendStrategy {
	
	private static final Num lossCriteria = DecimalNum.valueOf(100);
	private static final Num winCriteria = DecimalNum.valueOf(150);
	private static final Num partialWinCriteria = DecimalNum.valueOf(150);

	public static void main(String[] args) {
		int winTrades = 0;
		int lossTrades = 0;
		
		BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\dataIndices\\NIFTY BANK\\15minute\\data.csv");
		int barCount = series.getBarCount();
		int count = 0;
		for (int i = 0; i < barCount; i++) {
			if (series.getBar(i).getBeginTime().getHour() == 9 && series.getBar(i).getBeginTime().getMinute() == 15) {
				if (is15MinsBreakDown(series, i)) {
					boolean isWinTrade = isWinTradeBreakDown(series, i);
					System.out.println("breakDown" +series.getBar(i).toString() + " result: "+isWinTrade);
					if(isWinTrade) {
						winTrades++;
					}else {
						lossTrades++;
					}
				}else if (is15MinsBreakOut(series, i)) {
					System.out.println("breakOut" +series.getBar(i).toString());
				}
			}
		}
		System.out.println("Win Trades : "+ winTrades +"- Loss Trades : "+ lossTrades);
	}
	
	private static boolean isWinTradeBreakDown(BarSeries series, int barIndex) {
		int tradeDay = series.getBar(barIndex).getBeginTime().getDayOfYear();
		Num stopLessTarget = series.getBar(barIndex).getHighPrice().plus(DecimalNum.valueOf(10));
		Num target = series.getBar(barIndex + 2).getClosePrice().minus(winCriteria);
		
		int dayTracker = tradeDay;
		int startIndex = barIndex + 3;
		while(dayTracker == tradeDay) {
			if(series.getBar(startIndex).getLowPrice().isLessThan(target)) {
				return true;
			}else if(series.getBar(startIndex).getHighPrice().isGreaterThan(stopLessTarget)) {
				return false;
			}
			startIndex++;
			dayTracker = series.getBar(startIndex).getBeginTime().getDayOfYear();
		}
		
		return false;
	}
	
	private static boolean is15MinsBreakOut(BarSeries series, int barIndex) {
		if (isRedCandle(series.getBar(barIndex))) {
			if (isGreenCandle(series.getBar(barIndex + 1))) {
				if (series.getBar(barIndex).getHighPrice().isLessThan(series.getBar(barIndex + 2).getClosePrice())
						&& series.getBar(barIndex + 1).getClosePrice()
								.isLessThan(series.getBar(barIndex + 2).getClosePrice())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean is15MinsBreakDown(BarSeries series, int barIndex) {
		if (isGreenCandle(series.getBar(barIndex))) {
			if (isRedCandle(series.getBar(barIndex + 1))) {
				if (series.getBar(barIndex).getLowPrice().isGreaterThan(series.getBar(barIndex + 2).getClosePrice())
						&& series.getBar(barIndex + 1).getClosePrice()
								.isGreaterThan(series.getBar(barIndex + 2).getClosePrice())) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean isGreenCandle(Bar bar) {
		if (bar.getClosePrice().isGreaterThan(bar.getOpenPrice())) {
			return true;
		}
		return false;
	}

	private static boolean isRedCandle(Bar bar) {
		if (bar.getClosePrice().isLessThanOrEqual(bar.getOpenPrice())) {
			return true;
		}
		return false;
	}

}
