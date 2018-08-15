package com.progandroid.foodordering.entities;


public class LoginObject {

    private int id;
    private String username;
    private String email;
    private String address;
    private String phone;
    private String loggedIn;

    public LoginObject(int id, String username, String email, String address, String phone, String loggedIn) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.address = address;
        this.phone = phone;
        this.loggedIn = loggedIn;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getLoggedIn() {
        return loggedIn;
    }
}
