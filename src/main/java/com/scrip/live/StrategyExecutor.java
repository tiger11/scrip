package com.scrip.live;

import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.KiteConnect;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StrategyExecutor implements Runnable{

    List<Symbol> symbols;
    KiteConnect kiteConnect;
    ConcurrentHashMap<String, BarSeries> allBars;
    HashMap<String, Num> previousHighs;

    public StrategyExecutor(List<Symbol> symbols, KiteConnect kiteConnect,
                            ConcurrentHashMap<String, BarSeries> allBars,
                            HashMap<String, Num> previousHighs){
        this.allBars = allBars;
        this.kiteConnect = kiteConnect;
        this.symbols = symbols;
        this.previousHighs = previousHighs;
    }

    @Override
    public void run() {
        System.out.println("Strategy Executing at  = " + ZonedDateTime.now());
        for (String token :
                allBars.keySet()) {
            BarSeries barSeries = allBars.get(token);

            if (!barSeries.isEmpty()) {
                Bar lastBar = barSeries.getLastBar();
                if (lastBar.getEndTime().isAfter(ZonedDateTime.now())) {
                    lastBar = barSeries.getBar(barSeries.getEndIndex() - 1);
                }
                Num barPrevHighPrice = previousHighs.get(token);
                if (lastBar.getHighPrice().isGreaterThan(barPrevHighPrice) &&
                        lastBar.getClosePrice().isLessThanOrEqual(barPrevHighPrice)) {
                    System.out.println("barPrevHighPrice = " + barPrevHighPrice
                            + "  lastBar = " + lastBar.getClosePrice()
                            + " token = " + token
                            + " scrip = " + symbols.stream()
                                .filter(o -> o.getInstrumentToken().equals(token)).findFirst().get().getTradingSymbol());
                }
            }
        }
    }
}
