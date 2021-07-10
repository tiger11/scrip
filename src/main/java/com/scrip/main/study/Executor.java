package com.scrip.main.study;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;

public class Executor {

    public static void main(String[] args) throws InterruptedException {
        BlockingQueue<String> strings = new LinkedBlockingDeque<>();
        Provider p = new Provider(strings);
        Consumer c = new Consumer(strings);

        Thread t1 = new Thread(p);
        Thread t2 = new Thread(c);
        t1.start();
        t2.start();

        Thread.sleep(10000);

    }

}
