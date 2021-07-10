package com.scrip.main.strategy.nr;

import org.ta4j.core.Indicator;
import org.ta4j.core.BarSeries;
import org.ta4j.core.TradingRecord;
import org.ta4j.core.num.Num;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.rules.AbstractRule;

public class NrIbIndicatorRule extends AbstractRule {

	private Indicator<Num> indicator;
	private Integer nrCount;

	public NrIbIndicatorRule(Indicator<Num> indicator, Integer nrCount) {
		this.indicator = indicator;
		this.nrCount = nrCount;
	}

	@Override
	public boolean isSatisfied(int index, TradingRecord tradingRecord) {
		BarSeries series = indicator.getBarSeries();
		
		if(index <= nrCount) {
			return false;
		}
		
		int count = 0;
		Num seriesMaxPrice = series.getBar(index).getHighPrice();
		Num seriesMinPrice = series.getBar(index).getLowPrice();

		for (int i = index - nrCount -1; i < index; i++) {
			if ((series.getBar(index).getHighPrice().minus(series.getBar(index).getLowPrice()).isLessThan(
					series.getBar(i).getHighPrice().minus(series.getBar(i).getLowPrice())))) {
				count++;

				if (series.getBar(i).getHighPrice().isGreaterThan(seriesMaxPrice)) {
					seriesMaxPrice = series.getBar(i).getHighPrice();
				}

				if (series.getBar(i).getLowPrice().isLessThan(seriesMinPrice)) {
					seriesMinPrice = series.getBar(i).getLowPrice();
				}
			}
		}

		boolean isInsideBar = false;
		if (series.getBar(index).getHighPrice().isLessThan(series.getBar(index - 1).getHighPrice())
				&& series.getBar(index).getLowPrice().isGreaterThan(series.getBar(index - 1).getLowPrice())) {
			isInsideBar = true;
		}
		
		Num ratioInsideBar = (series.getBar(index).getHighPrice().minus(series.getBar(index).getLowPrice()).multipliedBy(DecimalNum.valueOf(100))
				.dividedBy(series.getBar(index-1).getHighPrice().minus(series.getBar(index-1).getLowPrice())));
		
		if (count == 6 && isInsideBar && ratioInsideBar.isLessThanOrEqual(DecimalNum.valueOf(53))) {
			return true;
		} else {
			return false;
		}
		
	}

}
