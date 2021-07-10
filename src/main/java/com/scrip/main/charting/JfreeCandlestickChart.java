package com.scrip.main.charting;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.data.xy.*;
import org.ta4j.core.BarSeries;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.*;
import java.util.*;
import java.util.List;

public class JfreeCandlestickChart extends JFrame {
    public JfreeCandlestickChart(String title, String stockSymbol, BarSeries barSeries) {
        super("CandlestickDemo");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DateAxis    domainAxis       = new DateAxis("Date");
        NumberAxis  rangeAxis        = new NumberAxis("Price");
        CandlestickRenderer renderer = new CandlestickRenderer();
        XYDataset   dataset          = getDataSet(stockSymbol, barSeries);

        XYPlot mainPlot = new XYPlot(dataset, domainAxis, rangeAxis, renderer);

        //Do some setting up, see the API Doc
        renderer.setSeriesPaint(0, Color.BLACK);
        renderer.setDrawVolume(false);
        rangeAxis.setAutoRangeIncludesZero(false);
        domainAxis.setTimeline(newWorkdayTimeline());

        //Now create the chart and chart panel
        JFreeChart chart = new JFreeChart(title, null, mainPlot, false);
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));

        this.add(chartPanel);
        this.pack();

        try {
            ChartUtils.saveChartAsJPEG(new File("D:\\files\\charts\\" + stockSymbol + ".jpeg"),
                    chart, 1920, 1080);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    protected AbstractXYDataset getDataSet(String stockSymbol, BarSeries barSeries) {
        //This is the dataset we are going to create
        DefaultOHLCDataset result = null;
        //This is the data needed for the dataset
        OHLCDataItem[] data;

        //This is where we go get the data, replace with your own data source
        data = getData(barSeries);

        //Create a dataset, an Open, High, Low, Close dataset
        result = new DefaultOHLCDataset(stockSymbol, data);

        return result;
    }
    //This method uses yahoo finance to get the OHLC data
    protected OHLCDataItem[] getData(BarSeries barSeries) {
        List<OHLCDataItem> dataItems = new ArrayList<OHLCDataItem>();
            DateFormat df = new SimpleDateFormat("y-M-d");

            barSeries.getBarData().forEach(
                    bar -> {
                        Date date       = Date.from(bar.getEndTime().toInstant());
                        double open     = bar.getOpenPrice().doubleValue();
                        double high     = bar.getHighPrice().doubleValue();
                        double low      = bar.getLowPrice().doubleValue();
                        double close    = bar.getClosePrice().doubleValue();
                        double volume   = bar.getVolume().doubleValue();

                        OHLCDataItem item = new OHLCDataItem(date, open, high, low, close, volume);
                        dataItems.add(item);
                    }
            );
        //Convert the list into an array
        OHLCDataItem[] data = dataItems.toArray(new OHLCDataItem[dataItems.size()]);
        return data;
    }

    public SegmentedTimeline newWorkdayTimeline() {
        SegmentedTimeline timeline = new SegmentedTimeline(
                SegmentedTimeline.HOUR_SEGMENT_SIZE, 8, 16);
        timeline.setStartTime(SegmentedTimeline.firstMondayAfter1900()
                + 9 * timeline.getSegmentSize());
        timeline.setBaseTimeline(SegmentedTimeline.newMondayThroughFridayTimeline());
        return timeline;
    }

}