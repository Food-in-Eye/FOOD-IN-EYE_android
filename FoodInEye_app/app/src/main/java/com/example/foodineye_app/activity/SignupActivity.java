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
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.foodineye_app.ApiClientEx;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.PostId;
import com.example.foodineye_app.data.PostIdResponse;
import com.example.foodineye_app.data.PostSignup;
import com.example.foodineye_app.data.PostSignupResponse;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    //닉네임
    EditText editNickname;

    //아이디
    Button idCheckBtn;
    EditText editId;
    TextView availId, unavailId;

    //비밀번호
    EditText editPw;
    TextView availPw;

    //비밀번호 확인
    EditText editRePw;
    TextView unavilRePw;

    //성별 체크
    ToggleButton maleBtn, femaleBtn;

    //나이
    EditText editAge;

    //PostBuyer
    String nickname, id, password;
    int gender, age;

    //회원가입하기, 로그인하기
    Button signupBtn;
    TextView loginTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //닉네임 작성
        editNickname = (EditText) findViewById(R.id.signup_nickname);
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

        //아이디 중복 여부 확인하기
        editId = (EditText) findViewById(R.id.signup_id);
        availId = (TextView) findViewById(R.id.availableId);
        unavailId = (TextView) findViewById(R.id.unavailableId);

        idCheckBtn = (Button) findViewById(R.id.signup_idcheck);
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCheck(editId.getText().toString());
            }
        });

        //패스워드 유효성 검사
        editPw = (EditText) findViewById(R.id.signup_pw);
        availPw = (TextView) findViewById(R.id.availablePw);

        // edittext 변결될 때마다 검사 수행
        editPw.addTextChangedListener(new TextWatcher() {
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

        //패스워드 확인
        editRePw = (EditText) findViewById(R.id.signup_re_pw);
        unavilRePw = (TextView) findViewById(R.id.unavailable_pw);
        editRePw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String rePassword = s.toString();
                pwCheck(rePassword);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //성별 체크
        maleBtn = (ToggleButton) findViewById(R.id.signup_male_togglebtn);
        femaleBtn = (ToggleButton) findViewById(R.id.signup_female_togglebtn);

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
        editAge = (EditText) findViewById(R.id.signup_age);
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

        //회원가입하기 버튼 클릭
        signupBtn = (Button) findViewById(R.id.signupBtn);
        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });


        //로그인하기 버튼 클릭
        loginTxt = (TextView) findViewById(R.id.loginBtn);
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    //아이디 중복 여부 함수
    public void idCheck(String editID){

        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);
        PostId postId = new PostId(editID);

        Call<PostIdResponse> call = apiInterface.idCheck(postId);
        call.enqueue(new Callback<PostIdResponse>() {
            @Override
            public void onResponse(Call<PostIdResponse> call, Response<PostIdResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    // 성공적인 응답을 받은 경우
                    PostIdResponse postIdResponse = response.body();

                    //id 중복아닐경우 -> available, 중복일경우 -> unavailable
                    if (postIdResponse.getState().equals("available")) {
                        availId.setVisibility(View.VISIBLE);
                        unavailId.setVisibility(View.INVISIBLE);

                        //edittext 배경 stroke 색상 변경하기
                        GradientDrawable background = (GradientDrawable) editId.getBackground();
                        background.setStroke(3, ContextCompat.getColor(getApplicationContext(), R.color.green));

                        id = editID;

                    }else if (postIdResponse.getState().equals("unavailable")){
                        unavailId.setVisibility(View.VISIBLE);
                        availId.setVisibility(View.INVISIBLE);

                        //배경색 원래대로 변경하기
                        GradientDrawable background = (GradientDrawable) editId.getBackground();
                        background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.red)); // 원래 배경 색상으로 변경
                    }else{
                        Log.i("SignupActivity", "아이디 중복 체크 서버 응답 오류");

                        //배경색 원래대로 변경하기
                        GradientDrawable background = (GradientDrawable) editId.getBackground();
                        background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
                    }
                }else{
                    // 응답이 실패하거나 response.body()가 null인 경우
                    Log.i("SignupActivity", "아이디 중복 체크 서버 응답 오류");
                    //배경색 원래대로 변경하기
                    GradientDrawable background = (GradientDrawable) editId.getBackground();
                    background.setStroke(2, ContextCompat.getColor(getApplicationContext(), R.color.light_gray)); // 원래 배경 색상으로 변경
                }
            }
            @Override
            public void onFailure(Call<PostIdResponse> call, Throwable t) {
                Log.i("SignupActivity", "아이디 중복 체크 서버 응답 오류: " + t.toString());
            }
        });

    }

    //패스워드 유효성 검사
    public void pwCheckValidation(String editpassword){

        // 8자리 이상, 숫자, 특수문자, 영문자 대문자 포함 검사
        availPw.setVisibility(View.VISIBLE);
        String regex = "^(?=.*[0-9])(?=.*[!@#$%^&*])(?=.*[A-Z]).{8,}$";
        if (Pattern.compile(regex).matcher(editpassword).matches()) {

            availPw.setVisibility(View.INVISIBLE);

            //edittext 배경 stroke 색상 변경하기
            GradientDrawable background = (GradientDrawable) editPw.getBackground();
            background.setStroke(3, ContextCompat.getColor(this, R.color.green));

            password = editpassword;

        } else {
            availPw.setVisibility(View.VISIBLE);
            availPw.setTextColor(ContextCompat.getColor(this, R.color.red));

            //배경색 원래대로 변경하기
            GradientDrawable background = (GradientDrawable) editRePw.getBackground();
            background.setStroke(2, ContextCompat.getColor(this, R.color.light_gray)); // 원래 배경 색상으로 변경
        }
    }

    //패스워드 확인 검사
    public void pwCheck(String rePassword){
        if(rePassword.equals(editPw.getText().toString())){
            unavilRePw.setVisibility(View.INVISIBLE);
            //edittext 배경 stroke 색상 변경하기
            GradientDrawable background = (GradientDrawable) editRePw.getBackground();
            background.setStroke(3, ContextCompat.getColor(this, R.color.green));
        }else{
            unavilRePw.setVisibility(View.VISIBLE);

            // 비밀번호가 일치하지 않을 때 배경색 원래대로 변경하기
            GradientDrawable background = (GradientDrawable) editRePw.getBackground();
            background.setStroke(2, ContextCompat.getColor(this, R.color.light_gray)); // 원래 배경 색상으로 변경
        }
    }

    //회원가입하기
    public void signUp(){

        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);
        PostSignup postSignup = new PostSignup(id, password, nickname, gender, age);
        Log.i("SignupActivity", "postBuyer"+postSignup.toString());

        Call<PostSignupResponse> call = apiInterface.signUp(postSignup);
        call.enqueue(new Callback<PostSignupResponse>() {
            @Override
            public void onResponse(Call<PostSignupResponse> call, Response<PostSignupResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    PostSignupResponse postSignupResponse = response.body();
                    if(postSignupResponse.getDetail() != null && postSignupResponse.getDetail().equals("Duplicate ID")){
                        show("입력된 id가 중복되었습니다.");
                    }else{
                        show("회원가입되었습니다!");

                        Intent signToLoginIntent = new Intent(getApplicationContext(), SignToLoginActivity.class);
                        startActivity(signToLoginIntent);
                    }

                }else{
                    Log.i("SignupActivity", "회원가입 중복 체크 서버 응답 오류");
                }
            }

            @Override
            public void onFailure(Call<PostSignupResponse> call, Throwable t) {
                Log.i("SignupActivity", "회원가입 서버 응답 오류: " + t.toString());
            }
        });


    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}