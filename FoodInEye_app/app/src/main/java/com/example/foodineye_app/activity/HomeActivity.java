package com.example.foodineye_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.R;


public class HomeActivity extends AppCompatActivity {

    Context context;
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = getApplicationContext();

//        PermissionRequester.request(this);
//        setContentView(R.layout.activity_home);
//        ActivityCompat.requestPermissions(this, new String[]
//                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);


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

//        LinearLayout orderBtn = (LinearLayout) findViewById(R.id.home_order);
//        orderBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //home -> storelist
//                Intent loginIntent = new Intent(getApplicationContext(), Calibration.class);
//                startActivity(loginIntent);
//            }
//        });

//        LinearLayout orderBtn = (LinearLayout) findViewById(R.id.home_order);
//        orderBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // 권한을 먼저 확인하고, 권한이 없는 경우에만 권한 요청을 수행합니다.
//                if (PermissionRequester.hasPermissions(context,Manifest.permission.CAMERA)) {
//                    // 권한이 이미 동의되어 있을 때의 처리
//                    Intent loginIntent = new Intent(getApplicationContext(), Calibration.class);
//                    startActivity(loginIntent);
//                } else {
//                    // 권한이 없는 경우 권한 요청
//                    PermissionRequester.request((Activity) context);
//                    setContentView(R.layout.activity_home);
//                    ActivityCompat.requestPermissions((Activity) context, new String[]
//                            {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
//                }
//            }
//        });

        LinearLayout orderBtn = (LinearLayout) findViewById(R.id.home_order);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //home -> storelist
                Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(cameraIntent);
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