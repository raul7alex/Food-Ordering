package com.progandroid.foodordering.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.progandroid.foodordering.R;
import com.progandroid.foodordering.database.Query;
import com.progandroid.foodordering.entities.FavoriteObject;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteViewHolder>{

    private static final String TAG = FavouriteAdapter.class.getSimpleName();

    private Context context;
    private List<FavoriteObject> favorites;

    public FavouriteAdapter(Context context, List<FavoriteObject> favorites) {
        this.context = context;
        this.favorites = favorites;
    }

    @Override
    public FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.favorite_layout, parent, false);
        return new FavouriteViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(FavouriteViewHolder holder, final int position) {
        FavoriteObject fObject = favorites.get(position);
        final int favoriteId = fObject.getId();

        Glide.with(context).load(fObject.getImagePath()).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().override(250, 250).into(holder.path);
        holder.name.setText(fObject.getName());
        holder.price.setText("$" + String.valueOf(fObject.getPrice()) + "0");

        holder.path.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query db = new Query(context);
                db.deleteFavoriteMenuItem(favoriteId);
                favorites.remove(position);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }
}
