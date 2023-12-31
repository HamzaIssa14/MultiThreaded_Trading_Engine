package com.amazonaws.engine.server;

import com.amazonaws.engine.enums.StockUniverse;
import com.amazonaws.engine.order.Order;
import com.amazonaws.engine.process.StockPipeline;
import com.amazonaws.engine.process.StockPipelineFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {
    ServerSocket server;
    StockPipelineFactory stockPipelineFactory;
    ExecutorService connectionPool;
    ExecutorService stockPipelinePool;
    List<ConnectionHandler> connections;
    Map<StockUniverse, StockPipeline> stockPipelineMap;
    boolean done;


    public Server(StockPipelineFactory stockPipelineFactory){
        this.stockPipelineFactory = stockPipelineFactory;
        this.connections = new ArrayList<>();
        this.stockPipelineMap = new HashMap<>();
        this.done = false;
    }

    @Override
    public void run(){
        try{
            ServerSocket server = new ServerSocket(8080);
            connectionPool = Executors.newCachedThreadPool();
            stockPipelinePool = Executors.newCachedThreadPool();
            while(!done){
                Socket client = server.accept();
                ConnectionHandler connectionHandler = new ConnectionHandler(client);
                connections.add(connectionHandler);
                connectionPool.execute(connectionHandler);
            }
        } catch (IOException e){
            shutdown();
        }
    }

    private void shutdown(){
        try {
            done = true;
            if (!server.isClosed()){
                for(ConnectionHandler ch : connections){
                    ch.shutdown();
                    connections.remove(ch);
                }
                server.close();
            }
        } catch (Exception e){
            // Log it.
        }
    }

    public void processOrder(Order order){
        System.out.println("Entered Server.processOrder()");
            for(StockUniverse stock : StockUniverse.values()){
                if (stock.getDescription().equals(order.getStock().getStockTicker().getDescription()))
                    try {
                        if(stockPipelineMap.containsKey(order.getStock().getStockTicker())){
                            stockPipelineMap.get(order.getStock().getStockTicker()).submitOrder(order);
                            stockPipelinePool.execute(stockPipelineMap.get(order.getStock().getStockTicker()));
                        } else {
                            StockPipeline stockPipeline = stockPipelineFactory.generateStockPipeline();
                            stockPipelineMap.put(stock, stockPipeline);
                            stockPipeline.submitOrder(order);
                            stockPipelinePool.execute(stockPipeline);
                        }
                    } catch (InterruptedException e){
                        shutdown();
                    }
            }

    }
    private class ConnectionHandler implements Runnable{
        ObjectInputStream incoming;
        ObjectOutputStream outgoing;
        Socket client;
        public ConnectionHandler(Socket client){
            this.client = client;
        }
        @Override
        public void run(){
            try {
                incoming = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
                outgoing = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
                outgoing.flush();

                Object obj;
                while((obj = incoming.readObject()) != null) {
                    Order order;
                    if (obj instanceof Order) {
                        order = (Order) obj;
                        processOrder(order);
                    }
                }
            } catch (IOException | ClassNotFoundException e){
                shutdown();
            }
        }


        private void shutdown(){
           try {
               incoming.close();
               outgoing.close();
               if (!client.isClosed()){
                   client.close();
               }
           } catch (IOException e) {
               e.printStackTrace();
           }
        }
    }
}
