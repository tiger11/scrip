package com.scrip.main.study;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Try {
    public static void main(String[] args) {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime executeAt = now.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(5 + (5 * (now.getMinute() / 5)));
        long initialDelay = Duration.between(now, executeAt).getSeconds();

        System.out.println("now = " + now);
        System.out.println("executeAT = " + executeAt);
        System.out.println("initialDelay = " + initialDelay);
    }
}
