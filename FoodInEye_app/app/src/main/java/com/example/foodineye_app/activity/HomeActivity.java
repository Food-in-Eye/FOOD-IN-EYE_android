package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.GazeTrackerManager;
import com.example.foodineye_app.R;
import com.example.foodineye_app.gaze.PermissionRequester;

import camp.visual.gazetracker.constant.AccuracyCriteria;
import visual.camp.sample.view.PointView;

//calibration
import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.constant.CalibrationModeType;
import visual.camp.sample.view.CalibrationViewer;


public class HomeActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //camera permissionrequester
        PermissionRequester.request(this);
        setContentView(R.layout.activity_home);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);


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
                //home -> storelist
                Intent loginIntent = new Intent(getApplicationContext(), StorelistActivity.class);
                startActivity(loginIntent);
            }
        });

        LinearLayout calibrationBtn = (LinearLayout) findViewById(R.id.home_calibration);
        calibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calibrationIntent = new Intent(getApplicationContext(), Calibration.class);
                startActivity(calibrationIntent);
            }
        });
    }
}