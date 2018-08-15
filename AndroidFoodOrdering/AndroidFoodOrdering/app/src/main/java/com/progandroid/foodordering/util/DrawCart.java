package com.progandroid.foodordering.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.progandroid.foodordering.R;
import com.progandroid.foodordering.entities.CartObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawCart {

    private Context context;

    public DrawCart(Context context) {
        this.context = context;
    }

    public Drawable buildCounterDrawable(int count, int backgroundImageId) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.shopping_layout, null);
        view.setBackgroundResource(backgroundImageId);

        if (count == 0) {
            View counterTextPanel = view.findViewById(R.id.counterValuePanel);
            counterTextPanel.setVisibility(View.GONE);
        } else {
            TextView textView = (TextView) view.findViewById(R.id.count);
            textView.setText("" + count);
        }

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                     View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);

        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public List<CartObject> convertObjectArrayToListObject(CartObject[] allProducts){
        List<CartObject> mProduct = new ArrayList<CartObject>();
        Collections.addAll(mProduct, allProducts);
        return mProduct;
    }

    public double getSubtotalAmount(List<CartObject> orderedItems){
        double total = 0.0;
        for (int i = 0; i < orderedItems.size(); i++){
            int quantity = orderedItems.get(i).getQuantity();
            if(quantity == 0){
                quantity = 1;
            }
            float itemSubtotal = quantity * orderedItems.get(i).getPrice();
            total += (double)itemSubtotal;
        }
        return total;
    }
}
