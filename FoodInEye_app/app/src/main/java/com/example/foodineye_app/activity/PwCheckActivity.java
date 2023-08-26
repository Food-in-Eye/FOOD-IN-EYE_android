package com.example.foodineye_app.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.post.PostPw;
import com.example.foodineye_app.post.PostPwCheckResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PwCheckActivity extends AppCompatActivity {

    Toolbar toolbar;

    EditText editPw;
    String pw;
    Button editBtn;
    TextView noti;
    String u_id;
    String nickname, id;
    int gender, age;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pw_check);

        u_id = ((Data) getApplication()).getUser_id(); // 회원 고유의 ID

        toolbar = findViewById(R.id.pwcheck_toolbar);
        setToolBar(toolbar);

        editPw = (EditText) findViewById(R.id.pwcheck_pw);
        pw = editPw.getText().toString();

        editBtn = (Button) findViewById(R.id.pwcheck_btn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pwCheck(u_id);
            }
        });


    }

    //---------------------------------------------------------------------------------------------------
    //툴바
    public void setToolBar(Toolbar toolbar){
        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.pwcheck_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

    }

    public void pwCheck(String u_id){
        //비밀번호 확인 & response값으로 받은 아이디, 닉네임, 성별, 나이는 인텐트 값으로 넘겨주기
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        PostPw postPw = new PostPw(pw);
        Call<PostPwCheckResponse> call = apiInterface.pwCheck(u_id, postPw);

        call.enqueue(new Callback<PostPwCheckResponse>() {
            @Override
            public void onResponse(Call<PostPwCheckResponse> call, Response<PostPwCheckResponse> response) {
                if(response.isSuccessful()){
                    //데이터 성공 시
                    PostPwCheckResponse postPwCheckResponse = response.body();
                    nickname = postPwCheckResponse.getName();
                    id = postPwCheckResponse.getId();
                    gender = postPwCheckResponse.getGender();
                    age = postPwCheckResponse.getAge();

                    //인텐트 넘겨주기
                    Intent intent = new Intent(getApplicationContext(), MyinfoSettingActivity.class);
                    intent.putExtra("u_id", u_id);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("id", id);
                    intent.putExtra("gender", gender);
                    intent.putExtra("age", age);

                }else{
                    //데이터 실패 시
                    String errorBody = null; // 실패한 응답의 본문을 얻음
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if(errorBody.contains("Incorrect Pw")){
                        noti.setVisibility(View.VISIBLE);

                        // 배경색 원래대로 변경하기
                        GradientDrawable backgroundPw = (GradientDrawable) editPw.getBackground();
                        backgroundPw.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경

                    }else{
                        // 다른 오류 처리 로직을 추가
                    }
                }
            }

            @Override
            public void onFailure(Call<PostPwCheckResponse> call, Throwable t) {
                Log.i("PwCheckActivity", "비밀번호 검증 오류: " + t.toString());
            }
        });





    }

}