package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.R;

public class Intro0Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro0);

        // 1초(1000ms) 동안 현재 화면을 표시한 후 다음 화면으로 전환
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 다음 화면으로 이동
                Intent intent = new Intent(Intro0Activity.this, IntroActivity.class); // NextActivity는 다음 화면의 클래스명으로 변경해야 합니다.
                startActivity(intent);
            }
        }, 2000); // 1000ms(1초) 딜레이
    }
}
