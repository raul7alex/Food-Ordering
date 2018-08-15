package com.progandroid.foodordering.entities;


public class CartObject {

    private int id;
    private String orderName;
    private int quantity;
    private float price;
    private String extra;
    private String note;

    public CartObject(int id, String orderName, int quantity, float price, String extra, String note) {
        this.id = id;
        this.orderName = orderName;
        this.quantity = quantity;
        this.price = price;
        this.extra = extra;
        this.note = note;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getOrderName() {
        return orderName;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public String getExtra() {
        return extra;
    }

    public String getNote() {
        return note;
    }
}
