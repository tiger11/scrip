package com.scrip.live;


import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.models.Tick;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class DataProcessor extends Scrip{
    List<Symbol> symbols;
    private BlockingQueue<ArrayList<Tick>> tickQueue;
    private ArrayList<Long> tokens;

    public DataProcessor(List<Symbol> symbols, KiteConnect kiteConnect,
                         BlockingQueue<ArrayList<Tick>> tickQueue) {
        super(symbols, kiteConnect, tickQueue);
    }

    @Override
    public void run() {

    }
}
