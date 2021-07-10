package com.scrip.main.indicators;

import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.num.Num;

/**
 * Returns the previous (n-th) value of an indicator
 * </p>
 */
public class PreviousDayCloseVwapIndicator extends CachedIndicator<Num> {

	private static final long serialVersionUID = -3974372066357537343L;
	private Indicator<Num> indicator;

	public PreviousDayCloseVwapIndicator(Indicator<Num> indicator) {
		super(indicator);
		this.indicator = indicator;
	}

	protected Num calculate(int index) {
		for (int i = index; i >= 0; i--) {
			if (isDateSwitch(index)) {
				return this.indicator.getValue(i-1);
			}
		}

		return this.indicator.getValue(0);
	}

	private boolean isDateSwitch(int index) {
		int currDay = super.getBarSeries().getBar(index).getBeginTime().getDayOfYear();
		int prevDay = super.getBarSeries().getBar(index - 1).getBeginTime().getDayOfYear();

		if (currDay != prevDay) {
			return true;
		}

		return false;
	}
}