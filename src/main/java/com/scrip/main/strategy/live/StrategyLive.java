package com.scrip.main.strategy.live;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StrategyLive {

	public static void main(String[] args) {
		
		ScheduledDataFeeder feeder15Mins = new ScheduledDataFeeder();
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
		service.scheduleAtFixedRate(feeder15Mins, 0, 15, TimeUnit.MINUTES);

	}

}