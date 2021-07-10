package com.scrip.main.strategy.nr;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import com.neovisionaries.ws.client.WebSocketException;
import com.scrip.main.util.Util;
import com.zerodhatech.kiteconnect.KiteConnect;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.Order;
import com.zerodhatech.models.Tick;
import com.zerodhatech.ticker.KiteTicker;
import com.zerodhatech.ticker.OnConnect;
import com.zerodhatech.ticker.OnDisconnect;
import com.zerodhatech.ticker.OnError;
import com.zerodhatech.ticker.OnOrderUpdate;
import com.zerodhatech.ticker.OnTicks;

public class NR7StrategyLive {

	public static void main(String[] args) throws IOException, WebSocketException, KiteException {
		
		KiteConnect kiteConnect = Util.getKiteConnect();
		
		NR7StrategyLive liveObj = new NR7StrategyLive();
		ArrayList<Long> tokens = new ArrayList<>();
		tokens.add(Long.parseLong("54803207"));
		liveObj.tickerUsage(kiteConnect, tokens);

	}

	/**
	 * Demonstrates com.zerodhatech.ticker connection, subcribing for instruments,
	 * unsubscribing for instruments, set mode of tick data, com.zerodhatech.ticker
	 * disconnection
	 */
	public void tickerUsage(KiteConnect kiteConnect, ArrayList<Long> tokens)
			throws IOException, WebSocketException, KiteException {
		/**
		 * To get live price use websocket connection. It is recommended to use only one
		 * websocket connection at any point of time and make sure you stop connection,
		 * once user goes out of app. custom url points to new endpoint which can be
		 * used till complete Kite Connect 3 migration is done.
		 */
		final KiteTicker tickerProvider = new KiteTicker(kiteConnect.getAccessToken(), kiteConnect.getApiKey());

		tickerProvider.setOnConnectedListener(new OnConnect() {
			@Override
			public void onConnected() {
				/**
				 * Subscribe ticks for token. By default, all tokens are subscribed for
				 * modeQuote.
				 * 
				 */
				tickerProvider.subscribe(tokens);
				tickerProvider.setMode(tokens, KiteTicker.modeFull);
			}
		});

		tickerProvider.setOnDisconnectedListener(new OnDisconnect() {
			@Override
			public void onDisconnected() {
				// your code goes here
			}
		});

		/** Set listener to get order updates. */
		tickerProvider.setOnOrderUpdateListener(new OnOrderUpdate() {
			@Override
			public void onOrderUpdate(Order order) {
				System.out.println("order update " + order.orderId);
			}
		});

		/** Set error listener to listen to errors. */
		tickerProvider.setOnErrorListener(new OnError() {
			@Override
			public void onError(Exception exception) {
				// handle here.
			}

			@Override
			public void onError(KiteException kiteException) {
				// handle here.
			}

			@Override
			public void onError(String error) {
				System.out.println(error);
			}
		});

		tickerProvider.setOnTickerArrivalListener(new OnTicks() {
			@Override
			public void onTicks(ArrayList<Tick> ticks) {
				NumberFormat formatter = new DecimalFormat();
				System.out.println("ticks size " + ticks.size());
				if (ticks.size() > 0) {
					System.out.println("last price " + ticks.get(0).getLastTradedPrice());
					System.out.println("open interest " + formatter.format(ticks.get(0).getOi()));
					System.out.println("day high OI " + formatter.format(ticks.get(0).getOpenInterestDayHigh()));
					System.out.println("day low OI " + formatter.format(ticks.get(0).getOpenInterestDayLow()));
					System.out.println("change " + formatter.format(ticks.get(0).getChange()));
					System.out.println("tick timestamp " + ticks.get(0).getTickTimestamp());
					System.out.println("tick timestamp date " + ticks.get(0).getTickTimestamp());
					System.out.println("last traded time " + ticks.get(0).getLastTradedTime());
					System.out.println(ticks.get(0).getMarketDepth().get("buy").size());
				}
			}
		});
		// Make sure this is called before calling connect.
		tickerProvider.setTryReconnection(true);
		// maximum retries and should be greater than 0
		tickerProvider.setMaximumRetries(10);
		// set maximum retry interval in seconds
		tickerProvider.setMaximumRetryInterval(30);

		/**
		 * connects to com.zerodhatech.com.zerodhatech.ticker server for getting live
		 * quotes
		 */
		tickerProvider.connect();

		/**
		 * You can check, if websocket connection is open or not using the following
		 * method.
		 */
		boolean isConnected = tickerProvider.isConnectionOpen();
		System.out.println(isConnected);

		/**
		 * set mode is used to set mode in which you need tick for list of tokens.
		 * Ticker allows three modes, modeFull, modeQuote, modeLTP. For getting only
		 * last traded price, use modeLTP For getting last traded price, last traded
		 * quantity, average price, volume traded today, total sell quantity and total
		 * buy quantity, open, high, low, close, change, use modeQuote For getting all
		 * data with depth, use modeFull
		 */
		tickerProvider.setMode(tokens, KiteTicker.modeLTP);

		// Unsubscribe for a token.
		//tickerProvider.unsubscribe(tokens);

		// After using com.zerodhatech.com.zerodhatech.ticker, close websocket
		// connection.
		//tickerProvider.disconnect();
	}

}
