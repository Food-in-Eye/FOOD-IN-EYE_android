package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    Button idCheckBtn;
    TextView loginTxt;
    EditText editId;
    TextView availId;
    TextView unavailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        availId = (TextView) findViewById(R.id.availableId);
        unavailId = (TextView) findViewById(R.id.unavailableId);

        //아이디 중복 여부 확인하기
        idCheckBtn = (Button) findViewById(R.id.signup_idcheck);
        idCheckBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCheck(editId.getText().toString());

            }
        });



        //로그인하기 버튼 클릭
        loginTxt = (TextView) findViewById(R.id.login);
        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginIntent);
            }
        });
    }

    //아이디 중복 여부 함수
    public void idCheck(String id){

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        PostId postId = new PostId(id);

        Call<PostIdResponse> call = apiInterface.idCheck(postId);
        call.enqueue(new Callback<PostIdResponse>() {
            @Override
            public void onResponse(Call<PostIdResponse> call, Response<PostIdResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    // 성공적인 응답을 받은 경우
                    PostIdResponse postIdResponse = response.body();

                    //id 중복일경우 -> unavailable, 중복아닐경우 -> available
                    if (postIdResponse.getResponse().equals("available")) {
                        availId.setVisibility(View.VISIBLE);
                    }else if (postIdResponse.getResponse().equals("unavailable")){
                        unavailId.setVisibility(View.VISIBLE);
                    }else{
                        Log.i("SignupActivity", "서버 응답 오류");
                    }
                }else{
                    // 응답이 실패하거나 response.body()가 null인 경우
                    Log.i("SignupActivity", "서버 응답 오류");
                }
            }
            @Override
            public void onFailure(Call<PostIdResponse> call, Throwable t) {
                Log.i("SignupActivity", "서버 응답 오류: " + t.toString());
            }
        });





    }
}