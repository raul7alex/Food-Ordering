package com.progandroid.foodordering.entities;


public class ItemObject {

    private int id;
    private String foodImage;
    private String foodName;
    private String foodSubName;
    private String foodPrice;

    public ItemObject(int id, String foodImage, String foodName, String foodSubName, String foodPrice) {
        this.id = id;
        this.foodImage = foodImage;
        this.foodName = foodName;
        this.foodSubName = foodSubName;
        this.foodPrice = foodPrice;
    }

    public ItemObject(String foodImage, String foodName, String foodSubName, String foodPrice) {
        this.foodImage = foodImage;
        this.foodPrice = foodPrice;
        this.foodSubName = foodSubName;
        this.foodName = foodName;
    }

    public int getId() {
        return id;
    }

    public String getFoodImage() {
        return foodImage;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodSubName() {
        return foodSubName;
    }

    public String getFoodPrice() {
        return foodPrice;
    }

}
