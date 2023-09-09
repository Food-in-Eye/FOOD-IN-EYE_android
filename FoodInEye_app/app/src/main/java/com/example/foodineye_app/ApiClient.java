package com.example.foodineye_app;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();

    // 추가: 생성자에서 context를 전달받도록 변경
    public ApiClient(Context context) {
        this.context = context;

        // HttpLoggingInterceptor를 추가하여 헤더와 내용을 로그로 출력
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);
    }

    //--------------------------------------------------------

    private Context context; // 변경: Context 멤버 변수
    private static OkHttpClient httpClient;

    // HttpClient 초기화를 생성자 내에서 수행하도록 변경
    public void initializeHttpClient() {
        httpClient = httpClientBuilder
                .addInterceptor(new ApiInterceptor(context))
                .build();
    }

    //--------------------------------------------------------
//        public static String BASE_URL="http://10.0.2.2:8000/";
    public static String BASE_URL="http://203.252.213.200:4040/";
//    public static String BASE_URL = "http://192.168.219.200:4040/";
//        public static String BASE_URL="http://127.0.0.1:8000/";

    private static Retrofit retrofit;

    public static Retrofit getClient(){

        if(retrofit == null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient) // 변경된 부분
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
