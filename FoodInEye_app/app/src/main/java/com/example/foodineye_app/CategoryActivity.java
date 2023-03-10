package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CategoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Button s1Btn = (Button) findViewById(R.id.sBtn);
        s1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s1Intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(s1Intent);
            }
        });
    }
}