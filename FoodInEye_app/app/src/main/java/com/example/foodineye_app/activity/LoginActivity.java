package com.example.foodineye_app.activity;

import android.content.Intent;
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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editId, editPw;
    TextView errorId, errorPw;

    String id, password;

    String at, rt; //tokens
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
//                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(loginIntent);
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
                    if(response.body() != null){
                        if(response.body().getDetail().equals("Nonexistent ID")){
                            show("아이디가 일치하지 않습니다!");

                            errorId.setVisibility(View.VISIBLE);
                            //배경색 원래대로 변경하기
                            GradientDrawable backgroundId = (GradientDrawable) editId.getBackground();
                            backgroundId.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경

                            //배경색 원래대로 변경하기
                            GradientDrawable backgroundPw = (GradientDrawable) editPw.getBackground();
                            backgroundPw.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경

                        }else if(response.body().getDetail().equals("Incorrect PW")){

                            errorPw.setVisibility(View.VISIBLE);
                            show("비밀번호가 일치하지 않습니다!");

                            //배경색 원래대로 변경하기
                            GradientDrawable backgroundPw = (GradientDrawable) editPw.getBackground();
                            backgroundPw.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경

                        }else{
                            PostLoginResponse postLoginResponse = response.body();
                            ((Data)getApplication()).setUser_id(postLoginResponse.getUser_id()); //회원 고유의 ID

                            at = postLoginResponse.getA_Token();
                            rt = postLoginResponse.getR_Token();
                        }
                    }else{
                        Log.i("LoginActivity", "로그인 응답 오류: " + response.body().toString());
                    }

                } else {
                    // 서버 응답이 성공이 아닌 경우 오류 처리
                    Log.i("LoginActivity", "로그인 응답 오류: " + response.code());
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