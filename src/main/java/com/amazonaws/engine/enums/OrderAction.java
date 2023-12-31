package com.amazonaws.engine.enums;

public enum OrderAction {
    BUY("Buy"), SELL("Sell");

    private final String description;

    OrderAction(String description){
        this.description = description;
    }
}
