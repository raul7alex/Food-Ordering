package com.progandroid.foodordering.util;


import android.content.Context;
import android.content.SharedPreferences;

public class CustomSharedPreference {

    private SharedPreferences sharedPref;

    public CustomSharedPreference(Context context) {
        sharedPref = context.getSharedPreferences(Helper.SHARED_PREF, Context.MODE_PRIVATE);
    }

    public SharedPreferences getInstanceOfSharedPreference(){
        return sharedPref;
    }

    //Save user information
    public void setUserData(String userData){
        sharedPref.edit().putString(Helper.USER_DATA, userData).apply();
    }

    public String getUserData(){
        return sharedPref.getString(Helper.USER_DATA, "");
    }

    //Save Cart Information
    public void updateCartItems(String cartItem){
        sharedPref.edit().putString(Helper.CART, cartItem).apply();
    }

    public String getCartItems(){
        return sharedPref.getString(Helper.CART, "");
    }

    //Save Delivery Address Information
    public void saveDeliveryAddress(String address){
        sharedPref.edit().putString(Helper.DELIVERY_ADDRESS, address).apply();
    }

    public String getSavedDeliveryAddress(){
        return sharedPref.getString(Helper.DELIVERY_ADDRESS, "");
    }

    //Save Payment Card Information
    public void saveCreditCardDetails(String cardDetails){
        sharedPref.edit().putString(Helper.CREDIT_CARD, cardDetails).apply();
    }

    public String getSavedCreditCardDetails(){
        return sharedPref.getString(Helper.CREDIT_CARD, "");
    }

    // Save Restaurant Information
    public void saveRestaurantInformation(String restaurant){
        sharedPref.edit().putString(Helper.RESTAURANT, restaurant).apply();
    }

    public String getRestaurantInformation(){
        return sharedPref.getString(Helper.RESTAURANT, "");
    }

}
