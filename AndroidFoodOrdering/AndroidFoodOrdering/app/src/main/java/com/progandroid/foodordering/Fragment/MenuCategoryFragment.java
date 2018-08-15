package com.progandroid.foodordering.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.progandroid.foodordering.R;
import com.progandroid.foodordering.adapter.CategoryAdapter;
import com.progandroid.foodordering.entities.MenuCategoryObject;
import com.progandroid.foodordering.network.GsonRequest;
import com.progandroid.foodordering.network.VolleySingleton;
import com.progandroid.foodordering.util.Helper;

import java.util.ArrayList;
import java.util.List;


public class MenuCategoryFragment extends Fragment {

    private static final String TAG = MenuCategoryFragment.class.getSimpleName();

    private RecyclerView recyclerView;

    public MenuCategoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.manu_categories));
        View view = inflater.inflate(R.layout.fragment_menu_category, container, false);

        recyclerView = (RecyclerView)view.findViewById(R.id.food_menu);
        GridLayoutManager mGrid = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(mGrid);
        recyclerView.setHasFixedSize(true);

        getRestaurantMenuFromRemoteServer();

        return view;
    }

    private void getRestaurantMenuFromRemoteServer(){
        GsonRequest<MenuCategoryObject[]> serverRequest = new GsonRequest<MenuCategoryObject[]>(
                Request.Method.GET,
                Helper.PATH_TO_MENU,
                MenuCategoryObject[].class,
                createRequestSuccessListener(),
                createRequestErrorListener());

        serverRequest.setRetryPolicy(new DefaultRetryPolicy(
                Helper.MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getActivity()).addToRequestQueue(serverRequest);
    }

    private Response.Listener<MenuCategoryObject[]> createRequestSuccessListener() {
        return new Response.Listener<MenuCategoryObject[]>() {
            @Override
            public void onResponse(MenuCategoryObject[] response) {
                try {
                    Log.d(TAG, "Json Response " + response.toString());
                    if(response.length > 0){
                        //display restaurant menu information
                        List<MenuCategoryObject> menuObject = new ArrayList<>();
                        for(int i = 0; i < response.length; i++){
                            Log.d(TAG, "Menu name " + response[i].getMenu_name());
                            menuObject.add(new MenuCategoryObject(response[i].getMenu_id(), response[i].getMenu_name(), response[i].getMenu_image()));
                        }
                        CategoryAdapter mAdapter = new CategoryAdapter(getActivity(), menuObject);
                        recyclerView.setAdapter(mAdapter);

                    }else{
                        Toast.makeText(getActivity(), "Restaurant menu is empty", Toast.LENGTH_LONG).show();
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

}
