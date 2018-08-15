package com.progandroid.foodordering;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.progandroid.foodordering.database.Query;
import com.progandroid.foodordering.entities.CartObject;
import com.progandroid.foodordering.entities.MenuItemObject;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.CustomSharedPreference;
import com.progandroid.foodordering.util.DrawCart;
import com.progandroid.foodordering.util.Helper;

import java.util.ArrayList;
import java.util.List;

public class FoodActivity extends AppCompatActivity {

    private static final String TAG = FoodActivity.class.getSimpleName();

    private LinearLayout boxWrapper;

    private MenuItemObject singleMenuItem;

    private LinearLayout.LayoutParams params;

    private List<CheckBox> optionCheckBox = new ArrayList<CheckBox>();

    private Gson gson;
    private CustomSharedPreference shared;
    private Query dbQuery;

    private String addedOrderNote = "";
    private String addedOrderOptions = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        gson = ((CustomApplication)getApplication()).getGsonObject();
        shared = ((CustomApplication)getApplication()).getShared();
        dbQuery = new Query(this);

        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        ImageView menuItemImage = (ImageView)findViewById(R.id.food_item_image);
        TextView menuItemName = (TextView)findViewById(R.id.food_item_name);
        TextView menuItemPrice = (TextView)findViewById(R.id.food_item_price);
        TextView menuItemDescription = (TextView)findViewById(R.id.food_item_description);

        ImageView favoriteButton = (ImageView)findViewById(R.id.favorite_icon);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.displayErrorMessage(FoodActivity.this, "Your favorite was successfully added");
                storeFavoriteMenuItem(singleMenuItem);
            }
        });

        Button customizeButton = (Button)findViewById(R.id.customize_button);
        customizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomizeOrderDialog();
            }
        });

        Button addToCartButton = (Button)findViewById(R.id.add_to_cart);
        addToCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create new Cart object and add to cart list object
                CartObject orderItem = new CartObject(singleMenuItem.getMenu_id(), singleMenuItem.getItem_name(), 1,
                        singleMenuItem.getItem_price(), addedOrderOptions, addedOrderNote);
                addMenuItemToCart(orderItem);
                invalidateCart();
            }
        });

        boxWrapper = (LinearLayout)findViewById(R.id.box_wrapper);
        boxWrapper.setVisibility(View.GONE);

        String menuString = getIntent().getExtras().getString("MENU_ITEM");

        if(!TextUtils.isEmpty(menuString)){
            Gson gson = ((CustomApplication)getApplication()).getGsonObject();
            singleMenuItem = gson.fromJson(menuString, MenuItemObject.class);
            boxWrapper.setVisibility(View.VISIBLE);
            setTitle(singleMenuItem.getItem_name());

            String serverImagePath = Helper.PUBLIC_FOLDER + singleMenuItem.getItem_picture();
            Glide.with(FoodActivity.this).load(serverImagePath).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().override(300, 300).into(menuItemImage);

            menuItemName.setText(singleMenuItem.getItem_name());
            menuItemDescription.setText(singleMenuItem.getDescription());
            menuItemPrice.setText("$" + String.valueOf(singleMenuItem.getItem_price()) + "0");
        }else {
            Toast.makeText(FoodActivity.this, getString(R.string.no_information), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuItem = menu.findItem(R.id.action_shop);
        int mCount = ((CustomApplication)getApplication()).cartItemCount();
        DrawCart dCart = new DrawCart(this);
        menuItem.setIcon(dCart.buildCounterDrawable(mCount, R.drawable.cart));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_shop) {
            Intent checkoutIntent = new Intent(FoodActivity.this, CartActivity.class);
            startActivity(checkoutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void invalidateCart() {
        invalidateOptionsMenu();
    }

    private void showCustomizeOrderDialog(){
        LayoutInflater inflater = LayoutInflater.from(FoodActivity.this);
        View subView = inflater.inflate(R.layout.add_option_layout, null);

        LinearLayout wrapLayout = (LinearLayout)subView.findViewById(R.id.option_layout);
        final EditText mOrderNote = (EditText)subView.findViewById(R.id.order_note);

        final String mOptions = singleMenuItem.getItem_options();
        if(TextUtils.isEmpty(mOptions)){
            TextView noticeTextView = new TextView(FoodActivity.this);
            noticeTextView.setText(R.string.no_option_item);
            noticeTextView.setLayoutParams(params);

            wrapLayout.removeAllViews();
            wrapLayout.addView(noticeTextView);
        }else{
            String[] allOptions = mOptions.split(",");
            optionCheckBox.clear();
            wrapLayout.removeAllViews();
            for(int i = 0; i < allOptions.length; i++){
                CheckBox oneBox = createDynamicCheckBox(i, allOptions[i]);
                optionCheckBox.add(oneBox);
                wrapLayout.addView(oneBox);
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Menu Item Option");
        builder.setView(subView);
        builder.create();

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addedOrderNote = mOrderNote.getText().toString();
                for(int i = 0; i < optionCheckBox.size(); i++){
                   CheckBox mBox = optionCheckBox.get(i);
                    if(mBox.isChecked()){
                        addedOrderOptions += mBox.getText().toString() + " ";
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(FoodActivity.this, "No order customization added", Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
    }

    private CheckBox createDynamicCheckBox(int index, String textContent){
        CheckBox checkBox = new CheckBox(this);
        checkBox.setId(index);
        checkBox.setText(textContent);
        checkBox.setLayoutParams(params);
        return checkBox;
    }

    private void addMenuItemToCart(CartObject orderItem){
        if(shared.getCartItems().equals("")){
            List<CartObject> cartListItems = new ArrayList<CartObject>();
            cartListItems.add(orderItem);
            storeCartOrderList(cartListItems);
        }else{
            String storeOrderList = shared.getCartItems();
            CartObject[] cartItemsCollections = gson.fromJson(storeOrderList, CartObject[].class);
            DrawCart cart = new DrawCart(this);
            List<CartObject> allOrders = cart.convertObjectArrayToListObject(cartItemsCollections);
            allOrders.add(orderItem);
            storeCartOrderList(allOrders);
        }
    }

    private void storeCartOrderList(List<CartObject> orderList){
        String mOrders = gson.toJson(orderList);
        shared.updateCartItems(mOrders);
    }

    private void storeFavoriteMenuItem(MenuItemObject itemObject){
        int id = itemObject.getMenu_id();
        String name = itemObject.getItem_name();
        String path = Helper.PUBLIC_FOLDER + itemObject.getItem_picture();
        float price = itemObject.getItem_price();
        //add to local database
        dbQuery.addFavoriteMenuItem(name, path, price);
    }

}
