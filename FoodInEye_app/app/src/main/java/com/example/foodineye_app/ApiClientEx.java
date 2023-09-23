package com.example.foodineye_app;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientEx {

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    static {
        httpClientBuilder.followRedirects(false); // 리디렉션 무시 설정

        // HttpLoggingInterceptor를 추가하여 헤더와 내용을 로그로 출력
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClientBuilder.addInterceptor(loggingInterceptor);
    }

    private static OkHttpClient httpClientex = httpClientBuilder.build();

//        public static String BASE_URL="http://10.0.2.2:8000/";
    public static String BASE_URL="http://203.252.213.200:4040/"; //외부 서버
//    public static String BASE_URL = "http://192.168.219.200:4040/";   //내부 서버
//        public static String BASE_URL="http://127.0.0.1:8000/";

    private static Retrofit retrofit;

    public static Retrofit getExClient(){

        if(retrofit == null){
            retrofit=new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClientex)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}