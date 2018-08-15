package com.progandroid.foodordering.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.progandroid.foodordering.R;

public class FavouriteViewHolder extends RecyclerView.ViewHolder{

    public TextView name;
    public ImageView path;
    public TextView price;
    public ImageView delete;

    public FavouriteViewHolder(View itemView) {
        super(itemView);
        name = (TextView)itemView.findViewById(R.id.food_name);
        path = (ImageView) itemView.findViewById(R.id.food_image);
        price = (TextView)itemView.findViewById(R.id.food_price);
        delete = (ImageView)itemView.findViewById(R.id.delete_favorite);
    }
}
