package com.amazonaws.engine.enums;

public enum StockUniverse {
    TSLA("Tesla Motors"), APPL("Apple");

    private final String description;

    StockUniverse(String description){
        this.description = description;
    }


    public String getDescription(){
        return description;
    }
}
