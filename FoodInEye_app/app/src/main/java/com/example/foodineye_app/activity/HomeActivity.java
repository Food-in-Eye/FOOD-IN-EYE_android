package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.foodineye_app.R;


public class HomeActivity extends AppCompatActivity {

    Context context;

    SharedPreferences sharedPreferences;
    String eye_permission; //null, true, false



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = getApplicationContext();


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


        //시선 권한 동의 여부 확인
        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        eye_permission = sharedPreferences.getString("eye_permission", null);

        LinearLayout orderBtn = (LinearLayout) findViewById(R.id.home_order);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(eye_permission.equals("true")){
                    checkCameraPermission();
                    //home -> calibration
                    Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
                    startActivity(intent);

                }else if(eye_permission.equals("false")){
                    //false
                    showDialog();

                }else{
                    //null
                    //home -> camera
                    Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                    startActivity(cameraIntent);
                }

            }
        });


        LinearLayout calibrationBtn = (LinearLayout) findViewById(R.id.home_calibration);
        calibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent calibrationIntent = new Intent(getApplicationContext(), CalibrationActivity.class);
                startActivity(calibrationIntent);
            }
        });
    }


    //false일 때 질척이기
    public void showDialog(){

        LayoutInflater layoutInflater = LayoutInflater.from(HomeActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_camera, null);

        AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this, R.style.CustomAlertDialog)
                .setView(view)
                .create();

        TextView contiTxt = view.findViewById(R.id.camera_alert_continue);
        TextView agreeTxt = view.findViewById(R.id.camera_alert_agree);
        ImageView delete = view.findViewById(R.id.camera_alert_delete);

        contiTxt.setText("유지하기");
        agreeTxt.setText("동의하러 가기");

        contiTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storelist
                Intent intent = new Intent(getApplicationContext(), StorelistActivity.class);
                startActivity(intent);
            }
        });

        agreeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // -> camera
                Intent intent = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "카메라 권한 동의", Toast.LENGTH_SHORT).show();
            // 권한이 이미 허용된 경우
//            startCalibrationActivity();

        } else {
            // 권한이 허용되지 않은 경우
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // 사용자가 이전에 권한 요청을 거절한 경우
                Toast.makeText(this, "시선 수집 이용 약관에 동의를 하셨습니다. 카메라 권한을 동의 해주세요.", Toast.LENGTH_SHORT).show();
            }

            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MODE_PRIVATE);
        }
    }

}