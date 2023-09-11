package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.PutEyePermission;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CameraActivity extends AppCompatActivity {

    Toolbar toolbar;
    CheckBox agreeCK, disagreeCK;
    int eyeP;
    Button nextBtn;

    SharedPreferences sharedPreferences;
    String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.camera_toolbar);
        setToolBar(toolbar);

        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        u_id = sharedPreferences.getString("u_id", null);
        eyeP = sharedPreferences.getInt("eye_permission", 0);

        agreeCK = (CheckBox) findViewById(R.id.camera_agree);
        disagreeCK = (CheckBox) findViewById(R.id.camera_disagree);

        agreeCK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //동의했을 때
                if (agreeCK.isChecked()) {
                    disagreeCK.setChecked(false);
                    checkCameraPermission();
                    eyeP = 1; //true
                }
            }
        });

        disagreeCK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //동의하지 않았을 때
                if (disagreeCK.isChecked()) {
                    agreeCK.setChecked(false);
                    eyeP = 2; //false
                }
            }
        });

        Log.d("CameraActivity", "ceyePermission: "+eyeP);

        nextBtn = (Button) findViewById(R.id.next_btn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                putCheck();
                if(eyeP == 1){
                    //동의
//                    startCalibrationActivity();
                }else if(eyeP ==2){
                    //비동의
                    startStorelistActivity();
                }else{

                }
            }
        });

    }

    //checkbox 결과 PUT 보내기
    public void putCheck(){

        SharedPreferences.Editor editor = sharedPreferences.edit();

        // 기존 access_token 삭제
        editor.remove("eye_permission");
        editor.putInt("eye_permission", eyeP);
        editor.apply();

        PutEyePermission putEyePermission = new PutEyePermission(eyeP);

        // 초기 페이지 로딩
        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);

        Call<Void> call = apiInterface.putEyePermission(u_id, putEyePermission);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()){
                    Log.d("CameraActivity", "cPUT: 200 OK");
                    startCalibrationActivity();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.d("CameraActivity", "conFailure: " + t.toString());
            }
        });


    }

    //권한 체크하고 카메라 권한 요청
    private void checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "카메라 권한 항상 동의", Toast.LENGTH_SHORT).show();
            // 권한이 이미 허용된 경우
//            startCalibrationActivity();

        } else {
            // 권한이 허용되지 않은 경우
            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                // 사용자가 이전에 권한 요청을 거절한 경우
                Toast.makeText(this, "시선추적을 하기 위해서는 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }

            // 권한 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MODE_PRIVATE);
        }

    }

    public void startCalibrationActivity(){
        Intent intent = new Intent(this, CalibrationActivity.class);
        startActivity(intent);
        finish();
    }

    public void startStorelistActivity(){
        Intent intent = new Intent(this, StorelistActivity.class);
        startActivity(intent);
        finish();
    }


    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.camera_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.camera_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> home
                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }
}