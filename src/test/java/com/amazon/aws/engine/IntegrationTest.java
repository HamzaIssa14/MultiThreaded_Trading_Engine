package com.amazon.aws.engine;

import com.amazonaws.engine.client.User;
import com.amazonaws.engine.enums.OrderAction;
import com.amazonaws.engine.enums.StockUniverse;
import com.amazonaws.engine.order.BuyOrder;
import com.amazonaws.engine.order.Order;
import com.amazonaws.engine.process.StockPipeline;
import com.amazonaws.engine.process.StockPipelineFactory;
import com.amazonaws.engine.server.Server;
import com.amazonaws.engine.stock.Stock;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.Date;


public class IntegrationTest {
    private StockPipeline stockPipeline;
    private Server server;

    @Before
    public void setup(){
        StockPipelineFactory stockPipelineFactory = new StockPipelineFactory();
        server = new Server(stockPipelineFactory);
    }

    @Test
    public void processOrder_EvenBuySellOrders_ShouldFillAllOrders() throws InterruptedException {

        Stock stock = Mockito.mock(Stock.class);
        User user = Mockito.mock(User.class);

        Order buyOrder = new BuyOrder.Builder()
                .orderAction(OrderAction.BUY)
                .stock(stock)
                .shares(2)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder1 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(1)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder2 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(1)
                .date(new Date())
                .user(user)
                .build();

        Mockito.when(stock.getStockTicker()).thenReturn(StockUniverse.APPL);

        Thread thread = new Thread(server);
        thread.start();

        server.processOrder(buyOrder);
        server.processOrder(sellOrder1);
        server.processOrder(sellOrder2);

        Thread.sleep(15000);
    }

    @Test
    public void processOrder_UnevenBuySellOrders_ShouldPartiallyFillOrder() throws InterruptedException {

        Stock stock = Mockito.mock(Stock.class);
        User user = Mockito.mock(User.class);

        Order buyOrder = new BuyOrder.Builder()
                .orderAction(OrderAction.BUY)
                .stock(stock)
                .shares(2)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder1 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(1)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder2 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(3)
                .date(new Date())
                .user(user)
                .build();

        Mockito.when(stock.getStockTicker()).thenReturn(StockUniverse.APPL);

        Thread thread = new Thread(server);
        thread.start();

        server.processOrder(buyOrder);
//        Thread.sleep(3000);
        server.processOrder(sellOrder1);
//        Thread.sleep(3000);
        server.processOrder(sellOrder2);
        Thread.sleep(3000);

    }

    @Test
    public void processOrder_UnevenManyBuySellOrders_ShouldPartiallyFillOrder() throws InterruptedException {

        Stock stock = Mockito.mock(Stock.class);
        User user = Mockito.mock(User.class);

        Order buyOrder = new BuyOrder.Builder()
                .orderAction(OrderAction.BUY)
                .stock(stock)
                .shares(6)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder1 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(1)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder2 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(2)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder3 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(2)
                .date(new Date())
                .user(user)
                .build();

        Order sellOrder4 = new BuyOrder.Builder()
                .orderAction(OrderAction.SELL)
                .stock(stock)
                .shares(3)
                .date(new Date())
                .user(user)
                .build();

        Mockito.when(stock.getStockTicker()).thenReturn(StockUniverse.APPL);

        Thread thread = new Thread(server);
        thread.start();

        server.processOrder(buyOrder);
//        Thread.sleep(3000);
        server.processOrder(sellOrder1);
//        Thread.sleep(3000);
        server.processOrder(sellOrder2);
//        Thread.sleep(3000);
        server.processOrder(sellOrder3);
//        Thread.sleep(3000);
        server.processOrder(sellOrder4);
        Thread.sleep(5000);

    }
}
