package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        LinearLayout mypageBtn = (LinearLayout) findViewById(R.id.home_mypage);
        mypageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), MypageActivity.class);
                startActivity(loginIntent);
            }
        });

        LinearLayout orderdetailBtn = (LinearLayout) findViewById(R.id.home_orderdetail);
        orderdetailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                startActivity(loginIntent);
            }
        });

        LinearLayout orderBtn = (LinearLayout) findViewById(R.id.home_order);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(getApplicationContext(), CategoryActivity.class);
                startActivity(loginIntent);
            }
        });
    }
}