package com.example.foodineye_app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.foodineye_app.activity.HomeActivity;
import com.example.foodineye_app.data.PostRTokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RefreshTokenService extends Service {
    private static final int NOTIFICATION_ID = 1;
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable refreshTokenTask;
    SharedPreferences sharedPreferences;
    Context context;

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
//                handler.postDelayed(this, 50 * 60 * 1000); // 50분
                handler.postDelayed(this, 5 * 60 * 1000); // 5분
            }
        };

        // 최초 실행
        handler.post(refreshTokenTask);

        // Foreground Service로 설정
        startForeground(NOTIFICATION_ID, createNotification());
    }

    private void stopRefreshTokenScheduler() {
        handler.removeCallbacks(refreshTokenTask);
        stopForeground(true);
    }

    // Notification을 생성하는 메서드
    private Notification createNotification() {
        Intent notificationIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);

        // Android 8.0 이상에서는 Notification 채널 설정이 필요
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "channel_id";
            String channelName = "Channel Name";
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        return new NotificationCompat.Builder(this, "channel_id")
                .setContentTitle("Foreground Service")
                .setContentText("Refresh Token Service is running")
                .setSmallIcon(R.drawable.food_in_eye)
                .setContentIntent(pendingIntent)
                .build();
    }

    //Refresh Token 50분마다 재발급
    public void getRefreshToken(){
        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        String refreshTokenHedaer = "Bearer " + refreshToken;

        Log.d("RefreshTokenService", "refresh: "+refreshTokenHedaer);

        String u_id = sharedPreferences.getString("u_id", null);

        Log.d("RefreshTokenService", "u_id: "+u_id);

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

                    Log.d("RefreshTokenService", "new!!!!!!!refresh: "+newRToken);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("refresh_token", newRToken);
                    editor.apply();

                }else{
                    //데이터 요청 실패 처리
                    // HTTP 상태 코드에 따라 다른 메시지 표시
                    switch (response.code()) {
                        case 402:

//                            Log.d("ApiInterceptor", "402");
//
//                            // LoginActivity를 시작하기 위한 커스텀 이벤트를 브로드캐스트합니다.
//                            Intent intent = new Intent("start_login_activity");
//                            context.sendBroadcast(intent);
//                            break;

                        case 401:

//                            Log.d("ApiInterceptor", "401");
//
//                            // LoginActivity를 시작하기 위한 커스텀 이벤트를 브로드캐스트합니다.
//                            Intent intent1 = new Intent("start_login_activity");
//                            context.sendBroadcast(intent1);
//                            break;

                        case 403:

                            Log.d("ApiInterceptor", "403");

//                            // LoginActivity를 시작하기 위한 커스텀 이벤트를 브로드캐스트합니다.
//                            Intent intent2 = new Intent("start_login_activity");
//                            context.sendBroadcast(intent2);
//                            break;

                        default:
                            break;
                    }
                }
            }

            @Override
            public void onFailure(Call<PostRTokenResponse> call, Throwable t) {
                Log.d("RefreshTokenService", "Fail_error: "+t.toString());
            }
        });

    }
}
