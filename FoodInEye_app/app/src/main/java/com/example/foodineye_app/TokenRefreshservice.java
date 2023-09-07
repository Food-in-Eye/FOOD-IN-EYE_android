package com.example.foodineye_app;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.foodineye_app.data.PostRTokenResponse;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TokenRefreshservice extends Service {
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshTokenTask;
    SharedPreferences sharedPreferences;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startRefreshTokenScheduler();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRefreshTokenScheduler();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void startRefreshTokenScheduler() {
        refreshTokenTask = new Runnable() {
            @Override
            public void run() {
                // 50분마다 Refresh Token 갱신
                getRefreshToken();

                // 다음 갱신 예약
                handler.postDelayed(this, 50 * 60 * 1000); // 50분

//                handler.postDelayed(this, 50 * 1000); //50초
            }
        };

        // 최초 실행
        handler.post(refreshTokenTask);
    }

    private void stopRefreshTokenScheduler() {
        handler.removeCallbacks(refreshTokenTask);
    }

    //Refresh Token 50분마다 재발급
    public void getRefreshToken(){
        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        String refreshTokenHedaer = "Bearer " + refreshToken;

        Log.d("TokenRefreshservice", "refresh: "+refreshTokenHedaer);

        String u_id = sharedPreferences.getString("u_id", null);

        Log.d("TokenRefreshservice", "u_id: "+u_id);

        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);
        Call<PostRTokenResponse> call = apiInterface.getNewRToken(u_id, refreshTokenHedaer);

        call.enqueue(new Callback<PostRTokenResponse>() {
            @Override
            public void onResponse(Call<PostRTokenResponse> call, Response<PostRTokenResponse> response) {
                if(response.isSuccessful()){
                    //데이터 요청 성공 처리
                    //new refresh_token 저장
                    PostRTokenResponse postRTokenResponse = response.body();
                    String newRToken = postRTokenResponse.getR_Token();

                    Log.d("TokenRefreshservice", "new!!!!!!!refresh: "+newRToken);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refresh_token", newRToken);
                    editor.apply();

                }else{
                    //데이터 요청 실패 처리
                    //데이터 실패 시
                    String errorBody = null; // 실패한 응답의 본문을 얻음
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    if(errorBody.contains("Signature verification failed.")){
                        Log.d("TokenRefreshservice", "error: "+errorBody);
                    }else if(errorBody.contains("Signature has expired.")){
                        Log.d("TokenRefreshservice", "error: "+errorBody);
                    }else if(errorBody.contains("Ownership verification failed.")){
                        Log.d("TokenRefreshservice", "error: "+errorBody);
                    }else{
                        Log.d("TokenRefreshservice", "error: "+errorBody);
                    }
                }
            }

            @Override
            public void onFailure(Call<PostRTokenResponse> call, Throwable t) {
                Log.d("TokenRefreshservice", "Fail_error: "+t.toString());
            }
        });



    }
}
