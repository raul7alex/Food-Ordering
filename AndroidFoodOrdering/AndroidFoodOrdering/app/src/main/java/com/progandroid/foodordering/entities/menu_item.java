package com.progandroid.foodordering.entities;


public class menu_item {

    private int menu_item_id;
    private int menu_id;
    private String item_name;
    private String description;
    private String item_picture;
    private float item_price;
    private int item_quantity;
    private pivot joinPivot;

    public menu_item(int menu_item_id, int menu_id, String item_name, String description, String item_picture, float item_price, int item_quantity, pivot joinPivot) {
        this.menu_item_id = menu_item_id;
        this.menu_id = menu_id;
        this.item_name = item_name;
        this.description = description;
        this.item_picture = item_picture;
        this.item_price = item_price;
        this.item_quantity = item_quantity;
        this.joinPivot = joinPivot;
    }

    public int getMenu_item_id() {
        return menu_item_id;
    }

    public int getMenu_id() {
        return menu_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getDescription() {
        return description;
    }

    public String getItem_picture() {
        return item_picture;
    }

    public float getItem_price() {
        return item_price;
    }

    public int getItem_quantity() {
        return item_quantity;
    }

    public pivot getJoinPivot() {
        return joinPivot;
    }
}
