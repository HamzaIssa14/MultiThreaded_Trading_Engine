package com.amazon.aws.engine;

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

public class ServerTest {
    private StockPipeline stockPipeline;
    private Server server;

    @Before
    public void setup(){
        stockPipeline = Mockito.mock(StockPipeline.class);
        StockPipelineFactory stockPipelineFactory = Mockito.mock(StockPipelineFactory.class);
        Mockito.when(stockPipelineFactory.generateStockPipeline()).thenReturn(stockPipeline);
        server = new Server(stockPipelineFactory);
    }

    @Test
    public void processOrder_WithNewStockOrder_ShouldSubmitOrderToStockPipeline() throws InterruptedException {
        Order buyOrder = Mockito.mock(BuyOrder.class);
        Stock stock = Mockito.mock(Stock.class);

        Mockito.when(buyOrder.getOrderAction()).thenReturn(OrderAction.BUY);
        Mockito.when(buyOrder.getStock()).thenReturn(stock);
        Mockito.when(stock.getStockTicker()).thenReturn(StockUniverse.APPL);

        Thread thread = new Thread(server);
        thread.start();
        server.processOrder(buyOrder);
        Mockito.verify(stockPipeline, Mockito.times(1)).submitOrder(buyOrder);
        thread.interrupt();
    }
}
