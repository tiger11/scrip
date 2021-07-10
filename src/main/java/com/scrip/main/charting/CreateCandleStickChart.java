package com.scrip.main.charting;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CombinedDomainXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.time.Minute;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.ta4j.core.Bar;

public class CreateCandleStickChart {
/*

	public String getChart(org.ta4j.core.TimeSeries barseries, String chartName) {
		JFreeChart chart = createChart(barseries, chartName);
		try {
			ChartUtilities.saveChartAsJPEG(new File(chartName + ".jpeg"), chart, 800, 600);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return chartName + ".jpeg";
	}

	private JFreeChart createChart(org.ta4j.core.TimeSeries barseries, String chartTitle) {

		OHLCSeriesCollection candlestickDataset = new OHLCSeriesCollection();
		OHLCSeries ohlcSeries = new OHLCSeries("Price");
		candlestickDataset.addSeries(ohlcSeries);
		// Create candlestick chart priceAxis
		NumberAxis priceAxis = new NumberAxis("Price");
		priceAxis.setAutoRangeIncludesZero(false);
		// Create candlestick chart renderer
		CandlestickRenderer candlestickRenderer = new CandlestickRenderer(CandlestickRenderer.WIDTHMETHOD_AVERAGE,
				false, new CustomHighLowItemLabelGenerator(new SimpleDateFormat(":mm"), new DecimalFormat("0.000")));
		// Create candlestickSubplot
		XYPlot candlestickSubplot = new XYPlot(candlestickDataset, null, priceAxis, candlestickRenderer);
		candlestickSubplot.setBackgroundPaint(Color.white);

		*/
/**
		 * Creating volume subplot
		 *//*

		// creates TimeSeriesCollection as a volume dataset for volume chart
		TimeSeriesCollection volumeDataset = new TimeSeriesCollection();
		TimeSeries volumeSeries = new TimeSeries("Volume");
		volumeDataset.addSeries(volumeSeries);
		// Create volume chart volumeAxis
		NumberAxis volumeAxis = new NumberAxis("Volume");
		volumeAxis.setAutoRangeIncludesZero(false);
		// Set to no decimal
		volumeAxis.setNumberFormatOverride(new DecimalFormat("0"));
		// Create volume chart renderer

		for (int i = barseries.getBarCount() - 50; i < barseries.getBarCount(); i++) {
			Bar bar = barseries.getBar(i);
			//RegularTimePeriod period = new Minute(new Date(bar.getBeginTime().toInstant().toEpochMilli()));
			Date dt = new Date();
			dt.setMinutes(-i);
			RegularTimePeriod period = new Minute(dt);
			ohlcSeries.add(period, bar.getOpenPrice().doubleValue(), bar.getMaxPrice().doubleValue(),
					bar.getMinPrice().doubleValue(), bar.getClosePrice().doubleValue());
			volumeSeries.add(period, bar.getVolume().longValue());
		}

		XYBarRenderer timeRenderer = new XYBarRenderer();
		timeRenderer.setShadowVisible(false);
		timeRenderer.setBaseToolTipGenerator(new StandardXYToolTipGenerator("Volume--> Time={1} Size={2}",
				new SimpleDateFormat(":mm"), new DecimalFormat("0")));
		// Create volumeSubplot
		XYPlot volumeSubplot = new XYPlot(volumeDataset, null, volumeAxis, timeRenderer);
		volumeSubplot.setBackgroundPaint(Color.white);

		*/
/**
		 * Create chart main plot with two subplots (candlestickSubplot, volumeSubplot)
		 * and one common dateAxis
		 *//*

		// Creating charts common dateAxis
		DateAxis dateAxis = new DateAxis("Time");
		dateAxis.setDateFormatOverride(new SimpleDateFormat(":mm"));
		// reduce the default left/right margin from 0.05 to 0.02
		dateAxis.setLowerMargin(0.02);
		dateAxis.setUpperMargin(0.02);
		// Create mainPlot
		CombinedDomainXYPlot mainPlot = new CombinedDomainXYPlot(dateAxis);
		mainPlot.setGap(10.0);

		mainPlot.add(candlestickSubplot, 3);
		mainPlot.add(volumeSubplot, 1);
		mainPlot.setOrientation(PlotOrientation.VERTICAL);

		JFreeChart chart = new JFreeChart(chartTitle, JFreeChart.DEFAULT_TITLE_FONT, mainPlot, true);

		chart.removeLegend();
		return chart;
	}

*/
}
