package com.progandroid.foodordering.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.progandroid.foodordering.R;

public class CartViewHolder extends RecyclerView.ViewHolder {

    public TextView orderItemName;
    public TextView orderItemExtra;
    public TextView removeOrder;
    public TextView orderSubtotal;
    public TextView orderQuantity;

    public Button minusButton;
    public Button plusButton;


    public CartViewHolder(View itemView) {
        super(itemView);

        orderItemName = (TextView)itemView.findViewById(R.id.order_item_name);
        orderItemExtra = (TextView)itemView.findViewById(R.id.order_item_exra);
        removeOrder = (TextView)itemView.findViewById(R.id.remove_order);
        orderSubtotal = (TextView)itemView.findViewById(R.id.order_total);
        orderQuantity = (TextView)itemView.findViewById(R.id.order_quantity);

        minusButton = (Button)itemView.findViewById(R.id.minus_button);
        plusButton = (Button)itemView.findViewById(R.id.plus_button);
    }
}
