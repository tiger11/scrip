package com.scrip.config;

import org.redisson.Redisson;
import org.redisson.RedissonMap;
import org.redisson.RedissonMapCache;
import org.redisson.RedissonTimeSeries;
import org.redisson.api.RedissonClient;

import java.util.Date;

public class Redis {

    public static void main(String[] args) {
        RedissonClient client = Redisson.create();

        client.getMapCache("hello1").fastPut("1", "2");

        RedissonMapCache series = (RedissonMapCache)client.getMapCache("hello1");
        System.out.println("series.first() = " + series.get("1"));
    }

}
