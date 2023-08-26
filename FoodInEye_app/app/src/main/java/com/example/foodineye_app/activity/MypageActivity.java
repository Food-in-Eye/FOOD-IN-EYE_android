package com.example.foodineye_app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.R;
import com.example.foodineye_app.TokenRefreshservice;

public class MypageActivity extends AppCompatActivity {
    Button logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);

        Button setinfoBtn = (Button) findViewById(R.id.setmyinfo);
        setinfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent setInfoIntent = new Intent(getApplicationContext(), PwCheckActivity.class);
                startActivity(setInfoIntent);
            }
        });


        Button historyBtn = (Button) findViewById(R.id.order_history);
        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent historyIntent = new Intent(getApplicationContext(), OrderHistoryActivity.class);
                startActivity(historyIntent);
            }
        });

        //로그아웃
        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });
    }

    public void logout(){
        //로그아웃 후 R_Token Handler 중지
        stopService(new Intent(getApplicationContext(), TokenRefreshservice.class));

        // SharedPreferences 객체 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit(); // SharedPreferences 에디터 가져오기
        editor.clear(); // 모든 데이터 삭제

        editor.apply();// 변경사항 저장

        Intent intent = new Intent(getApplicationContext(), LogoutActivity.class);
        startActivity(intent);

    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}