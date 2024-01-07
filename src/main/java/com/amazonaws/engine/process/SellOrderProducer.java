package com.amazonaws.engine.process;

import com.amazonaws.engine.cache.CacheService;
import com.amazonaws.engine.order.Order;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SellOrderProducer implements Runnable{
    BlockingQueue<Order> blockingQueue;
    SharedBuffer<Order> sharedBuffer;
    CacheService cacheService;

    public SellOrderProducer(SharedBuffer<Order> sharedBuffer, CacheService cacheService){
        this.sharedBuffer = sharedBuffer;
        this.blockingQueue = new LinkedBlockingQueue<>();
        this.cacheService = cacheService;
    }

    @Override
    public void run(){
        while (true){
            try {
                Order order = blockingQueue.take();
                sharedBuffer.put(order);
                cacheService.pushOrder(order);
            } catch (InterruptedException | IOException e){
                // TODO: Handle it...
            }
        }
    }

    public void submitOrder(Order order){
        try {
            blockingQueue.put(order);
        } catch (InterruptedException e){
            // TODO: Handle it...
        }
    }

}
