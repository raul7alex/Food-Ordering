package com.progandroid.foodordering.util;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.progandroid.foodordering.entities.CartObject;
import com.progandroid.foodordering.entities.LoginObject;


public class CustomApplication extends Application {

    private Gson gson;
    private GsonBuilder builder;

    private CustomSharedPreference shared;

    @Override
    public void onCreate() {
        super.onCreate();
        builder = new GsonBuilder();
        gson = builder.create();
        shared = new CustomSharedPreference(getApplicationContext());
    }

    public CustomSharedPreference getShared(){
        return shared;
    }

    public Gson getGsonObject(){
        return gson;
    }

    public LoginObject getLoginUser(){
        Gson mGson = getGsonObject();
        String storedUser = getShared().getUserData();
        return mGson.fromJson(storedUser, LoginObject.class);
    }

    public int cartItemCount(){
        String orderList = getShared().getCartItems();
        CartObject[] allCart = getGsonObject().fromJson(orderList, CartObject[].class);
        if(allCart == null){
            return 0;
        }
        return allCart.length;
    }
}