package com.amazonaws.engine.order;

import com.amazonaws.engine.client.User;
import com.amazonaws.engine.enums.OrderAction;
import com.amazonaws.engine.enums.OrderStatus;
import com.amazonaws.engine.stock.Stock;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public abstract class Order implements Serializable {
    private final String orderId;
    private final OrderAction orderAction;
    private final Stock stock;
    private final int originalShares;
    private int shares;
    private final Date date;
    private final User user;
    private OrderStatus status;

    public String getOrderId(){
        return orderId;
    }

    public OrderAction getOrderAction(){
        return orderAction;
    }

    public Stock getStock() {
        return stock;
    }

    public int getOriginalShares(){
        return originalShares;
    }

    public int getShares() {
        return shares;
    }


    public Date getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    public OrderStatus getStatus(){
        return status;
    }

    public void deductShares(int shareDeduction){
        this.shares -= shareDeduction;
    }

    public void setOrderStatus(OrderStatus status){
        this.status = status;
    }
    public static abstract class Builder<T extends Builder<T>> {
        private OrderAction orderAction;
        private Stock stock;
        private int originalShares;
        private int shares;
        private Date date;
        private User user;
        private OrderStatus status;

        public Builder() {}

        public T orderAction(OrderAction orderAction){
            this.orderAction = orderAction;
            return self();
        }
        public T stock(Stock stock){
            this.stock = stock;
            return self();
        }

        public T shares(int shares){
            this.originalShares = shares;
            this.shares = shares;
            return self();
        }

        public T date(Date date){
            this.date = date;
            return self();
        }

        public T user(User user){
            this.user = user;
            return self();
        }

        /**
         *  self() refers to the direct instance. In an abstract class, we dont use 'this' because
         *  we don't want to refer to the abstract class, we want to refer to the instance extending it.
         */
        protected abstract T self();

        public abstract Order build();
    }

    protected Order(Builder<?> builder){
        this.orderId = UUID.randomUUID().toString();
        this.orderAction = builder.orderAction;
        this.stock = builder.stock;
        this.originalShares = builder.shares;
        this.shares = builder.shares;
        this.date = builder.date;
        this.user = builder.user;
        this.status = OrderStatus.UNFILLED;
    }
}
