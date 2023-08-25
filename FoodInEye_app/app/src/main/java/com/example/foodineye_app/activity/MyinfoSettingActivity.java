package com.example.foodineye_app.activity;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.example.foodineye_app.R;

import java.util.regex.Pattern;

public class MyinfoSettingActivity extends AppCompatActivity {

    Toolbar toolbar;

    EditText editNickname, editId, editOldPw, editNewPw, editRePw, editAge;
    ToggleButton maleBtn, femaleBtn;

    String u_id;
    String nickname, password;
    int gender, age;

    TextView availPw, samePw;
    TextView unavailPw;

    Button setInfoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo_setting);

        toolbar = findViewById(R.id.setmyinfo_toolbar);
        setToolBar(toolbar);

        editNickname = (EditText)findViewById(R.id.setmyinfo_nickname);
        editId = (EditText) findViewById(R.id.setmyinfo_id);
        editOldPw = (EditText) findViewById(R.id.setmyinfo_pw);
        editNewPw = (EditText) findViewById(R.id.setmyinfo_new_pw);
        editRePw = (EditText) findViewById(R.id.setmyinfo_re_pw);
        editAge = (EditText) findViewById(R.id.setmyinfo_age);

    //인텐트로 받은 기본 정보값

        Intent intent = getIntent();

        u_id = intent.getExtras().getString("u_id");
        editNickname.setHint(intent.getExtras().getString("nickname"));
        editId.setHint(intent.getExtras().getString("id"));
        editAge.setHint(intent.getExtras().getInt("age"));

    //닉네임 작성
        editNickname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editNickname.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editNickname.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }

            @Override
            public void afterTextChanged(Editable s) {
                String afterNickname = s.toString();
                nickname = afterNickname;

                //edittext 배경 stroke 색상 변경하기
                GradientDrawable background = (GradientDrawable) editNickname.getBackground();
                background.setStroke(3, ContextCompat.getColor(getApplicationContext(), R.color.green));
            }
        });

    //이전 패스워드 == 새 패스워드 & 유효성 검사
        availPw = (TextView) findViewById(R.id.setmyinfo_availablePw); //8자리 이상, 숫자와 특수문자가~
        samePw = (TextView) findViewById(R.id.setmyinfo_samePw); //이전 비밀번호와 동일

        editNewPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                pwCheckValidation(password);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    //새 패스워드 == 새 패스워드 확인 검사
        unavailPw = (TextView) findViewById(R.id.setmyinfo_unavailable_pw);
        editOldPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                pwReCheck(password);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    //성별 체크
        maleBtn = (ToggleButton) findViewById(R.id.signup_male_togglebtn);
        femaleBtn = (ToggleButton) findViewById(R.id.signup_female_togglebtn);

        //인텐트값으로 변경
        int intentGender;
        intentGender = intent.getExtras().getInt("gender");
        if (intentGender == 1){
            femaleBtn.setChecked(false);
        }else{
            maleBtn.setChecked(false);
        }
        gender = intentGender;

        maleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(maleBtn.isChecked()){
                    femaleBtn.setChecked(false);
                    gender = 1;
                }
            }
        });

        femaleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(femaleBtn.isChecked()){
                    maleBtn.setChecked(false);
                    gender = 2;
                }
            }
        });

    //나이 작성
        editAge.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editAge.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //배경색 원래대로 변경하기
                GradientDrawable background = (GradientDrawable) editAge.getBackground();
                background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
            }

            @Override
            public void afterTextChanged(Editable s) {
                String editage = s.toString();

                if(!editage.isEmpty()){
                    try{
                        age = Integer.parseInt(editage);
                    }catch (NumberFormatException e){
                        // 정수로 변환할 수 없는 경우에 대한 예외 처리
                    }

                    //edittext 배경 stroke 색상 변경하기
                    GradientDrawable background = (GradientDrawable) editAge.getBackground();
                    background.setStroke(3, ContextCompat.getColor(getApplicationContext(), R.color.green));
                }else{
                    //edittext 배경 stroke 색상 변경하기
                    GradientDrawable background = (GradientDrawable) editAge.getBackground();
                    background.setStroke(3, ContextCompat.getColor(getApplicationContext(), R.color.light_gray));
                }

            }
        });

    //내 정보 수정 버튼 클릭
        setInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMyInfo();

            }
        });

    }

    //---------------------------------------------------------------------------------------------------
    //툴바
    public void setToolBar(Toolbar toolbar){
        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.setmyinfo_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // 메뉴 항목을 클릭했을 때의 동작을 처리합니다.
        switch (item.getItemId()) {
            case R.id.action_back:
                show("뒤로가기 버튼");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //---------------------------------------------------------------------------------------------------
    //패스워드 유효성 검사
    public void pwCheckValidation(String editpassword){

        // 8자리 이상, 숫자, 특수문자, 영문자 대문자 포함 검사
        availPw.setVisibility(View.VISIBLE);
        String regex = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z]).{8,}$";
        if (Pattern.compile(regex).matcher(editpassword).matches()) {

            availPw.setVisibility(View.INVISIBLE);

            //새 비밀번호와 이전 비밀번호가 동일한지 확인
            pwCheck(editpassword);

        } else {
            availPw.setVisibility(View.VISIBLE);
            availPw.setTextColor(ContextCompat.getColor(this, R.color.red));

            //배경색 원래대로 변경하기
            GradientDrawable background = (GradientDrawable) editRePw.getBackground();
            background.setStroke(2, ContextCompat.getColor(this, R.color.light_gray)); // 원래 배경 색상으로 변경
        }
    }

    //이전 비밀번호 == 새 비밀번호 확인 검사
    public void pwCheck(String rePassword){
        if(rePassword.equals(editOldPw.getText().toString())){
            samePw.setVisibility(View.VISIBLE);

            // 비밀번호가 일치하지 않을 때 배경색 원래대로 변경하기
            GradientDrawable background = (GradientDrawable) editRePw.getBackground();
            background.setStroke(2, ContextCompat.getColor(this, R.color.light_gray)); // 원래 배경 색상으로 변경
        }else{
            samePw.setVisibility(View.INVISIBLE);

            //edittext 배경 stroke 색상 변경하기
            GradientDrawable background = (GradientDrawable) editNewPw.getBackground();
            background.setStroke(3, ContextCompat.getColor(this, R.color.green));
        }
    }

    //새 비밀번호 == 새 비밀번호 확인 검사
    public void pwReCheck(String rePassword){
        if(rePassword.equals(editNewPw.getText().toString())){
            unavailPw.setVisibility(View.INVISIBLE);

            //edittext 배경 stroke 색상 변경하기
            GradientDrawable background = (GradientDrawable) editRePw.getBackground();
            background.setStroke(3, ContextCompat.getColor(this, R.color.green));
        }else{
            unavailPw.setVisibility(View.VISIBLE);

            // 비밀번호가 일치하지 않을 때 배경색 원래대로 변경하기
            GradientDrawable background = (GradientDrawable) editRePw.getBackground();
            background.setStroke(2, ContextCompat.getColor(this, R.color.light_gray)); // 원래 배경 색상으로 변경
        }
    }

    //새로운 정보 POST
    public void setMyInfo(){

        //POST 보낸 후 성공하면
        Intent intent = new Intent(getApplicationContext(), MypageActivity.class);
        startActivity(intent);
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}