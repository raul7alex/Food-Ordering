package com.progandroid.foodordering.entities;


public class OrderObject {

    private int id;
    private String orderDate;
    private float orderPrice;
    private String orderStatus;

    public OrderObject(int id, String orderDate, float orderPrice, String orderStatus) {
        this.id = id;
        this.orderDate = orderDate;
        this.orderPrice = orderPrice;
        this.orderStatus = orderStatus;
    }

    public int getId() {
        return id;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public float getOrderPrice() {
        return orderPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
