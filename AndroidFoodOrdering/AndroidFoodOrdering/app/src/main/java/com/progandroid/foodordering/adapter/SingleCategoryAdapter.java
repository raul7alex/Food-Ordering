package com.progandroid.foodordering.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.progandroid.foodordering.FoodActivity;
import com.progandroid.foodordering.entities.MenuItemObject;
import com.progandroid.foodordering.util.CustomApplication;
import com.progandroid.foodordering.util.Helper;

import java.util.List;

public class SingleCategoryAdapter extends RecyclerView.Adapter<SingleCategoryViewHolder>{

    private Context context;
    private List<MenuItemObject> itemObjectList;
    private int layoutResource;
    private View layoutView;

    public SingleCategoryAdapter(Context context, List<MenuItemObject> itemObjectList, int layoutResource) {
        this.context = context;
        this.itemObjectList = itemObjectList;
        this.layoutResource = layoutResource;
    }

    @Override
    public SingleCategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        layoutView = LayoutInflater.from(parent.getContext()).inflate(layoutResource, parent, false);
        return new SingleCategoryViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(SingleCategoryViewHolder holder, int position) {
        MenuItemObject singleItem = itemObjectList.get(position);
        final int id = singleItem.getMenu_id();

        // use Glide to download and display the category image.
        String serverImagePath = Helper.PUBLIC_FOLDER + singleItem.getItem_picture();
        Glide.with(context).load(serverImagePath).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().override(150, 150).into(holder.foodImage);

        holder.foodName.setText(singleItem.getItem_name());
        holder.foodPrice.setText("$" + String.valueOf(singleItem.getItem_price()) + "0");

        Gson gson = ((CustomApplication)((Activity)context).getApplication()).getGsonObject();
        final String objectToString = gson.toJson(singleItem);

        layoutView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent foodIntent = new Intent(context, FoodActivity.class);
                foodIntent.putExtra("MENU_ITEM", objectToString);
                context.startActivity(foodIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemObjectList.size();
    }

}
