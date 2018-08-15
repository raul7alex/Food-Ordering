package com.progandroid.foodordering;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.progandroid.foodordering.adapter.CheckoutAdapter;
import com.progandroid.foodordering.entities.CartObject;
import com.progandroid.foodordering.network.GsonRequest;
import com.progandroid.foodordering.network.VolleySingleton;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.DrawCart;
import com.progandroid.foodordering.util.Helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderListActivity extends AppCompatActivity {

    private static final String TAG = OrderListActivity.class.getSimpleName();

    private TextView orderItemCount, orderTotalAmount, orderVat, orderFullAmount, restaurantName, restaurantAddress, orderNumber;

    private RecyclerView orderRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);

        setTitle(getString(R.string.past_orders));

        int orderId = getIntent().getExtras().getInt("ORDER_ID");

        restaurantName = (TextView)findViewById(R.id.restaurant_name);
        restaurantAddress = (TextView)findViewById(R.id.restaurant_address);
        restaurantName.setText(restaurantDetails(0));
        restaurantAddress.setText(restaurantDetails(1));

        orderItemCount = (TextView)findViewById(R.id.order_item_count);
        orderTotalAmount =(TextView)findViewById(R.id.order_total_amount);
        orderVat = (TextView)findViewById(R.id.order_vat);
        orderFullAmount = (TextView)findViewById(R.id.order_full_amounts);

        orderNumber = (TextView)findViewById(R.id.order_number);
        orderNumber.setText("Order number #"+ String.valueOf(orderId));


        orderRecyclerView = (RecyclerView)findViewById(R.id.checkout_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderRecyclerView.setLayoutManager(linearLayoutManager);
        orderRecyclerView.setHasFixedSize(true);

        // make network call
        getOrderHistoryByIdFromRemoteServer(orderId);
    }

    private void getOrderHistoryByIdFromRemoteServer(int userId){
        Map<String, String> params = new HashMap<String,String>();
        params.put(Helper.ID, String.valueOf(userId));

        GsonRequest<CartObject[]> serverRequest = new GsonRequest<CartObject[]>(
                Request.Method.POST,
                Helper.PATH_TO_ORDER_HISTORY,
                CartObject[].class,
                params,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(this).addToRequestQueue(serverRequest);
    }

    private Response.Listener<CartObject[]> createRequestSuccessListener() {
        return new Response.Listener<CartObject[]>() {
            @Override
            public void onResponse(CartObject[] response) {
                try {
                    Log.d(TAG, "Json Response " + response.length);
                    if(response.length > 0){
                        List<CartObject> orderHistory = new ArrayList<CartObject>();
                        Collections.addAll(orderHistory, response);
                        calculateOrder(orderHistory);
                        CheckoutAdapter mAdapter = new CheckoutAdapter(OrderListActivity.this, orderHistory);
                        orderRecyclerView.setAdapter(mAdapter);
                    }else{
                        Toast.makeText(OrderListActivity.this, R.string.failed_login, Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private Response.ErrorListener createRequestErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        };
    }

    private void calculateOrder(List<CartObject> allOrder){
        orderItemCount.setText(String.valueOf(allOrder.size()));

        DrawCart drawCart = new DrawCart(this);
        double subTotal = drawCart.getSubtotalAmount(allOrder);

        orderTotalAmount.setText("$" + String.valueOf(subTotal));
        orderVat.setText("$0.00");
        orderFullAmount.setText("$" + String.valueOf(subTotal));
    }

    private String restaurantDetails(int index){
        String restaurant = ((CustomApplication)getApplication()).getShared().getRestaurantInformation();
        String[] restaurantList = restaurant.split(":");
        if(restaurantList.length > 0){
            return restaurantList[index];
        }
        return "";
    }
}
