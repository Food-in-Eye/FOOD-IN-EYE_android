package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.foodineye_app.R;
import com.example.foodineye_app.data.PutEyePermission;
import com.example.foodineye_app.gaze.PermissionRequester;

public class CameraActivity extends AppCompatActivity {

    CheckBox agreeCK, disagreeCK;
    Boolean eyePermission;
    Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        agreeCK = (CheckBox) findViewById(R.id.camera_agree);
        disagreeCK = (CheckBox) findViewById(R.id.camera_disagree);

        agreeCK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeCK.setChecked(true);
                disagreeCK.setChecked(false);

                isPermission();

                eyePermission = true;

            }
        });

        disagreeCK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disagreeCK.setChecked(false);
                agreeCK.setChecked(true);

                eyePermission = false;
            }
        });

        nextBtn = (Button) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                putCheck();

                if(eyePermission){

                    //home -> calibration
                    Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
                    startActivity(intent);

                }else{

                    //home -> storelist
                    Intent intent = new Intent(getApplicationContext(), StorelistActivity.class);
                    startActivity(intent);

                }
            }
        });


    }

    //checkbox 결과 PUT 보내기
    public void putCheck(){

        PutEyePermission putEyePermission = new PutEyePermission(u_id, eyePermission);


    }

    //권한 체크하고 카메라 권한 요청
    public void isPermission(){

        // 권한을 먼저 확인하고, 권한이 없는 경우에만 권한 요청을 수행합니다.
        if (PermissionRequester.hasPermissions(this, Manifest.permission.CAMERA)) {
            // 권한이 이미 동의되어 있을 때의 처리
            Intent loginIntent = new Intent(getApplicationContext(), CalibrationActivity.class);
            startActivity(loginIntent);
        } else {
            // 권한이 없는 경우 권한 요청
            PermissionRequester.request(this);
            setContentView(R.layout.activity_camera);
            ActivityCompat.requestPermissions(this, new String[]
                    {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

            //권한 요청했는데 허용하지 않음을 체크하면 checkbox -> disagree
        }
    }
}