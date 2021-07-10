package com.scrip.main.strategy.banknifty;

import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import com.scrip.main.util.CsvTimeSeries;

public class BankNiftyTrendStrategy1Level15 {
	
	private static final Num lossCriteria = DecimalNum.valueOf(30);
	private static final Num winCriteria = DecimalNum.valueOf(50);
	private static final Num partialWinCriteria = DecimalNum.valueOf(150);

	public static void main(String[] args) {
		int winTrades = 0;
		int lossTrades = 0;
		int neutralTrades = 0;
		
		BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\dataIndices\\NIFTY 50\\15minute\\data.csv");
		int barCount = series.getBarCount();
		int count = 0;
		for (int i = 0; i < barCount; i++) {
			if (series.getBar(i).getBeginTime().getHour() == 9 && series.getBar(i).getBeginTime().getMinute() == 15) {
				if (is15MinsBreakDown(series, i)) {
					Boolean isWinTrade = isWinTradeBreakDown(series, i);
					System.out.println("breakDown" +series.getBar(i).toString() + " result: "+isWinTrade);
					if(isWinTrade==null) {
						neutralTrades++;
					} else if(isWinTrade) {
						winTrades++;
					}else {
						lossTrades++;
					}
				}else if (is15MinsBreakOut(series, i)) {
					Boolean isWinTrade = isWinTradeBreakOut(series, i);
					System.out.println("breakOut" +series.getBar(i).toString()+ " result: "+isWinTrade);
					if(isWinTrade==null) {
						neutralTrades++;
					} else if(isWinTrade) {
						winTrades++;
					}else {
						lossTrades++;
					}
				}
			}
		}
		System.out.println("Win Trades : "+ winTrades +"- Loss Trades : "+ lossTrades +"- Neutral Trades : "+ neutralTrades);
	}
	
	private static Boolean isWinTradeBreakDown(BarSeries series, int barIndex) {
		int tradeDay = series.getBar(barIndex).getBeginTime().getDayOfYear();
		//Num stopLessTarget = series.getBar(barIndex).getMaxPrice().plus(PrecisionNum.valueOf(10));
		Num stopLessTarget = series.getBar(barIndex + 1).getClosePrice().plus(lossCriteria);
		Num target = series.getBar(barIndex + 1).getClosePrice().minus(winCriteria);
		
		int dayTracker = tradeDay;
		int startIndex = barIndex + 2;
		while(dayTracker == tradeDay) {
			if(series.getBar(startIndex).getLowPrice().isLessThan(target)) {
				return true;
			}else if(series.getBar(startIndex).getHighPrice().isGreaterThan(stopLessTarget)) {
				return false;
			}
			startIndex++;
			dayTracker = series.getBar(startIndex).getBeginTime().getDayOfYear();
		}
		
		return null;
	}
	
	private static Boolean isWinTradeBreakOut(BarSeries series, int barIndex) {
		int tradeDay = series.getBar(barIndex).getBeginTime().getDayOfYear();
		//Num stopLessTarget = series.getBar(barIndex).getMinPrice().minus(PrecisionNum.valueOf(10));
		Num stopLessTarget = series.getBar(barIndex + 1).getClosePrice().minus(lossCriteria);
		Num target = series.getBar(barIndex + 1).getClosePrice().plus(winCriteria);
		
		int dayTracker = tradeDay;
		int startIndex = barIndex + 2;
		while(dayTracker == tradeDay) {
			if(series.getBar(startIndex).getHighPrice().isGreaterThan(target)) {
				return true;
			}else if(series.getBar(startIndex).getLowPrice().isLessThan(stopLessTarget)) {
				return false;
			}
			startIndex++;
			dayTracker = series.getBar(startIndex).getBeginTime().getDayOfYear();
		}
		
		return null;
	}
	
	private static boolean is15MinsBreakOut(BarSeries series, int barIndex) {
		if (isRedCandle(series.getBar(barIndex))) {
			if (isGreenCandle(series.getBar(barIndex + 1))) {
				if (series.getBar(barIndex).getHighPrice().isLessThan(series.getBar(barIndex + 1).getClosePrice())) {
					return true;
				}
			}
		}
		return false;
	}
	
	private static boolean is15MinsBreakDown(BarSeries series, int barIndex) {
		if (isGreenCandle(series.getBar(barIndex))) {
			if (isRedCandle(series.getBar(barIndex + 1))) {
				if (series.getBar(barIndex).getLowPrice().isGreaterThan(series.getBar(barIndex + 1).getClosePrice())) {
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
