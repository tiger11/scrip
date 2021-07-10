package com.scrip.main.study;

import java.util.UUID;
import java.util.concurrent.BlockingQueue;

public class Provider implements Runnable{

    private BlockingQueue<String> randomData;

    public Provider(BlockingQueue<String> randomData){
        this.randomData = randomData;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Value Added");
            randomData.add(UUID.randomUUID().toString());
        }
    }
}
