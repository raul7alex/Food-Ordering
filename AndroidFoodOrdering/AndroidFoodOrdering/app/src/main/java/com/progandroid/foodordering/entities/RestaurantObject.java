package com.progandroid.foodordering.entities;


public class RestaurantObject {

    private String name;
    private String description;
    private String address;
    private String email;
    private String phone;
    private String opening_time;

    public RestaurantObject(String name, String description, String address, String email, String phone, String opening_time) {
        this.name = name;
        this.description = description;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.opening_time = opening_time;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getOpening_time() {
        return opening_time;
    }
}
