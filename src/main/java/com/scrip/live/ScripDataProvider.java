package com.scrip.live;

import com.scrip.main.pojo.Symbol;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.*;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

public class ScripDataProvider extends Scrip {

    public ScripDataProvider(List<Symbol> symbols, KiteConnect kiteConnect,
                             BlockingQueue<ArrayList<Tick>> tickQueue) {
        super(symbols, kiteConnect, tickQueue);
    }

    @Override
    public void run() {
        KiteTicker tickerProvider = new KiteTicker(
                getKiteConnect().getAccessToken(), getKiteConnect().getApiKey());

        tickerProvider.setOnConnectedListener(new OnConnect() {
            @Override
            public void onConnected() {
                /** Subscribe ticks for token.
                 * By default, all tokens are subscribed for modeQuote.
                 * */
                tickerProvider.subscribe(getTokens());
                tickerProvider.setMode(getTokens(), KiteTicker.modeFull);
            }
        });

        tickerProvider.setOnTickerArrivalListener(new OnTicks() {
            @Override
            public void onTicks(ArrayList<Tick> ticks) {
                NumberFormat formatter = new DecimalFormat();
                if(ticks.size() > 0) {
                    addTicksToQueue(ticks);
                }
            }
        });

        tickerProvider.setTryReconnection(true);
        try {
            tickerProvider.setMaximumRetries(10);
            tickerProvider.setMaximumRetryInterval(30);
        } catch (KiteException e) {
            e.printStackTrace();
        }
        tickerProvider.connect();
        boolean isConnected = tickerProvider.isConnectionOpen();
        System.out.println(isConnected);

        /** set mode is used to set mode in which you need tick for list of tokens.
         * Ticker allows three modes, modeFull, modeQuote, modeLTP.
         * For getting only last traded price, use modeLTP
         * For getting last traded price, last traded quantity, average price, volume traded today, total sell quantity and total buy quantity, open, high, low, close, change, use modeQuote
         * For getting all data with depth, use modeFull*/
        tickerProvider.setMode(getTokens(), KiteTicker.modeLTP);
    }
}
