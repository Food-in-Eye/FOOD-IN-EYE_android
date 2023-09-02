package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.foodineye_app.ApiClientEx;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.post.PostATokenResponse;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;

public class ApiInterceptor implements Interceptor {

    private SharedPreferences sharedPreferences;
    private Context context;
    private Request originalRequest;

    public ApiInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        originalRequest = chain.request();

        // 토큰 관련 로직을 추가하여 헤더를 수정
        Request modifiedRequest = addTokenToRequest(originalRequest);
        Log.d("header", "header: " + modifiedRequest.toString());

        Response response = chain.proceed(modifiedRequest);

        // Access 토큰 만료 등의 상황에 따른 작업 처리
        if (isTokenExpired(response)) {
            // Refresh 토큰으로 Access 토큰 갱신 시도
            Response refreshTokenResponse = refreshToken();

            // 갱신된 토큰으로 다시 요청 보내기
            if (refreshTokenResponse != null) {
                // 이전 응답을 닫기
                response.close();

                // 토큰 갱신 후에도 원래 요청을 다시 보냅니다.
                Response retryResponse = chain.proceed(addTokenToRequest(originalRequest));

                // 성공적으로 다시 시도한 경우 결과를 반환합니다.
                return retryResponse;
            } else {
                // 갱신에 실패하면 에러 응답 반환
                return response;
            }
        }

        return response;
    }


    // 토큰을 요청에 추가하는 로직
    private Request addTokenToRequest(Request originalRequest) {
        // 현재 SharedPreferences에서 access_token을 가져오거나, 원하는 방식으로 토큰을 얻어옵니다.
        sharedPreferences = context.getSharedPreferences("test_token1", Context.MODE_PRIVATE);
        String accessToken = sharedPreferences.getString("access_token", null);

        Log.d("ApiInterceptor", "addTokenToRequest_access_token: " + accessToken);

        // 복제본을 만듭니다.
        Request.Builder builder = originalRequest.newBuilder();

        // Authorization 헤더에 토큰을 추가합니다.
        builder.header("Authorization", "Bearer " + accessToken);

        // 새로운 Request 객체를 생성하고 반환합니다.
        return builder.build();
    }

    // 토큰 만료 여부 확인
    private boolean isTokenExpired(Response response) {
        return response.code() == 401; // 예시로 401 코드가 토큰 만료 코드라 가정합니다.
    }


    // Refresh 토큰으로 Access 토큰 갱신
    private Response refreshToken() throws IOException {
        // 여기에 토큰 갱신 로직 작성
        sharedPreferences = context.getSharedPreferences("test_token1", Context.MODE_PRIVATE);
        String refreshToken = sharedPreferences.getString("refresh_token", null);
        String refreshTokenHeader = "Bearer " + refreshToken;
        Log.d("ApiInterceptor", "refresh 가져오기: " + refreshTokenHeader);

        String u_id = sharedPreferences.getString("u_id", null);
        Log.d("ApiInterceptor", "u_id: " + u_id);

        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);
        Call<PostATokenResponse> call = apiInterface.getNewAToken(u_id, refreshTokenHeader);

        retrofit2.Response<PostATokenResponse> response = call.execute(); // 동기적으로 토큰 갱신 요청 보냄

        if (response.isSuccessful()) {
            // 데이터 요청 성공 처리
            // new access_token 저장
            PostATokenResponse postATokenResponse = response.body();
            String newAccessToken = postATokenResponse.getA_Token();

            SharedPreferences.Editor editor = sharedPreferences.edit();

            // 기존 access_token 삭제
            editor.remove("access_token");
            editor.putString("access_token", newAccessToken);
            editor.apply();
            Log.d("ApiInterceptor", "새로운 AccessToken: " + newAccessToken);

            return response.raw();
        } else {
            // 데이터 요청 실패 처리
            Log.d("ApiInterceptor", "postATokenResponse: " + response.errorBody().toString());
            // HTTP 상태 코드에 따라 다른 메시지 표시
            switch (response.code()) {
                case 404:
                    Log.d("InfoSet", "error(404): " + response.errorBody().toString());
                    break;
                case 400:
                    Log.d("InfoSet", "error(400): " + response.errorBody().toString());
                    break;
                case 401:
                    String errorBody = response.errorBody().string();

                    // 에러가 발생했을 때 인터셉터 수정 부분
                    if (errorBody.contains("Signature verification failed.")) {
                        Log.d("ApiInterceptor", "서명 검증 실패.");

                        // LoginActivity를 시작하기 위한 커스텀 이벤트를 브로드캐스트합니다.
                        Intent intent = new Intent("start_login_activity");
                        context.sendBroadcast(intent);

                    } else if (errorBody.contains("Signature has expired.")) {
                        Log.d("ApiInterceptor", "Signature has expired.");



                    } else if (errorBody.contains("Ownership verification failed.")) {
                        Log.d("ApiInterceptor", "Ownership verification failed.");
                    } else {
                        Log.d("ApiInterceptor", "error: " + errorBody);
                    }
                    break;
                default:
                    break;
            }
            return null;
        }
    }

}
