package com.amazonaws.engine.enums;

public enum OrderStatus {
    UNFILLED("Unfilled"), FILLED("Filled"), PARTIALLY_FILLED("Partially filled");

    private final String description;

    OrderStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
}
