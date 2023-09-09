package com.example.foodineye_app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.foodineye_app.ApiClientEx;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.RefreshTokenService;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editId, editPw;
    TextView errorId, errorPw;

    String id, password;

    String at, rt; //tokens
    String user_id;
    Boolean eye_permission;

    private SharedPreferences sharedPreferences;

    // 앱의 메인 코드(예: 애플리케이션 클래스 또는 액티비티)에서 BroadcastReceiver를 등록합니다.
    private BroadcastReceiver startLoginActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("start_login_activity".equals(intent.getAction())) {
                // 여기서 LoginActivity를 시작합니다.
                startLoginActivity(context);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 커스텀 이벤트를 수신하기 위한 BroadcastReceiver를 등록합니다.
        IntentFilter filter = new IntentFilter("start_login_activity");
        registerReceiver(startLoginActivityReceiver, filter);

        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);

        //아이디 입력
        editId = (EditText) findViewById(R.id.login_id);
        errorId = (TextView) findViewById(R.id.login_availableId);
        editId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editId.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editId.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }
            @Override
            public void afterTextChanged(Editable s) {
                id = s.toString();

            }
        });
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + id);

        //패스워드 입력
        editPw = (EditText) findViewById(R.id.login_pw);
        errorPw = (TextView) findViewById(R.id.login_availablePw);
        editPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editId.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editId.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();

            }
        });

        //로그인하기
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                HomeActivity로 이동
//                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(loginIntent);
                login();
            }
        });

        //회원가입하기
        TextView textView = (TextView) findViewById(R.id.sign_up);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singupIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(singupIntent);
            }
        });
    }

    //로그인하기
    private void login(){

        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);

        Call<PostLoginResponse> call = apiInterface.login(id, password);
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + id);
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + password);

        call.enqueue(new Callback<PostLoginResponse>() {
            @Override
            public void onResponse(Call<PostLoginResponse> call, Response<PostLoginResponse> response) {
                if (response.isSuccessful()) {
                    PostLoginResponse postLoginResponse = response.body();
                    if (postLoginResponse != null) {
                        Log.d("LoginActivity", "로그인 서버 성공: " + postLoginResponse.toString());

                        at = postLoginResponse.getA_Token();
                        rt = postLoginResponse.getR_Token();
                        user_id = postLoginResponse.getUser_id(); // 회원 고유의 ID
//                        eye_permission = postLoginResponse.isEye_permssion(); //회원 시선 수집 동의 여부


                        // Access Token과 Refresh Token 저장
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", at);
                        editor.putString("refresh_token", rt);
                        editor.putString("u_id", user_id);

                        editor.apply();

//                        //로그인 후 R_Token Handler 실행
//                        startService(new Intent(getApplicationContext(), TokenRefreshservice.class));

                        // 로그인 후 R_Token Handler 실행
                        Intent serviceIntent = new Intent(getApplicationContext(), RefreshTokenService.class);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            // 안드로이드 Oreo (API 레벨 26) 이상에서는 startForegroundService 사용
                            startForegroundService(serviceIntent);
                        } else {
                            // Oreo 이전 버전에서는 그냥 startService 사용
                            startService(serviceIntent);
                        }


                        //HomeActivity로 이동
                        Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(loginIntent);

                    } else {
                        Log.d("LoginActivity", "로그인 응답 오류: response.body()가 null입니다.");
                    }
                } else {

                    // 응답이 실패한 경우에 대한 처리를 여기서 수행합니다.
                    String errorBody = null; // 실패한 응답의 본문을 얻음
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.d("LoginActivity", "로그인 응답 오류: " + response.code() + ", " + errorBody);

                    // 여기서 오류 메시지를 해석하고 사용자에게 알맞은 안내를 제공합니다.
                    if (errorBody.contains("Nonexistent ID")) {
                        show("아이디가 존재하지 않습니다!");
                        errorId.setVisibility(View.VISIBLE);
                        // 배경색 원래대로 변경하기
                        GradientDrawable backgroundId = (GradientDrawable) editId.getBackground();
                        backgroundId.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경

                        // 배경색 원래대로 변경하기
                        GradientDrawable backgroundPw = (GradientDrawable) editPw.getBackground();
                        backgroundPw.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경

                    } else if (errorBody.contains("Incorrect PW")) {
                        errorPw.setVisibility(View.VISIBLE);
                        show("비밀번호가 일치하지 않습니다!");

                        // 배경색 원래대로 변경하기
                        GradientDrawable backgroundPw = (GradientDrawable) editPw.getBackground();
                        backgroundPw.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경

                    } else {
                        // 다른 오류 처리 로직을 추가할 수 있습니다.
                    }
                }
            }

            @Override
            public void onFailure(Call<PostLoginResponse> call, Throwable t) {
                Log.i("LoginActivity", "로그인 응답 오류: " + t.toString());
            }
        });

    }

    // ApiInterceptor 클래스 내부에서
    private void startLoginActivity(Context context) {
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginIntent);
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}