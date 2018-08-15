package com.progandroid.foodordering;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.progandroid.foodordering.adapter.CartAdapter;
import com.progandroid.foodordering.entities.CartObject;
import com.progandroid.foodordering.entities.EventMessage;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.CustomSharedPreference;
import com.progandroid.foodordering.util.DrawCart;
import com.progandroid.foodordering.util.SimpleDividerItemDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class CartActivity extends AppCompatActivity {

    private static final String TAG = CartActivity.class.getSimpleName();

    private LinearLayout wrapper;
    private LinearLayout downWrapper;
    private LinearLayout noOrder;

    private TextView orderTotalAmount;
    private TextView orderItemCount;

    private Gson mGson;
    private CustomSharedPreference pref;
    private List<CartObject> orderedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        setTitle(getString(R.string.your_cart));

        mGson = ((CustomApplication)getApplication()).getGsonObject();
        pref = ((CustomApplication)getApplication()).getShared();

        wrapper = (LinearLayout)findViewById(R.id.activity_cart);
        downWrapper = (LinearLayout)findViewById(R.id.button_container);
        noOrder = (LinearLayout)findViewById(R.id.no_order);

        wrapper.setVisibility(View.GONE);
        downWrapper.setVisibility(View.GONE);
        noOrder.setVisibility(View.GONE);

        orderItemCount = (TextView) findViewById(R.id.order_item_count);
        orderTotalAmount = (TextView) findViewById(R.id.order_total_amount);
        RecyclerView cartRecyclerView = (RecyclerView) findViewById(R.id.cart_item);

        Button backToMenuButton = (Button)findViewById(R.id.back_to_menu_button);
        backToMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(CartActivity.this, MainActivity.class);
                startActivity(menuIntent);
            }
        });

        Button checkoutButton = (Button)findViewById(R.id.check_out_button);
        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent checkoutIntent = new Intent(CartActivity.this, CheckoutActivity.class);
                if(orderedItems.size() > 0){
                    String finalOrderList = mGson.toJson(orderedItems);
                    checkoutIntent.putExtra("FINAL_ORDER", finalOrderList);
                    startActivity(checkoutIntent);
                }
            }
        });

        String itemInCart = pref.getCartItems();
        CartObject[] ordersInCart = mGson.fromJson(itemInCart, CartObject[].class);
        if(ordersInCart == null){
            noOrder.setVisibility(View.VISIBLE);
        }else{
            wrapper.setVisibility(View.VISIBLE);
            downWrapper.setVisibility(View.VISIBLE);
            DrawCart cart = new DrawCart(this);
            orderedItems = cart.convertObjectArrayToListObject(ordersInCart);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            cartRecyclerView.setLayoutManager(linearLayoutManager);
            cartRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
            cartRecyclerView.setHasFixedSize(true);

            CartAdapter mAdapter = new CartAdapter(CartActivity.this, orderedItems);
            cartRecyclerView.setAdapter(mAdapter);

            orderItemCount.setText(String.valueOf(orderedItems.size()));
            DrawCart drawCart = new DrawCart(this);
            double subtotal = drawCart.getSubtotalAmount(orderedItems);
            orderTotalAmount.setText("$" + String.valueOf(subtotal));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventFromAdapter(EventMessage event){
        orderTotalAmount.setText("$" + event.getSubtotal());
        orderItemCount.setText(event.getItemCount());
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }
}
