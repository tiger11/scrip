package com.scrip.live;

import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.Tick;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.DoubleNum;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class DataProcessor extends Scrip {
    List<Symbol> symbols;
    private BlockingQueue<ArrayList<Tick>> tickQueue;
    private ArrayList<Long> tokens;
    private ConcurrentHashMap<String, BarSeries> allBars;

    public DataProcessor(List<Symbol> symbols, KiteConnect kiteConnect,
                         BlockingQueue<ArrayList<Tick>> tickQueue, ConcurrentHashMap<String, BarSeries> allBars) {
        super(symbols, kiteConnect, tickQueue);
        this.allBars = allBars;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                ArrayList<Tick> getTicksFromQueue = getTicksFromQueue();
                if (!getTicksFromQueue.isEmpty()) {
                    //System.out.println("getTicksFromQueue.size() = " + getTicksFromQueue.size());
                    getTicksFromQueue.stream().parallel().forEach(
                            o -> {
                                String token = String.valueOf(o.getInstrumentToken());
                                double lastTradedPrice = o.getLastTradedPrice();

                                Date dateTimeStamp = null;
                                if (null != o.getTickTimestamp()) {
                                    dateTimeStamp = o.getTickTimestamp();
                                } else {
                                    dateTimeStamp = o.getLastTradedTime();
                                }
                                ZonedDateTime timeStamp = ZonedDateTime.ofInstant(dateTimeStamp.toInstant(),
                                        ZoneId.systemDefault());
                                BarSeries barSeries = allBars.get(token);

                                if (barSeries.isEmpty() || timeStamp.isAfter(barSeries.getLastBar().getEndTime())) {
                                    ZonedDateTime endTime = timeStamp.truncatedTo(ChronoUnit.HOURS)
                                            .plusMinutes(5 + (5 * (timeStamp.getMinute() / 5)));
                                    Bar bar = BaseBar.builder(DecimalNum::valueOf, Double.class)
                                            .timePeriod(Duration.ofMinutes(5))
                                            .openPrice(lastTradedPrice)
                                            .highPrice(lastTradedPrice)
                                            .lowPrice(lastTradedPrice)
                                            .closePrice(lastTradedPrice)
                                            .endTime(endTime)
                                            .volume(DecimalNum.valueOf(0))
                                            .build();
                                    barSeries.addBar(bar);
                                }
                                barSeries.addPrice(lastTradedPrice);
                                allBars.put(token, barSeries);
                            }
                    );
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
