package com.progandroid.foodordering.entities;

import java.util.List;

public class HistoryObject {

    private int order_id;
    private String order_date;
    private int order_quantity;
    private float order_price;
    private String status;
    private String payment_method;
    List<menu_item> menu_items;

    public HistoryObject(int order_id, String order_date, int order_quantity, float order_price, String status, String payment_method, List<menu_item> menu_items) {
        this.order_id = order_id;
        this.order_date = order_date;
        this.order_quantity = order_quantity;
        this.order_price = order_price;
        this.status = status;
        this.payment_method = payment_method;
        this.menu_items = menu_items;
    }

    public int getOrder_id() {
        return order_id;
    }

    public String getOrder_date(){
        return order_date;
    }

    public int getOrder_quantity() {
        return order_quantity;
    }

    public float getOrder_price() {
        return order_price;
    }

    public String getStatus() {
        return status;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public List<menu_item> getMenu_items() {
        return menu_items;
    }
}
