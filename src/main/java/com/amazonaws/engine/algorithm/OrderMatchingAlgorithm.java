package com.amazonaws.engine.algorithm;

import com.amazonaws.engine.cache.CacheService;
import com.amazonaws.engine.enums.OrderStatus;
import com.amazonaws.engine.order.Order;
import com.amazonaws.engine.process.SharedBuffer;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;

/**
 * This com.amazonaws.engine.algorithm.OrderMatchingAlgorithm can be refactored to use a dynamic programming approach
 * to find the subset sum of existing sell orders while minimizing partially filled sell orders.
 *
 * Note. This algorithm is verbose for readability, but can be reduced.
 *
 */

public class OrderMatchingAlgorithm {
    SharedBuffer<Order> sellSharedBuffer;
    CacheService cacheService;

    public OrderMatchingAlgorithm(SharedBuffer<Order> sellSharedBuffer, CacheService cacheService){
        this.sellSharedBuffer = sellSharedBuffer;
        this.cacheService = cacheService;
    }

    public void fillOrder(Order buyOrder, List<Order> sellOrders) throws IOException {
        sellOrders.sort(Comparator.comparingInt(Order::getShares));
        System.out.println("***************Beginning Order Matching**********************");

        int requiredShares = buyOrder.getShares();
        int j = 0;
        while(j < sellOrders.size()){
            Order sellOrder = sellOrders.get(j);

            if (sellOrder.getShares() <= requiredShares){
                int sellSharesDeducted = sellOrders.get(j).getShares();
                requiredShares -= sellSharesDeducted;
                sellOrder.deductShares(sellSharesDeducted);
                sellOrder.setOrderStatus(OrderStatus.FILLED);
                cacheService.removeFilledOrder(sellOrder.getStock().getStockTicker(),
                        sellOrder.getOrderAction(),
                        sellOrder.getOrderId());
                System.out.println("///////////////Order " + sellOrder.getOrderId() + " Entered Here: 2//////////////////");

            } else if (sellOrder.getShares() > requiredShares){
                sellOrder.deductShares(requiredShares);
                sellOrder.setOrderStatus(OrderStatus.PARTIALLY_FILLED);
                System.out.println("///////////////Order " + sellOrder.getOrderId() + " Entered Here: 3//////////////////");
                cacheService.updatePartiallyFilledOrder(sellOrder);
                try {
                    sellSharedBuffer.put(sellOrder);

                } catch (InterruptedException e){
                    System.out.println("ERROR Returning Partially_Filled sellOrder to sellOrderSharedBuffer...");
                    e.printStackTrace();
                }
            }
            j++;
        }
        ////
        buyOrder.setOrderStatus(OrderStatus.FILLED);
        cacheService.removeFilledOrder(buyOrder.getStock().getStockTicker(),
                buyOrder.getOrderAction(),
                buyOrder.getOrderId());

        System.out.println("***************FINISHED Order Matching**********************");
        System.out.println("Buy order status: " + buyOrder.getStatus());
        for (Order sellOrder: sellOrders){
            System.out.println("SellOrder " + sellOrder.getOrderId() + " Original Shares " + sellOrder.getOriginalShares() +
                    " | New Status: " + sellOrder.getStatus() + " | Remaining Shares: " + sellOrder.getShares());
        }

    }
}
