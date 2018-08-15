package com.progandroid.foodordering.entities;


public class MenuItemObject {

    private int menu_id;
    private String item_name;
    private String description;
    private String item_picture;
    private float item_price;
    private String item_options;
    private String item_note;

    public MenuItemObject(int menu_id, String item_name, String description, String item_picture, float item_price, String item_options) {
        this.menu_id = menu_id;
        this.item_name = item_name;
        this.description = description;
        this.item_picture = item_picture;
        this.item_price = item_price;
        this.item_options = item_options;
    }

    public MenuItemObject(int menu_id, String item_name, String description, String item_picture, float item_price, String item_options, String item_note) {
        this.menu_id = menu_id;
        this.item_name = item_name;
        this.description = description;
        this.item_picture = item_picture;
        this.item_price = item_price;
        this.item_options = item_options;
        this.item_note = item_note;
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

    public String getItem_options(){
        return item_options;
    }
}
