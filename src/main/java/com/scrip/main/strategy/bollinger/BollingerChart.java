package com.scrip.main.strategy.bollinger;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.scrip.main.util.CsvTimeSeries;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.ApplicationFrame;
import org.jfree.data.time.*;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.EMAIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsLowerIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsMiddleIndicator;
import org.ta4j.core.indicators.bollinger.BollingerBandsUpperIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.statistics.StandardDeviationIndicator;
import org.ta4j.core.num.Num;

public class BollingerChart {
    private static TimePeriodValues buildChartTimeSeries(BarSeries tickSeries, Indicator<Num> indicator, String name) {
        TimePeriodValues timePeriodValues  = new TimePeriodValues(name);
        for (int i = 0; i < tickSeries.getBarCount(); i++) {
            Bar tick = tickSeries.getBar(i);
            timePeriodValues.add(new SimpleTimePeriod(tick.getEndTime().toInstant().toEpochMilli(),
                    tick.getEndTime().toInstant().toEpochMilli()), indicator.getValue(i).doubleValue());
        }
        return timePeriodValues;
    }

    private static void displayChart(JFreeChart chart) {
        // Chart panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new java.awt.Dimension(500, 270));
        // Application frame
        ApplicationFrame frame = new ApplicationFrame("Ta4j example - Indicators to chart");
        frame.setContentPane(panel);
        frame.pack();
        //RefineryUtilities.centerFrameOnScreen(frame);
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        BarSeries series = CsvTimeSeries.csvTimeSeries("D:\\scrip\\data\\NIFTY BANK\\day\\data.csv").getSubSeries(0,50);
        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);
        EMAIndicator avg14 = new EMAIndicator(closePrice, 14);
        StandardDeviationIndicator sd14 = new StandardDeviationIndicator(closePrice, 14);

        // Bollinger bands
        BollingerBandsMiddleIndicator middleBBand = new BollingerBandsMiddleIndicator(avg14);
        BollingerBandsLowerIndicator lowBBand = new BollingerBandsLowerIndicator(middleBBand, sd14);
        BollingerBandsUpperIndicator upBBand = new BollingerBandsUpperIndicator(middleBBand, sd14);

        /**
         * Building chart dataset
         */
        TimePeriodValuesCollection dataset = new TimePeriodValuesCollection();
        dataset.addSeries(buildChartTimeSeries(series, closePrice, "Apple Inc. (AAPL) - NASDAQ GS"));
        dataset.addSeries(buildChartTimeSeries(series, lowBBand, "Low Bollinger Band"));
        dataset.addSeries(buildChartTimeSeries(series, upBBand, "High Bollinger Band"));

        /**
         * Creating the chart
         */
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                "Apple Inc. 2013 Close Prices", // title
                "Date", // x-axis label
                "Price Per Unit", // y-axis label
                dataset, // data
                true, // create legend?
                true, // generate tooltips?
                false // generate URLs?
        );
        XYPlot plot = (XYPlot) chart.getPlot();
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        /**
         * Displaying the chart
         */
        displayChart(chart);
    }

}