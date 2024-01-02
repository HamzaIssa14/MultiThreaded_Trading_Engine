package com.amazonaws.engine;

import com.amazonaws.engine.process.StockPipelineFactory;
import com.amazonaws.engine.server.Server;

public class Main {
    public static void main(String[] args){
        StockPipelineFactory stockPipelineFactory = new StockPipelineFactory();
        Server server = new Server(stockPipelineFactory);
        Thread thread = new Thread(server);
        thread.start();
    }
}
