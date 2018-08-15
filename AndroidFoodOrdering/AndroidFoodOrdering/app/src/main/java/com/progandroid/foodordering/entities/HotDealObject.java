package com.progandroid.foodordering.entities;


public class HotDealObject {

    private String item_name;
    private String description;
    private float item_price;

    public HotDealObject(String item_name, String description, float item_price) {
        this.item_name = item_name;
        this.description = description;
        this.item_price = item_price;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getDescription() {
        return description;
    }

    public float getItem_price() {
        return item_price;
    }
}
