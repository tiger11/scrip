package com.scrip.main.strategy.nr;

import org.ta4j.core.Indicator;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;
import org.ta4j.core.rules.AbstractRule;

public class NrIbExitRule extends AbstractRule  {
	
	private Indicator<Num> indicator;
	private int slCandleCount;

	public NrIbExitRule(Indicator<Num> indicator, int slCandleCount) {
		this.indicator = indicator;
		this.slCandleCount = slCandleCount;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		
		return false;
	}
	
	

}
