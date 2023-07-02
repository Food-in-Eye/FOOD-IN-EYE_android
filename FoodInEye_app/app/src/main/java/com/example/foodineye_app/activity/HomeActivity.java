package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.R;

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

                Intent loginIntent = new Intent(getApplicationContext(), StorelistActivity.class);
                startActivity(loginIntent);
            }
        });
    }
//    protected void setupUIs() {
//        EditText userIdText = findViewById(R.id.shopUserId);
//        userIdText.setOnClickListener(view -> {
//            userIdText.requestFocus();
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInput(userIdText, InputMethodManager.SHOW_IMPLICIT);
//        });
//
//        View shopButton = findViewById(R.id.btn_shop);
//        shopButton.setOnClickListener(view -> {
//            Intent intent = new Intent(getApplicationContext(), ShopActivity.class);
//            intent.putExtra("user-id", userIdText.getText().toString());
//            startActivity(intent);
//        });
//    }
}