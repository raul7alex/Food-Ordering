package com.progandroid.foodordering.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.progandroid.foodordering.R;
import com.progandroid.foodordering.SingleMenuCategoryActivity;
import com.progandroid.foodordering.entities.MenuCategoryObject;
import com.progandroid.foodordering.util.Helper;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryViewHolder>{

    private Context context;
    private List<MenuCategoryObject> categoryObject;

    public CategoryAdapter(Context context, List<MenuCategoryObject> categoryObject) {
        this.context = context;
        this.categoryObject = categoryObject;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_category_list, parent, false);
        return new CategoryViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        final MenuCategoryObject catObject = categoryObject.get(position);
        final int id = catObject.getMenu_id();
        holder.categoryName.setText(catObject.getMenu_name());

        // use Glide to download and display the category image.
        String serverImagePath = Helper.PUBLIC_FOLDER + catObject.getMenu_image();
        Glide.with(context).load(serverImagePath).diskCacheStrategy(DiskCacheStrategy.ALL).fitCenter().override(300, 300).into(holder.categoryImage);

        holder.categoryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent categoryIntent = new Intent(context, SingleMenuCategoryActivity.class);
                categoryIntent.putExtra("CATEGORY_NAME", catObject.getMenu_name());
                categoryIntent.putExtra("CATEGORY_ID", id);
                context.startActivity(categoryIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryObject.size();
    }


    public static int getResourseId(Context context, String pVariableName, String pResourcename, String pPackageName) throws RuntimeException {
        try {
            return context.getResources().getIdentifier(pVariableName, pResourcename, pPackageName);
        } catch (Exception e) {
            throw new RuntimeException("Error getting Resource ID.", e);
        }
    }
}
