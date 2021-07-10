package com.scrip.main.study;

import java.util.concurrent.BlockingQueue;

public class Consumer implements Runnable{
    private BlockingQueue<String> randomData;

    public Consumer(BlockingQueue<String> randomData){
        this.randomData = randomData;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                System.out.println("Queue Size = " + randomData.size());
                System.out.println("randomData = " + randomData.take());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
