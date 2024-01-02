package com.amazonaws.engine.cache;

import com.amazonaws.engine.enums.OrderAction;
import com.amazonaws.engine.enums.StockUniverse;
import com.amazonaws.engine.order.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultCacheService implements CacheService {

    private final CacheConnectionPool connectionPool;
    private final ObjectMapper objectMapper;

    public DefaultCacheService(CacheConnectionPool connectionPool, ObjectMapper objectMapper){
        this.connectionPool = connectionPool;
        this.objectMapper = objectMapper;
    }

    private String convertToJSON(Order order) throws IOException {
        return objectMapper.writeValueAsString(order);
    }

    private Order convertToObject(String objectJSON) throws IOException {
        return objectMapper.readValue(objectJSON, Order.class);
    }

    private String buildOrderHashKey(StockUniverse stockTicker, OrderAction orderAction){
        // i.e. APPL:BUY
        return stockTicker.getDescription() + ":"+ orderAction.getDescription();
    }

    @Override
    public Order getOrder(StockUniverse stockTicker, OrderAction orderAction, String orderId) throws IOException {
        String orderHashKey = buildOrderHashKey(stockTicker, orderAction);

        try (Jedis jedis = connectionPool.getPool().getResource()){
            String orderJson = jedis.hget(orderHashKey, orderId);
            return orderJson != null ? convertToObject(orderJson) : null;
        }
    }

    @Override
    public List<Order> listBuyOrders(StockUniverse stockTicker){
        return listOrders(stockTicker, OrderAction.BUY);
    }

    @Override
    public List<Order> listSellOrders(StockUniverse stockTicker) {
        return listOrders(stockTicker, OrderAction.SELL);
    }

    @Override
    public List<Order> listAllOrders(StockUniverse stockTicker){
        List<Order> allOrders = new ArrayList<>();
        allOrders.addAll(listOrders(stockTicker, OrderAction.BUY));
        allOrders.addAll(listOrders(stockTicker, OrderAction.SELL));
        return allOrders;

    }

    @Override
    public List<Order> listOrders(StockUniverse stockTicker, OrderAction orderAction) {
        List<Order> orders = new ArrayList<>();

        String orderHashKey = buildOrderHashKey(stockTicker, orderAction);

        try (Jedis jedis = connectionPool.getPool().getResource()){
            List<String> orderJsonList = new ArrayList<>(jedis.hvals(orderHashKey));
            for(String orderJson : orderJsonList){
                orders.add(convertToObject(orderJson));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return orders;
    }

    @Override
    public void pushOrder(Order order) {

        try {
            String orderJson = convertToJSON(order);
            String orderHashKey = buildOrderHashKey(order.getStock().getStockTicker(), order.getOrderAction());

            try (Jedis jedis = connectionPool.getPool().getResource()) {
                System.out.println("------ Pushing Order to CacheService");
                jedis.hset(orderHashKey, order.getOrderId(), orderJson);
                System.out.println("------ Attempted pushing order to CacheService");
            }
        } catch (Exception e) {
            System.out.println("------ FAILIURE pushing order to CacheService");

            e.printStackTrace();
        }
    }


    @Override
    public void removeFilledOrder(StockUniverse stockTicker, OrderAction orderAction, String orderId) {
        String orderHashKey = buildOrderHashKey(stockTicker, orderAction);

        try (Jedis jedis = connectionPool.getPool().getResource()){
            jedis.hdel(orderHashKey, orderId);
        }
    }

    @Override
    public void updatePartiallyFilledOrder(Order order) throws IOException {
        pushOrder(order); // Since pushOrder overwrites the existing order
    }
}
