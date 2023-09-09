package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.foodineye_app.R;
import com.example.foodineye_app.gaze.PermissionRequester;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);



        // 권한을 먼저 확인하고, 권한이 없는 경우에만 권한 요청을 수행합니다.
        if (PermissionRequester.hasPermissions(this, Manifest.permission.CAMERA)) {
            // 권한이 이미 동의되어 있을 때의 처리
            Intent loginIntent = new Intent(getApplicationContext(), Calibration.class);
            startActivity(loginIntent);
        } else {
            // 권한이 없는 경우 권한 요청
            PermissionRequester.request(this);
            setContentView(R.layout.activity_camera);
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        }



    }
}