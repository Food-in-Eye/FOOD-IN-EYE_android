package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        Button s1m1Btn = (Button) findViewById(R.id.s1m1Btn);
        s1m1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s1m1Intent = new Intent(getApplicationContext(), MenuDetailActivity.class);
                startActivity(s1m1Intent);
            }
        });
    }
}