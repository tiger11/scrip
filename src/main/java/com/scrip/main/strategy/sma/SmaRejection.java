package com.scrip.main.strategy.sma;

import java.util.List;

import org.ta4j.core.BarSeries;

import com.scrip.main.pojo.Symbol;
import com.scrip.main.util.Constants;
import com.scrip.main.util.CsvTimeSeries;
import com.scrip.main.util.Util;

public class SmaRejection {
	
	private static final Integer [] smaRejectionLevels = {5, 9, 15, 20, 30, 50, 100, 200};

	public static void main(String[] args) {
		List<Symbol> getSymbolList = null;
		try {
			getSymbolList = Util.getSymbolList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		for(Symbol s : getSymbolList) {
			String scripLocation = Constants.rootDirectory + "\\" + s.getTradingSymbol() + "\\" + "5minute\\data.csv";
			BarSeries series = CsvTimeSeries.csvTimeSeries(scripLocation);
			
			
			
		}
	}
	
	
}
