package com.scrip.main.strategy.live;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.ta4j.core.BarSeries;
import org.ta4j.core.BarSeries;

public class StrategyLiveHelper {
	
	public static Map<String, BarSeries> map15Min = new HashMap<String, BarSeries>();
	public static Map<String, BarSeries> map30Min = new HashMap<String, BarSeries>();
	public static Map<String, BarSeries> map60Min = new HashMap<String, BarSeries>();
	
	public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");

}
