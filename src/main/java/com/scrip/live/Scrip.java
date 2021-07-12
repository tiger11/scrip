package com.scrip.live;

import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.Tick;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

public class Scrip implements Runnable {

    private List<Symbol> symbols;
    private KiteConnect kiteConnect;
    private BlockingQueue<ArrayList<Tick>> tickQueue;
    private ArrayList<Long> tokens;

    public Scrip(List<Symbol> symbols, KiteConnect kiteConnect,
                 BlockingQueue<ArrayList<Tick>> tickQueue){
        this.symbols = symbols;
        this.kiteConnect = kiteConnect;
        this.tickQueue = tickQueue;
        this.tokens = symbols.stream().map(o ->
                Long.valueOf(o.getExchangeToken()).longValue()).collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Symbol> getSymbols() {
        return symbols;
    }

    public void addTicksToQueue(ArrayList<Tick> ticks) {
        getTickQueue().add(ticks);
    }

    public ArrayList<Tick> getTicksFromQueue() throws InterruptedException {
        return getTickQueue().take();
    }

    public BlockingQueue<ArrayList<Tick>> getTickQueue() {
        return tickQueue;
    }

    public KiteConnect getKiteConnect() {
        return kiteConnect;
    }

    public ArrayList<Long> getTokens() {
        ArrayList<Long> names =
                symbols.stream()
                        .map(o -> Long.valueOf(o.getInstrumentToken()))
                        .collect(Collectors
                                .toCollection(ArrayList::new));
        return names;
    }

    @Override
    public void run() {

    }
}
