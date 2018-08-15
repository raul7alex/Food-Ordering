package com.progandroid.foodordering.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.progandroid.foodordering.R;
import com.progandroid.foodordering.entities.RestaurantObject;
import com.progandroid.foodordering.network.GsonRequest;
import com.progandroid.foodordering.network.VolleySingleton;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.Helper;

public class HomeFragment extends Fragment {

    private static final String TAG = HomeFragment.class.getSimpleName();

    private TextView restaurantName, restaurantAddress, restaurantOpeningHour, restaurantDescription,
    address, restaurantEmail, restaurantPhone;

    private LinearLayout generalWrapper;


    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.restuarant_home));
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        restaurantName = (TextView)view.findViewById(R.id.restaurant_name);
        restaurantAddress = (TextView)view.findViewById(R.id.restaurant_address);
        restaurantOpeningHour = (TextView)view.findViewById(R.id.restaurant_opening_hours);
        restaurantDescription = (TextView)view.findViewById(R.id.restaurant_description);
        address = (TextView)view.findViewById(R.id.address_info);
        restaurantEmail = (TextView)view.findViewById(R.id.restaurant_email_address);
        restaurantPhone = (TextView)view.findViewById(R.id.phone_number);

        generalWrapper = (LinearLayout)view.findViewById(R.id.general_wrapper);

        hideView();
        getRestaurantInformationFromRemoteServer();
        return view;
    }

    private void getRestaurantInformationFromRemoteServer(){
        GsonRequest<RestaurantObject> serverRequest = new GsonRequest<RestaurantObject>(
                Request.Method.GET,
                Helper.PATH_TO_RESTAURANT_HOME,
                RestaurantObject.class,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(serverRequest);
    }

    private Response.Listener<RestaurantObject> createRequestSuccessListener() {
        return new Response.Listener<RestaurantObject>() {
            @Override
            public void onResponse(RestaurantObject response) {
                try {
                    Log.d(TAG, "Json Response " + response.toString());
                    if(!TextUtils.isEmpty(response.getAddress())){
                        //display restaurant information
                        showView();
                        restaurantName.setText(response.getName());
                        restaurantAddress.setText(response.getAddress());
                        restaurantOpeningHour.setText(response.getOpening_time());
                        restaurantDescription.setText(response.getDescription());
                        address.setText(response.getAddress());
                        restaurantEmail.setText(response.getEmail());
                        restaurantPhone.setText(response.getPhone());
                        String nameAddress = response.getName() + ":" + response.getAddress();
                        ((CustomApplication)getActivity().getApplication()).getShared().saveRestaurantInformation(nameAddress);
                    }else{
                        Toast.makeText(getActivity(), "Restaurant information is empty", Toast.LENGTH_LONG).show();
                        return;
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

    private void hideView(){
        generalWrapper.setVisibility(View.GONE);
    }

    private void showView(){
        generalWrapper.setVisibility(View.VISIBLE);
    }

}
