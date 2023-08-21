package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editId, editPw;

    String id, password;

    String at, rt; //tokens
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //아이디 입력
        editId = (EditText) findViewById(R.id.login_id);
        id = editId.getText().toString();

        //패스워드 입력
        editPw = (EditText) findViewById(R.id.login_pw);
        password = editPw.getText().toString();

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

        call.enqueue(new Callback<PostLoginResponse>() {
            @Override
            public void onResponse(Call<PostLoginResponse> call, Response<PostLoginResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if(response.body().getStatus().equals("ERROR")){
                            Log.i("LoginActivity", "로그인 status Error : "+response.body().toString());
                        }else{
                            Log.i("LoginActivity", "로그인 : "+response.body().toString());

                            PostLoginResponse.Token token = response.body().tokens;
                            ((Data)getApplication()).setUser_id(token.getUser_id());
                            //access token
                            at = token.getAT();
                            //refresh token
                            rt = token.getRT();
                        }

                    } else {
                        // response.body()가 null이면 오류 처리
                        Log.i("LoginActivity", "로그인 응답 오류: 응답 본문이 null입니다.");
                    }
                } else {
                    // 서버 응답이 성공이 아닌 경우 오류 처리
                    Log.i("LoginActivity", "로그인 응답 오류: " + response.code());
                }
            }


            @Override
            public void onFailure(Call<PostLoginResponse> call, Throwable t) {
                Log.i("LoginActivity", "로그인 응답 오류: "+t.toString());
            }
        });

    }
    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}