package com.scrip.main.strategy.live;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimerTask;

import org.json.JSONException;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import com.scrip.main.pojo.Symbol;
import com.scrip.main.util.Util;
import com.zerodhatech.kiteconnect.kitehttp.exceptions.KiteException;
import com.zerodhatech.models.HistoricalData;

public class ScheduledDataFeeder extends TimerTask {

	@Override
	public void run() {
		System.out.println("Task running at : " + new Date());
		try {
		updateData("30minute");
		}catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Task completed at : " + new Date());
		System.out.println("=================================================");
	}
	
	private boolean isNrSeven(BarSeries series) {
		int count = 0;
		int endIndex = series.getEndIndex() - 1;
		
		Num seriesMaxPrice = series.getBar(endIndex).getHighPrice();
		Num seriesMinPrice = series.getBar(endIndex).getLowPrice();
		
		for (int i = endIndex - 6; i < endIndex; i++) {
			if ((series.getBar(endIndex).getHighPrice().minus(series.getBar(endIndex).getLowPrice()).isLessThan(
					series.getBar(i).getHighPrice().minus(series.getBar(i).getLowPrice())))) {
				count++;

				if (series.getBar(i).getHighPrice().isGreaterThan(seriesMaxPrice)) {
					seriesMaxPrice = series.getBar(i).getHighPrice();
				}

				if (series.getBar(i).getLowPrice().isLessThan(seriesMinPrice)) {
					seriesMinPrice = series.getBar(i).getLowPrice();
				}
			}
		}

		boolean isInsideBar = false;
		if (series.getBar(endIndex).getHighPrice().isLessThan(series.getBar(endIndex - 1).getHighPrice())
				&& series.getBar(endIndex).getLowPrice().isGreaterThan(series.getBar(endIndex - 1).getLowPrice())) {
			isInsideBar = true;
		}
		
		Num ratioInsideBar = (series.getBar(endIndex).getHighPrice().minus(series.getBar(endIndex).getLowPrice()).multipliedBy(DecimalNum.valueOf(100))
				.dividedBy(series.getBar(endIndex-1).getHighPrice().minus(series.getBar(endIndex-1).getLowPrice())));
		
		if (count == 6 && isInsideBar && ratioInsideBar.isLessThanOrEqual(DecimalNum.valueOf(53))) {
			return true;
		} else {
			return false;
		}
	}
	
	public void updateData(String duration) {
		List<Symbol> getSymbolList = null;
		try {
			getSymbolList = Util.getSymbolList();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Calendar cal = Calendar.getInstance();
		Date toDate = cal.getTime();
		Date fromDate = cal.getTime();
		fromDate.setDate(toDate.getDate() - 5);

		for (Symbol s : getSymbolList) {
			HistoricalData data = getScripData(fromDate, toDate, duration, s.getInstrumentToken(), s.getTradingSymbol(),
					false);
			BarSeries series = getTimeSeries(data);
			StrategyLiveHelper.map30Min.put(s.getTradingSymbol(), series);
			if(isNrSeven(series)) {
				System.out.println("Inside Bar : "+ s.getTradingSymbol());
				//new CreateCandleStickChart().getChart(series, s.getTradingSymbol() + " - " + duration);
			}
		}
	}

	private BarSeries getTimeSeries(HistoricalData dataSet) {
		BarSeries series = new BaseBarSeries("bars");

		for (HistoricalData data : dataSet.dataArrayList) {
			ZonedDateTime date = ZonedDateTime.parse(data.timeStamp, StrategyLiveHelper.DATE_FORMAT);
			series.addBar(date, data.open, data.high, data.low, data.close, data.volume);
		}

		return series;
	}

	private HistoricalData getScripData(Date fromDate, Date toDate, String interval, String instrumentToken,
			String instrumentName, boolean continuous) {

		if (fromDate.after(new Date())) {
			return null;
		}

		HistoricalData data = null;
		try {
			data = Util.getKiteConnect().getHistoricalData(fromDate, toDate, instrumentToken, interval, continuous, false);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (KiteException e) {
			e.printStackTrace();
		}

		return data;
	}
}
