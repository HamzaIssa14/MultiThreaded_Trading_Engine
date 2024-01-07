package com.amazonaws.engine.cache;

import com.amazonaws.engine.enums.OrderAction;
import com.amazonaws.engine.enums.StockUniverse;
import com.amazonaws.engine.order.Order;
import com.fasterxml.jackson.databind.ObjectMapper;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DefaultCacheService implements CacheService {
    private final int MAX_ATTEMPTS = 10;
    private final long WAIT_TIME_MS = 1000; // 1 second
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
                Transaction transaction = jedis.multi(); // Start the transaction
                transaction.hset(orderHashKey, order.getOrderId(), orderJson);
                transaction.exec(); // Execute all commands in the transaction

                System.out.println("********* Pushing to Redis with key: " + orderHashKey + " and field: " + order.getOrderId());
            }
        } catch (Exception e) {
            System.out.println("------ FAILURE pushing order to CacheService");
            e.printStackTrace();
        }
    }

    @Override
    public void removeFilledOrder(StockUniverse stockTicker, OrderAction orderAction, String orderId) {
        String orderHashKey = buildOrderHashKey(stockTicker, orderAction);

        try (Jedis jedis = connectionPool.getPool().getResource()) {
            for (int i = 0; i < MAX_ATTEMPTS; i++) {
                if (jedis.hexists(orderHashKey, orderId)) {
                    jedis.hdel(orderHashKey, orderId);
                    System.out.println("Removed: " + orderHashKey + " " + orderId);
                    return;
                }

                System.out.println("Waiting for key to exist: " + orderId);
                Thread.sleep(WAIT_TIME_MS);
            }
            System.out.println("Timeout waiting for key: " + orderId);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupted while waiting for key: " + orderId);
        }
    }



    @Override
    public void updatePartiallyFilledOrder(Order order) throws IOException {
        pushOrder(order); // Since pushOrder overwrites the existing order
    }
}
