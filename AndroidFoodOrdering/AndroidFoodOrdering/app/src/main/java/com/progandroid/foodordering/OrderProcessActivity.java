package com.progandroid.foodordering;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.progandroid.map.OrderTrackingActivity;

public class OrderProcessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_process);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar){
            actionBar.hide();
        }

        Button startTrackingButton = (Button)findViewById(R.id.start_tracking);
        startTrackingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startTrackingIntent = new Intent(OrderProcessActivity.this, OrderTrackingActivity.class);
                startActivity(startTrackingIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent confirmIntent = new Intent(OrderProcessActivity.this, MainActivity.class);
        startActivity(confirmIntent);
    }
}
