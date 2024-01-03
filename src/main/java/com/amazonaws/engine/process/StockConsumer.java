package com.amazonaws.engine.process;

import com.amazonaws.engine.algorithm.OrderMatchingAlgorithm;
import com.amazonaws.engine.order.Order;

import java.io.IOException;
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
        // REMIDNER, THIS CONSUMER IS NOT MADE CORRECTLY.
        // IT NEEDS TO WAIT FOR THE PRODUCER TO HAVE CONTENTS ON AN INFINITE LOOP
        // THEN IF A VALID COLLECTION OF SELL ORDERS IS OBTAINED, THEN CALL ORDERMATCHING ALGO....

        // Make sure to check entire process to ensure that it is constantly runnigng, all threads....

        try {
            List<Order> sellOrders = new ArrayList<>();
            Order buyOrder;
            while (!buySharedBuffer.buffer.isEmpty()) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private int countTotalSellShares(List<Order> sellOrders){
        return sellOrders.stream().mapToInt(Order::getShares).sum();
    }



}
