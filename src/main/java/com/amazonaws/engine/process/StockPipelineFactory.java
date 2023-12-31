package com.amazonaws.engine.process;

import com.amazonaws.engine.algorithm.OrderMatchingAlgorithm;
import com.amazonaws.engine.order.Order;

public class StockPipelineFactory {

    public StockPipelineFactory(){}

    public StockPipeline generateStockPipeline(){
        SharedBuffer<Order> buySharedBuffer = new SharedBuffer<>();
        SharedBuffer<Order> sellSharedBuffer = new SharedBuffer<>();
        BuyOrderProducer buyOrderProducer = new BuyOrderProducer(buySharedBuffer);
        SellOrderProducer sellOrderProducer = new SellOrderProducer(sellSharedBuffer);
        OrderMatchingAlgorithm orderMatchingAlgorithm = new OrderMatchingAlgorithm(sellSharedBuffer);
        StockConsumer stockConsumer = new StockConsumer(buySharedBuffer, sellSharedBuffer, orderMatchingAlgorithm);

        return StockPipeline.generateStockPipeline(buyOrderProducer, sellOrderProducer, stockConsumer);
    }
}
