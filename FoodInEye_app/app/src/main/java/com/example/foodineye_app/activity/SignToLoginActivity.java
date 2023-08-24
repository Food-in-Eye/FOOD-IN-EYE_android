package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.R;

public class SignToLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_to_login);

        Button signToLoginBtn = (Button) findViewById(R.id.sign_to_login);
        signToLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signToLoginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(signToLoginIntent);
            }
        });


    }
}