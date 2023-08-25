package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodineye_app.R;

public class PwCheckActivity extends AppCompatActivity {

    Toolbar toolbar;

    EditText editPw;
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



        //인텐트 넘겨주기
        Intent intent = new Intent(getApplicationContext(), MyinfoSettingActivity.class);
        intent.putExtra("u_id", u_id);
        intent.putExtra("nickname", nickname);
        intent.putExtra("id", id);
        intent.putExtra("gender", gender);
        intent.putExtra("age", age);


    }

}