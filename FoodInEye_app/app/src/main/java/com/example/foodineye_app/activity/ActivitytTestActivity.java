package com.example.foodineye_app.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivitytTestActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activityt_test);

        Button button = (Button) findViewById(R.id.test);
        TextView textView = (TextView) findViewById(R.id.test_text);
        TextView textView1 = (TextView) findViewById(R.id.test_text1);
        TextView textView2 = (TextView) findViewById(R.id.test_text2);

        sharedPreferences = getSharedPreferences("test_token", MODE_PRIVATE);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String accessToken = sharedPreferences.getString("access_token", null);
                textView.setText(accessToken);

                if (accessToken != null) {

                    // Access Token을 헤더에 추가하여 보호된 데이터 요청
                    String accessTokenHeader = "Bearer " + accessToken;
                    textView1.setText(accessTokenHeader);

                    ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
                    Call<PostTestResponse> call = apiInterface.test(accessTokenHeader);

                    call.enqueue(new Callback<PostTestResponse>() {
                        @Override
                        public void onResponse(Call<PostTestResponse> call, Response<PostTestResponse> response) {
                            if (response.isSuccessful()) {
                                // 데이터 성공적으로 받아옴
                                PostTestResponse postTestResponse = response.body();
                                textView.setText(postTestResponse.toString());

                            } else {
                                // 데이터 요청 실패 처리

                                // 응답이 실패한 경우에 대한 처리를 여기서 수행합니다.
                                String errorBody = null; // 실패한 응답의 본문을 얻음
                                try {
                                    errorBody = response.errorBody().string();
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }

                                Log.i("Test", "test 오류"+errorBody);
                            }
                        }

                        @Override
                        public void onFailure(Call<PostTestResponse> call, Throwable t) {
                            // 통신 실패 처리
                            Log.i("Test", "test 오류: "+t.toString());
                        }
                    });



                }
            }
        });
    }
}