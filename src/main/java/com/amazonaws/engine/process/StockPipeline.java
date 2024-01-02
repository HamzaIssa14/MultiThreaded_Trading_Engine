package com.amazonaws.engine.process;

import com.amazonaws.engine.enums.OrderAction;
import com.amazonaws.engine.order.Order;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class StockPipeline implements Runnable {

    BlockingQueue<Order> orderQueue;
    BuyOrderProducer buyOrderProducer;
    SellOrderProducer sellOrderProducer;
    StockConsumer stockConsumer;
    ExecutorService pool;

    private StockPipeline(BuyOrderProducer buyOrderProducer,
                         SellOrderProducer sellOrderProducer,
                          StockConsumer stockConsumer){
        this.buyOrderProducer = buyOrderProducer;
        this.sellOrderProducer = sellOrderProducer;
        this.stockConsumer = stockConsumer;
        pool = Executors.newCachedThreadPool();
        orderQueue = new LinkedBlockingQueue<>();
    }

    public static StockPipeline generateStockPipeline(BuyOrderProducer buyOrderProducer,
                                                      SellOrderProducer sellOrderProducer,
                                                      StockConsumer stockConsumer){
        return new StockPipeline(buyOrderProducer, sellOrderProducer, stockConsumer);
    }

    @Override
    public void run(){
        try {
            Order order = orderQueue.take();
            if(order.getOrderAction().equals(OrderAction.BUY)){
                buyOrderProducer.submitOrder(order);
                System.out.println("---- Submitting order #" + order.hashCode() +" to BuyOrderProducer");
                pool.execute(buyOrderProducer);

            } else if (order.getOrderAction().equals(OrderAction.SELL)){
                System.out.println("---- Submitting order #" + order.hashCode() +" to SellOrderProducer");
                sellOrderProducer.submitOrder(order);
                pool.execute(sellOrderProducer);

            }
            System.out.println("---- Booting up StockConsumer...");
            pool.execute(stockConsumer);
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
    }

    public void submitOrder(Order order) throws InterruptedException {
        orderQueue.put(order);
    }

}

