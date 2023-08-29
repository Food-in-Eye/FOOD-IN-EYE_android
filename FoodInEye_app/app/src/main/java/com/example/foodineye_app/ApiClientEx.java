package com.example.foodineye_app;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClientEx {

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    static {
        httpClientBuilder.followRedirects(false); // 리디렉션 무시 설정
    }

    private static OkHttpClient httpClientex = httpClientBuilder.build();

    public static String BASE_URL="http://10.0.2.2:8000/";
//    public static String BASE_URL="http://203.252.213.200:4040/";
//    public static String BASE_URL="http://203.252.213.200:2020/";
//    public static String BASE_URL = "http://192.168.219.200:4040/";
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
