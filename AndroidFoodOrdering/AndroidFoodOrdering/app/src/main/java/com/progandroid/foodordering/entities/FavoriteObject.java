package com.progandroid.foodordering.entities;


public class FavoriteObject {

    private int id;
    private String name;
    private String imagePath;
    private float price;

    public FavoriteObject(int id, String name, String imagePath, float price) {
        this.id = id;
        this.name = name;
        this.imagePath = imagePath;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImagePath() {
        return imagePath;
    }

    public float getPrice() {
        return price;
    }
}
