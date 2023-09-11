package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.foodineye_app.R;


public class HomeActivity extends AppCompatActivity {

    Context context;

    SharedPreferences sharedPreferences;
    int eye_permission;

    Data data;
    Boolean isOrder;

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        context = getApplicationContext();

        data = (Data) getApplication();


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

        //시선 권한 동의 여부 확인
        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        eye_permission = sharedPreferences.getInt("eye_permission", 0);

        //주문 가능한지
        if(data != null){
            Log.d("HomeActivity", "isOrder: "+data.isOrder());
            isOrder = data.isOrder();
        }else{
            data.setOrder(true);
        }


        LinearLayout orderBtn = (LinearLayout) findViewById(R.id.home_order);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isOrder){
                    //주문 가능하면 isOrder = true

                    if(eye_permission == 1){
                        checkCameraPermission();

                    }else if(eye_permission == 2){
                        //false
                        showDialog();

                    }else{
                        //null
                        //home -> camera
                        Intent cameraIntent = new Intent(getApplicationContext(), CameraActivity.class);
                        startActivity(cameraIntent);
                    }
                }else{
                    //주문 불가능
                    show("현재 진행중인 주문이 존재하기 때문에 주문을 진행 할 수 없습니다!");
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

    // 권한 체크하고 카메라 권한 요청
    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "카메라 권한에 동의했습니다.", Toast.LENGTH_SHORT).show();

            // 카메라 권한이 허용된 경우, CalibrationActivity로 이동
            Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
            startActivity(intent);
        } else {
            // 권한이 허용되지 않은 경우 권한 요청
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // 사용자가 이전에 권한 요청을 거절한 경우
                Toast.makeText(this, "시선추적을 하기 위해서는 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        }
    }

    // 권한 요청 결과 처리
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 사용자가 권한을 허용한 경우, CalibrationActivity로 이동
                Intent intent = new Intent(getApplicationContext(), CalibrationActivity.class);
                startActivity(intent);
            } else {
                // 사용자가 권한을 거부한 경우, 필요한 처리를 수행
                Toast.makeText(this, "카메라 권한이 거부되었습니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}