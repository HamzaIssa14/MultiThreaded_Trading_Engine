package com.amazonaws.engine.enums;

public enum OrderAction {
    BUY("BUY"), SELL("SELL");

    private final String description;

    OrderAction(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }
}
