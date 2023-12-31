package com.amazonaws.engine.process;

import com.amazonaws.engine.algorithm.OrderMatchingAlgorithm;
import com.amazonaws.engine.order.Order;

import java.util.ArrayList;
import java.util.List;

public class StockConsumer implements Runnable{
    private SharedBuffer<Order> buySharedBuffer;
    private SharedBuffer<Order> sellSharedBuffer;
    private OrderMatchingAlgorithm orderMatchingAlgorithm;

    public StockConsumer(SharedBuffer<Order> buySharedBuffer,
                         SharedBuffer<Order> sellSharedBuffer,
                         OrderMatchingAlgorithm orderMatchingAlgorithm){
        this.buySharedBuffer = buySharedBuffer;
        this.sellSharedBuffer = sellSharedBuffer;
        this.orderMatchingAlgorithm = orderMatchingAlgorithm;
    }

    @Override
    public void run(){
        try {
            List<Order> sellOrders = new ArrayList<>();
            Order buyOrder;
            if (!buySharedBuffer.buffer.isEmpty()) {
                buyOrder = buySharedBuffer.take();
                System.out.println("StockConsumer polling queue of of pending orders...");
                while (countTotalSellShares(sellOrders) < buyOrder.getShares()) {
                    if (!sellSharedBuffer.buffer.isEmpty()) {
                        Order sellOrder = sellSharedBuffer.take();
                        sellOrders.add(sellOrder);
                    }
                }
                System.out.println("Executing OrderMatching Algorithm...");
                // Run Matching Algorithm
                orderMatchingAlgorithm.fillOrder(buyOrder, sellOrders);
            }
        } catch (InterruptedException e){
            // TODO: Handle error.
        }
    }
    private int countTotalSellShares(List<Order> sellOrders){
        return sellOrders.stream().mapToInt(Order::getShares).sum();
    }



}
