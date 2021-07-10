package com.scrip.main.strategy.nr;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Month;
import java.util.List;

import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import com.scrip.main.pojo.Symbol;
import com.scrip.main.util.Constants;
import com.scrip.main.util.CsvTimeSeries;
import com.scrip.main.util.Util;

public class NR7StrategyOld {

	private static int tardeCount = 0;

	public static void main(String[] args) {
		List<Symbol> getSymbolList = null;
		try {
			getSymbolList = Util.getSymbolList();
		} catch (Exception e) {
			e.printStackTrace();
		}

		for (Symbol s : getSymbolList) {
			String scripLocation = Constants.rootDirectory + "\\" + s.getTradingSymbol() + "\\" + "day\\data.csv";
			Path folderPath = Paths.get(scripLocation);
			if(Files.exists(folderPath)) {
				System.out.println("Symbol : " + s.getTradingSymbol());
				BarSeries series = CsvTimeSeries.csvTimeSeries(scripLocation);
				findNrSeven(series, s.getTradingSymbol());
			}else {
				System.out.println("Symbol doesnt exist : " + s.getTradingSymbol()); 
			}
		}
		System.out.println("Total signal : " + tardeCount);
	}

	private static void findNrSeven(BarSeries series, String scripName) {

		for (int i = 7; i < series.getBarCount(); i++) {
			if (series.getBar(i).getBeginTime().getDayOfMonth() == 15 && series.getBar(i).getBeginTime().getYear() == 2019 && series.getBar(i).getBeginTime().getMonth() == Month.SEPTEMBER && isNrSeven(series, i)) {
			if (isNrSeven(series, i)) {
				tardeCount++;
				System.out.println(series.getBar(i).toString());
			}
		}
		}
	}

	private static boolean isNrSeven(BarSeries series, int index) {
		int count = 0;
		Num seriesMaxPrice = series.getBar(index).getHighPrice();
		Num seriesMinPrice = series.getBar(index).getLowPrice();

		if (series.getBarCount() - 1 == index) {
			return false;
		}

		for (int i = 1; i <= 6; i++) {
			if ((series.getBar(index).getHighPrice().minus(series.getBar(index).getLowPrice()).isLessThan(
					series.getBar(index - i).getHighPrice().minus(series.getBar(index - i).getLowPrice())))) {
				count++;

				if (series.getBar(index - i).getHighPrice().isGreaterThan(seriesMaxPrice)) {
					seriesMaxPrice = series.getBar(index - i).getHighPrice();
				}

				if (series.getBar(index - i).getLowPrice().isLessThan(seriesMinPrice)) {
					seriesMinPrice = series.getBar(index - i).getLowPrice();
				}
			}
		}

		boolean isInsideBar = false;
		if (series.getBar(index).getHighPrice().isLessThan(series.getBar(index - 1).getHighPrice())
				&& series.getBar(index).getLowPrice().isGreaterThan(series.getBar(index - 1).getLowPrice())) {
			isInsideBar = true;
		}

		/*if (count == 6 && isInsideBar && (series.getBar(index + 1).getHighPrice().isGreaterThan(seriesMaxPrice)
				|| series.getBar(index + 1).getLowPrice().isLessThan(seriesMinPrice))) {*/
		if (count == 6 && isInsideBar && (series.getBar(index + 1).getHighPrice().isGreaterThan(seriesMaxPrice))) {
			return true;
		} else {
			return false;
		}
	}

}
