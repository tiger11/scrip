package com.scrip.main.indicators;

import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.CachedIndicator;
import org.ta4j.core.indicators.helpers.TypicalPriceIndicator;
import org.ta4j.core.indicators.helpers.VolumeIndicator;
import org.ta4j.core.num.Num;

public class VWAPIndicatorCustom extends CachedIndicator<Num> {

	private final int barCount;

	private final Indicator<Num> typicalPrice;

	private final Indicator<Num> volume;

	private final Num ZERO;

	/**
	 * Constructor.
	 * 
	 * @param series   the series
	 * @param barCount the time frame
	 */
	public VWAPIndicatorCustom(BarSeries series, int barCount) {
		super(series);
		this.barCount = barCount;
		typicalPrice = new TypicalPriceIndicator(series);
		volume = new VolumeIndicator(series);
		this.ZERO = numOf(0);
	}

	@Override
	protected Num calculate(int index) {
		boolean dateSwitch = false;

		int barCountCurrDay = 0;
		String systemBarCount = System.getProperty("barCount");
		if (systemBarCount != null) {
			barCountCurrDay = Integer.valueOf(systemBarCount);
		}

		if (index == 0 || isDateSwitch(index)) {
			dateSwitch = true;
			barCountCurrDay = getBarCount(index);
			System.setProperty("currStartIndex", String.valueOf(index));
		}

		if (barCountCurrDay == 0) {
			System.out.println("Bar count 0. Exit!");
			System.exit(0);
		}

		if (index <= 0 || dateSwitch) {
			return typicalPrice.getValue(index);
		}

		int startIndex = Integer.valueOf(System.getProperty("currStartIndex"));

		Num cumulativeTPV = ZERO;
		Num cumulativeVolume = ZERO;
		for (int i = startIndex; i <= index; i++) {
			Num currentVolume = volume.getValue(i);
			cumulativeTPV = cumulativeTPV.plus(typicalPrice.getValue(i).multipliedBy(currentVolume));
			cumulativeVolume = cumulativeVolume.plus(currentVolume);
		}
		return cumulativeTPV.dividedBy(cumulativeVolume);
	}

	private int getBarCount(int index) {
		int currDay = super.getBarSeries().getBar(index).getBeginTime().getDayOfYear();
		int barCountCurrDay = 0;

		while (super.getBarSeries().getBar(index).getBeginTime().getDayOfYear() == currDay) {
			barCountCurrDay++;
			index++;
			if (super.getBarSeries().getBarCount() == index + 1) {
				break;
			}
		}
		System.setProperty("barCount", String.valueOf(barCountCurrDay));
		return barCountCurrDay;
	}

	private boolean isDateSwitch(int index) {
		int currDay = super.getBarSeries().getBar(index).getBeginTime().getDayOfYear();
		int prevDay = super.getBarSeries().getBar(index - 1).getBeginTime().getDayOfYear();

		if (currDay != prevDay) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + " barCount: " + barCount;
	}
}
