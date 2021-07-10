package com.scrip.main.strategy.nr;

import org.ta4j.core.*;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;

import com.scrip.main.util.CsvTimeSeries;

public class NR7Strategy {

    public static Strategy buildStrategy(BarSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);/*
        Rule entryRule = new NrIbIndicatorRule(closePrice, 7);
        Rule exitRule = new StopLossRule(closePrice, PrecisionNum.valueOf(0.5))
        		.or(new StopGainRule(closePrice, PrecisionNum.valueOf(1.0)));*/
        
        //return new BaseStrategy(entryRule, exitRule);
        return null;

    }
	
	
    public static void main(String[] args) {

    	BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\ACC\\15minute\\data.csv");

        // Building the trading strategy
        Strategy strategy = buildStrategy(series);

        // Running the strategy
        BarSeriesManager seriesManager = new BarSeriesManager(series);
        TradingRecord tradingRecord = seriesManager.run(strategy);
        System.out.println("Number of trades for the strategy: " + tradingRecord.getPositionCount());

        // Analysis
        //System.out.println("Total profit for the strategy: " + new TotalProfitCriterion().calculate(series, tradingRecord));
    }

	
}
