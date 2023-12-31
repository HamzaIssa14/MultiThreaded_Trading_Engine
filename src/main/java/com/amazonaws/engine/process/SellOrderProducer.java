package com.amazonaws.engine.process;

import com.amazonaws.engine.order.Order;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class SellOrderProducer implements Runnable{
    BlockingQueue<Order> blockingQueue;
    SharedBuffer<Order> sharedBuffer;
    public SellOrderProducer(SharedBuffer<Order> sharedBuffer){
        this.sharedBuffer = sharedBuffer;
        this.blockingQueue = new LinkedBlockingQueue<>();
    }

    @Override
    public void run(){
        try {
            Order order = blockingQueue.take();
            sharedBuffer.put(order);
        } catch (InterruptedException e){
            // TODO: Handle it...
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
