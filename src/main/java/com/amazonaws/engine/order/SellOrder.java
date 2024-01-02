package com.amazonaws.engine.order;

public class SellOrder extends Order{

    public static class Builder extends Order.Builder<Builder>  {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Order build() {
            return new SellOrder(this);
        }
    }

    private SellOrder(Builder builder){
        super(builder);
    }
}
