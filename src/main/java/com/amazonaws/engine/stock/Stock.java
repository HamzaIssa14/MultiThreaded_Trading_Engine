package com.amazonaws.engine.stock;

import com.amazonaws.engine.enums.StockUniverse;

public class Stock {
    private StockUniverse stockTicker;

    public Stock(StockUniverse stockTicker){
        this.stockTicker = stockTicker;
    }

    public StockUniverse getStockTicker(){
        return stockTicker;
    }
}
