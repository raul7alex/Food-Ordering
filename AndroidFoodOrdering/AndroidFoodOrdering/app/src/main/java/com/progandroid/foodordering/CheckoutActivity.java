package com.progandroid.foodordering;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.progandroid.foodordering.adapter.CheckoutAdapter;
import com.progandroid.foodordering.entities.CartObject;
import com.progandroid.foodordering.entities.LoginObject;
import com.progandroid.foodordering.entities.PaymentResponseObject;
import com.progandroid.foodordering.entities.SuccessObject;
import com.progandroid.foodordering.network.GsonRequest;
import com.progandroid.foodordering.network.VolleySingleton;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.CustomSharedPreference;
import com.progandroid.foodordering.util.DrawCart;
import com.progandroid.foodordering.util.Helper;
import com.progandroid.foodordering.util.SimpleDividerItemDecoration;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckoutActivity extends AppCompatActivity {

    private static final String TAG = CheckoutActivity.class.getSimpleName();

    private TextView orderItemCount, orderTotalAmount, orderVat, orderFullAmount, restaurantName,
            restaurantAddress, deliveryAddress;

    private CustomSharedPreference shared;

    private RadioGroup radioGroup;
    private String paymentMethod = "";
    private String buyerDeliveryAddress;

    private LoginObject user;
    private List<CartObject> checkoutOrder;
    private String finalList;

    private double subTotal;

    private static PayPalConfiguration config;
    private static final int REQUEST_PAYMENT_CODE = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        setTitle(getString(R.string.my_checkout));

        config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_NO_NETWORK).clientId(Helper.CLIENT_ID);

        finalList = getIntent().getExtras().getString("FINAL_ORDER");
        Log.d(TAG, "JSON FORMAT" + finalList);
        Gson gson = ((CustomApplication)getApplication()).getGsonObject();
        DrawCart cart = new DrawCart(this);
        checkoutOrder = cart.convertObjectArrayToListObject(gson.fromJson(finalList, CartObject[].class));

        shared = ((CustomApplication)getApplication()).getShared();
        user = gson.fromJson(shared.getUserData(), LoginObject.class);

        deliveryAddress = (TextView)findViewById(R.id.delivery_address);
        showDeliveryAddress();

        radioGroup = (RadioGroup)findViewById(R.id.radio_group);
        selectPaymentMethod();

        restaurantName = (TextView)findViewById(R.id.restaurant_name);
        restaurantAddress = (TextView)findViewById(R.id.restaurant_address);
        restaurantName.setText(restaurantDetails(0));
        restaurantAddress.setText(restaurantDetails(1));

        orderItemCount = (TextView)findViewById(R.id.order_item_count);
        orderTotalAmount =(TextView)findViewById(R.id.order_total_amount);
        orderVat = (TextView)findViewById(R.id.order_vat);
        orderFullAmount = (TextView)findViewById(R.id.order_full_amounts);

        orderItemCount.setText(String.valueOf(checkoutOrder.size()));
        DrawCart drawCart = new DrawCart(this);
        subTotal = drawCart.getSubtotalAmount(checkoutOrder);
        orderTotalAmount.setText("$" + String.valueOf(subTotal) + "0");
        orderVat.setText("0.00");
        orderFullAmount.setText("$" + String.valueOf(subTotal) + "0");


        RecyclerView checkoutRecyclerView = (RecyclerView)findViewById(R.id.checkout_item);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        checkoutRecyclerView.setLayoutManager(linearLayoutManager);

        checkoutRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        checkoutRecyclerView.setHasFixedSize(true);

        CheckoutAdapter mAdapter = new CheckoutAdapter(CheckoutActivity.this, checkoutOrder);
        checkoutRecyclerView.setAdapter(mAdapter);


        TextView addNewAddress = (TextView)findViewById(R.id.add_new_address);
        addNewAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newAddressIntent = new Intent(CheckoutActivity.this, NewAddressActivity.class);
                startActivity(newAddressIntent);
            }
        });

        TextView addNewCardPayment = (TextView)findViewById(R.id.add_payment_method);
        addNewCardPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newPaymentIntent = new Intent(CheckoutActivity.this, NewPaymentActivity.class);
                startActivity(newPaymentIntent);
            }
        });

        Button placeOrderButton = (Button)findViewById(R.id.place_an_order);
        placeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(radioGroup.getCheckedRadioButtonId() < 0){
                    Helper.displayErrorMessage(CheckoutActivity.this, "Payment option must be selected before checkout");
                    return;
                }
                if(TextUtils.isEmpty(buyerDeliveryAddress)){
                    Helper.displayErrorMessage(CheckoutActivity.this, "You must provide a delivery address before checkout");
                    return;
                }

                if(paymentMethod.equals("PAY PAL")){
                    initializePayPalPayment();
                }else if(paymentMethod.equals("CREDIT CARD")){

                }else{
                    postCheckoutOrderToRemoteServer(String.valueOf(user.getId()), String.valueOf(checkoutOrder.size()), String.valueOf(subTotal), paymentMethod, finalList);
                }
            }
        });
    }

    private String restaurantDetails(int index){
        String restaurant = ((CustomApplication)getApplication()).getShared().getRestaurantInformation();
        String[] restaurantList = restaurant.split(":");
        if(restaurantList.length > 0){
            return restaurantList[index];
        }
        return "";
    }

    private void showDeliveryAddress(){
        if(!TextUtils.isEmpty(shared.getSavedDeliveryAddress())){
            buyerDeliveryAddress = shared.getSavedDeliveryAddress();
            deliveryAddress.setText(shared.getSavedDeliveryAddress());
        }else{
            buyerDeliveryAddress = user.getAddress();
            deliveryAddress.setText(user.getAddress());
        }
    }

    private void selectPaymentMethod(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.pay_pal_payment:
                        paymentMethod = "PAY PAL";
                        break;
                    case R.id.credit_card_payment:
                        paymentMethod = "CREDIT CARD";
                        break;
                    case R.id.cash_on_delivery:
                        paymentMethod = "CASH ON DELIVERY";
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        showDeliveryAddress();
    }


    private void postCheckoutOrderToRemoteServer(String userId, String quantity, String price, String payment_method, String order_list){
        Map<String, String> params = new HashMap<String,String>();
        params.put("USER_ID", userId);
        params.put("QUANTITY", quantity);
        params.put("PRICE", price);
        params.put("PAYMENT", payment_method);
        params.put("ORDER_LIST", order_list);

        GsonRequest<SuccessObject> serverRequest = new GsonRequest<SuccessObject>(
                Request.Method.POST,
                Helper.PATH_TO_PLACE_ORDER,
                SuccessObject.class,
                params,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(CheckoutActivity.this).addToRequestQueue(serverRequest);
    }

    private Response.Listener<SuccessObject> createRequestSuccessListener() {
        return new Response.Listener<SuccessObject>() {
            @Override
            public void onResponse(SuccessObject response) {
                //Log.d(TAG,"JSON response " + response.toString());
                try {
                    Log.d(TAG, "Json Response " + response.getSuccess());
                    if(response.getSuccess() == 1){
                        //delete paid other
                        ((CustomApplication)getApplication()).getShared().updateCartItems("");
                        //confirmation page.
                        Intent orderIntent = new Intent(CheckoutActivity.this, OrderComfirmationActivity.class);
                        startActivity(orderIntent);
                    }else{
                        Helper.displayErrorMessage(CheckoutActivity.this, "Failed to upload order to server");
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

    private void initializePayPalPayment(){
        PayPalPayment payment = new PayPalPayment(new BigDecimal(String.valueOf(subTotal)), "USD", "Food Order", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payment);
        startActivityForResult(intent, REQUEST_PAYMENT_CODE);
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PAYMENT_CODE) {
            PaymentConfirmation confirm = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
            if (confirm != null) {
                try {
                    String jsonPaymentResponse = confirm.toJSONObject().toString(4);
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    PaymentResponseObject responseObject = gson.fromJson(jsonPaymentResponse, PaymentResponseObject.class);
                    if(responseObject != null){
                        String paymentId = responseObject.getResponse().getId();
                        String paymentState = responseObject.getResponse().getState();
                        Log.d(TAG, "Log payment id and state " + paymentId + " " + paymentState);
                        //send order to server
                        postCheckoutOrderToRemoteServer(String.valueOf(user.getId()), String.valueOf(checkoutOrder.size()), String.valueOf(subTotal), paymentMethod, finalList);
                    }
                } catch (JSONException e) {
                    Log.e("paymentExample", "an extremely unlikely failure occurred: ", e);
                }
            }
        }
        else if (resultCode == Activity.RESULT_CANCELED) {
            Log.i("paymentExample", "The user canceled.");
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
            Log.i("paymentExample", "An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
        }
    }
}
