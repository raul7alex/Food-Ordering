package com.progandroid.foodordering.entities;


public class CategoryObject {

    private int id;

    private String categoryName;

    private String categoryImage;

    public CategoryObject(String categoryName, String categoryImage) {
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public CategoryObject(int id, String categoryName, String categoryImage) {
        this.id = id;
        this.categoryName = categoryName;
        this.categoryImage = categoryImage;
    }

    public int getId(){
        return id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getCategoryImage() {
        return categoryImage;
    }
}
