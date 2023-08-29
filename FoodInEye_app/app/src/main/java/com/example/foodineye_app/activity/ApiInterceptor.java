package com.example.foodineye_app.activity;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.post.PostATokenResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class ApiInterceptor implements Interceptor {

    SharedPreferences sharedPreferences;
    private Context context; // 추가: Context 멤버 변수
    public ApiInterceptor(Context context) { // 추가: 생성자
        this.context = context;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        // 토큰 관련 로직을 추가하여 헤더를 수정
        Request modifiedRequest = addTokenToRequest(originalRequest);
        Log.d("header", "header: "+modifiedRequest.toString());

        Response response = chain.proceed(modifiedRequest);

        //Access 토큰 만료 등의 상황에 따른 작업 처리
        if (isTokenExpired(response)) {

            //Refresh 토큰으로 Access 토큰 갱신 시도
            refreshToken();

            // 갱신된 토큰으로 다시 요청 보내기
            modifiedRequest = addTokenToRequest(originalRequest);
            response = chain.proceed(modifiedRequest);
        }
        return response;
    }

    // 토큰을 요청에 추가하는 로직
    private Request addTokenToRequest(Request originalRequest) {
        Log.d("header", "!!!!!!!!!!!!!!!!!!!!!!!!!addTokenToRequest");
        // 현재 SharedPreferences에서 access_token을 가져오거나, 원하는 방식으로 토큰을 얻어옵니다.
        sharedPreferences = context.getSharedPreferences("test_token1", MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", null);

        // 복제본을 만듭니다.
        Request.Builder builder = originalRequest.newBuilder();
        // Authorization 헤더에 토큰을 추가합니다.
        builder.header("Authorization", "Bearer " + accessToken);

        // 새로운 Request 객체를 생성하고 반환합니다.
        return builder.build();
    }


    // 토큰 만료 여부 확인
    private boolean isTokenExpired(Response response) {
        // 여기에 토큰 만료 여부 확인 로직 작성

        switch (response.code()) {
            case 401:
                return true; //"detail": "Signature has expired."
            default:
                return false;
        }
    }


    //Refresh 토큰으로 Access 토큰 갱신
    public void refreshToken() {
        // 여기에 토큰 갱신 로직 작성
        sharedPreferences = context.getSharedPreferences("test_token1", MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        String refreshTokenHedaer = "Bearer " + refreshToken;

        Log.d("TokenRefresh", "refresh: "+refreshTokenHedaer);

        String u_id = ((Data) context.getApplicationContext()).getUser_id();

        Log.d("TokenRefresh", "u_id: "+u_id);
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<PostATokenResponse> call = apiInterface.getNewAToken(u_id, refreshTokenHedaer);

        call.enqueue(new Callback<PostATokenResponse>() {
            @Override
            public void onResponse(Call<PostATokenResponse> call, retrofit2.Response<PostATokenResponse> response) {
                if(response.isSuccessful()){
                    //데이터 요청 성공 처리
                    //new access_token 저장
                    PostATokenResponse postATokenResponse = response.body();
                    String newAToken = postATokenResponse.getA_Token();

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("access_token", newAToken);
                    editor.apply();

                }else{
                    //데이터 요청 실패 처리
                    // HTTP 상태 코드에 따라 다른 메시지 표시
                    switch (response.code()) {
                        case 404:
                            Log.d("InfoSet", "error(404): " + response.errorBody().toString());
                            break;
                        case 400:
                            Log.d("InfoSet", "error(400): " + response.errorBody().toString());
                            break;
                        case 401:
                            String errorBody = response.errorBody().toString();
                            if(errorBody.contains("Signature verification failed.")){
                                Log.d("TokenAccess", "error: "+errorBody);
                            }else if(errorBody.contains("Signature has expired.")){
                                Log.d("TokenAccess", "error: "+errorBody);
                            }else if(errorBody.contains("Ownership verification failed.")){
                                Log.d("TokenAccess", "error: "+errorBody);
                            }else{
                                Log.d("TokenAccess", "error: "+errorBody);
                            }

                        default:
                            break;

                    }

                }
            }


            @Override
            public void onFailure(Call<PostATokenResponse> call, Throwable t) {
                // 실패 처리

            }
        });

    }
}
