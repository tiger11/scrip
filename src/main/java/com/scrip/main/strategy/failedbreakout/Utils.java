package com.scrip.main.strategy.failedbreakout;

import com.scrip.main.util.CsvTimeSeries;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.num.Num;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {

    public static Map<ZonedDateTime, List<Bar>> getBarData(BarSeries barSeries) {
        return barSeries.getBarData().stream()
                .collect(Collectors.groupingBy(d -> d.getEndTime().truncatedTo(ChronoUnit.DAYS),
                        LinkedHashMap::new, Collectors.toList()));
    }

    public static Num getHighOfDay(List<Bar> barList){
        return barList.stream().max(Comparator.comparing(Bar::getHighPrice)).get().getHighPrice();
    }

    public static Num getLowOfDay(List<Bar> barList){
        return barList.stream().min(Comparator.comparing(Bar::getLowPrice)).get().getLowPrice();
    }

    public static Num getHighestCloseOfDay(List<Bar> barList){
        return barList.stream().max(Comparator.comparing(Bar::getClosePrice)).get().getClosePrice();
    }
}
