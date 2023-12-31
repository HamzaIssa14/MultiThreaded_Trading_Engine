package com.amazonaws.engine.order;

public class BuyOrder extends Order {

    public static class Builder extends Order.Builder<Builder> {

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public Order build() {
            return new BuyOrder(this);
        }
    }

    private BuyOrder(Builder builder){
        super(builder);
    }
}
