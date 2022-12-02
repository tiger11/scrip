package com.scrip.live;

import com.scrip.main.pojo.Symbol;
import com.scrip.main.util.Util;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.Tick;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.Num;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class ScripProcessor {

    public static void main(String[] args) throws Exception {
        System.out.println("Starting processing...");
        KiteConnect kiteConnect = Util.getKiteConnect();
        List<Symbol> symbolList = Util.getSymbolList();
        ConcurrentHashMap<String, BarSeries> allBars = new ConcurrentHashMap<>();
        BlockingQueue<ArrayList<Tick>> tickQueue = new LinkedBlockingDeque<>();

        //Helper.updatePrevHigh(symbolList);
        HashMap<String, Num> previousHighs =  Helper.getPreviousHigh(symbolList);
        Helper.fetchAllBarsTemplate(symbolList, allBars);

        ScripDataProvider provider = new ScripDataProvider(symbolList, kiteConnect, tickQueue);
        DataProcessor consumer = new DataProcessor(symbolList, kiteConnect, tickQueue, allBars);
        StrategyExecutor strategyExecutor = new StrategyExecutor(symbolList, kiteConnect, allBars, previousHighs);

        Thread providerThread = new Thread(provider);
        Thread consumerThread = new Thread(consumer);

        providerThread.start();
        consumerThread.start();

        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime executeAt = now.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(5 + (5 * (now.getMinute() / 5))).plusSeconds(2);
        long initialDelay = Duration.between(now, executeAt).getSeconds();

        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        System.out.println("Strategy execute at = " + executeAt);
        executor.scheduleAtFixedRate(strategyExecutor, initialDelay, 300, TimeUnit.SECONDS);

        System.out.println("Exiting Main Thread...");
    }

}
