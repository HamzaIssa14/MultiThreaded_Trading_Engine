package com.amazonaws.engine.algorithm;

import com.amazonaws.engine.enums.OrderStatus;
import com.amazonaws.engine.order.Order;

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
    public OrderMatchingAlgorithm(){
    }

    public void fillOrder(Order buyOrder, List<Order> sellOrders){
        int i = 0;
        int sharesCount = 0;
        sellOrders.sort(Comparator.comparingInt(Order::getShares));
        System.out.println("***************Beginning Order Matching**********************");

        while(i < sellOrders.size() && sharesCount < buyOrder.getShares()){
            Order curSellOrder = sellOrders.get(i);
            sharesCount += curSellOrder.getShares();
            System.out.println("OrderMatchingAlgorithm - Iteration " + i + ", sharesCount : "  + sharesCount);
            i++;
        }
        System.out.println("SharesCount " + sharesCount);
        if (buyOrder.getShares() == sharesCount){
            buyOrder.setOrderStatus(OrderStatus.FILLED);
            for(Order order : sellOrders){
                order.setOrderStatus(OrderStatus.FILLED);
                order.deductShares(order.getOriginalShares());

            }
        } else if (buyOrder.getShares() < sharesCount){
            int requiredShares = buyOrder.getShares();
            int j = 0;
            while(j < sellOrders.size()){
                Order sellOrder = sellOrders.get(j);

                if (sellOrder.getShares() <= requiredShares){
                    int sellSharesDeducted = sellOrders.get(j).getShares();
                    requiredShares -= sellSharesDeducted;
                    sellOrder.deductShares(sellSharesDeducted);
                    sellOrder.setOrderStatus(OrderStatus.FILLED);


                } else if (sellOrder.getShares() > requiredShares){
                    sellOrder.deductShares(requiredShares);
                    sellOrder.setOrderStatus(OrderStatus.PARTIALLY_FILLED);

                    // TODO: Send it back to the SellOrder Producer...
                }
                j++;
            }
            System.out.println("Does this print??");
        }
        buyOrder.setOrderStatus(OrderStatus.FILLED);
        System.out.println("***************FINISHED Order Matching**********************");
        System.out.println("Buy order status: " + buyOrder.getStatus());
        for (Order sellOrder: sellOrders){
            System.out.println("SellOrder Original Shares " + sellOrder.getOriginalShares() +
                    " | New Status: " + sellOrder.getStatus() + " | Remaining Shares: " + sellOrder.getShares());
        }

    }
}
