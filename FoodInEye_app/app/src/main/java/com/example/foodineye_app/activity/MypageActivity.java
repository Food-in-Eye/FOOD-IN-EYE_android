package com.example.foodineye_app.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.foodineye_app.R;

public class MypageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Button historyBtn = (Button) findViewById(R.id.order_history);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                startActivity(historyIntent);
            }
        });
    }
}