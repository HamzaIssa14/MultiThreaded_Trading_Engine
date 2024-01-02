package com.amazonaws.engine.cache;

import com.amazonaws.engine.enums.OrderAction;
import com.amazonaws.engine.enums.StockUniverse;
import com.amazonaws.engine.order.Order;

import java.io.IOException;
import java.util.List;

public interface CacheService {
    Order getOrder(StockUniverse stockTicker, OrderAction orderAction, String orderId) throws IOException;
    List<Order> listAllOrders(StockUniverse stockTicker);
    List<Order> listBuyOrders(StockUniverse stockTicker);
    List<Order> listSellOrders(StockUniverse stockTicker);
    List<Order> listOrders(StockUniverse stockTicker, OrderAction orderAction);
    void pushOrder(Order order) throws IOException;
    void removeFilledOrder(StockUniverse stockTicker, OrderAction orderAction, String orderId);
    void updatePartiallyFilledOrder(Order order) throws IOException;

}
