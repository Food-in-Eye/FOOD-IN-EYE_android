package com.example.foodineye_app.activity;

import android.content.Intent;
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

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;

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

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedPreferences = getSharedPreferences("test_token", MODE_PRIVATE);

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
                //edittext 배경 stroke 색상 변경하기
                GradientDrawable background = (GradientDrawable) editId.getBackground();
                background.setStroke(3, ContextCompat.getColor(getApplicationContext(), R.color.green));
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
                //edittext 배경 stroke 색상 변경하기
                GradientDrawable background = (GradientDrawable) editId.getBackground();
                background.setStroke(3, ContextCompat.getColor(getApplicationContext(), R.color.green));
            }
        });

        //로그인하기
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<PostLoginResponse> call = apiInterface.login(id, password);
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + id);
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + password);

        call.enqueue(new Callback<PostLoginResponse>() {
            @Override
            public void onResponse(Call<PostLoginResponse> call, Response<PostLoginResponse> response) {
                if (response.isSuccessful()) {
                    PostLoginResponse postLoginResponse = response.body();
                    if (postLoginResponse != null) {
                        Log.i("LoginActivity", "로그인 서버 성공: " + postLoginResponse.toString());
                        ((Data) getApplication()).setUser_id(postLoginResponse.getUser_id()); // 회원 고유의 ID
                        at = postLoginResponse.getA_Token();
                        rt = postLoginResponse.getR_Token();

                        // Access Token과 Refresh Token 저장
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", at);
                        editor.putString("refresh_token", rt);
                        editor.apply();

                        Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(loginIntent);

//                        Intent loginIntent = new Intent(getApplicationContext(), ActivitytTestActivity.class);
//                        startActivity(loginIntent);

                    } else {
                        Log.i("LoginActivity", "로그인 응답 오류: response.body()가 null입니다.");
                    }
                } else {

                    // 응답이 실패한 경우에 대한 처리를 여기서 수행합니다.
                    String errorBody = null; // 실패한 응답의 본문을 얻음
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.i("LoginActivity", "로그인 응답 오류: " + response.code() + ", " + errorBody);

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
    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}