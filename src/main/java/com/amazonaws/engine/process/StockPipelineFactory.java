package com.amazonaws.engine.process;

import com.amazonaws.engine.algorithm.OrderMatchingAlgorithm;
import com.amazonaws.engine.cache.CacheConnectionPool;
import com.amazonaws.engine.cache.CacheService;
import com.amazonaws.engine.cache.DefaultCacheService;
import com.amazonaws.engine.order.Order;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StockPipelineFactory {

    public StockPipelineFactory(){}

    public StockPipeline generateStockPipeline(){
        ObjectMapper objectMapper = new ObjectMapper();
        CacheConnectionPool cacheConnectionPool = new CacheConnectionPool();
        CacheService cacheService = new DefaultCacheService(cacheConnectionPool, objectMapper);
        SharedBuffer<Order> buySharedBuffer = new SharedBuffer<>();
        SharedBuffer<Order> sellSharedBuffer = new SharedBuffer<>();
        BuyOrderProducer buyOrderProducer = new BuyOrderProducer(buySharedBuffer, cacheService);
        SellOrderProducer sellOrderProducer = new SellOrderProducer(sellSharedBuffer, cacheService);
        OrderMatchingAlgorithm orderMatchingAlgorithm = new OrderMatchingAlgorithm(sellSharedBuffer, cacheService);
        StockConsumer stockConsumer = new StockConsumer(buySharedBuffer, sellSharedBuffer, orderMatchingAlgorithm);

        return StockPipeline.generateStockPipeline(buyOrderProducer, sellOrderProducer, stockConsumer);
    }
}
