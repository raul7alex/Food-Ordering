package com.progandroid.foodordering.entities;


public class pivot {

    private int order_id;
    private int quantity;
    private float price;
    private float subtotal;
    private String options;
    private String notes;

    public pivot(int order_id, int quantity, float price, float subtotal, String options, String notes) {
        this.order_id = order_id;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
        this.options = options;
        this.notes = notes;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public float getPrice() {
        return price;
    }

    public float getSubtotal() {
        return subtotal;
    }

    public String getOptions() {
        return options;
    }

    public String getNotes() {
        return notes;
    }
}
