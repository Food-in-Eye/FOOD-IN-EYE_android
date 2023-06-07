package com.example.foodineye_app;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
    static {
        httpClientBuilder.followRedirects(false); // 리디렉션 무시 설정
    }
    private static OkHttpClient httpClient = httpClientBuilder.build();

    public static String BASE_URL="http://10.0.2.2:8000/";
//    public static String BASE_URL="http://203.252.213.200:2020/";
//    public static String BASE_URL = "http://192.168.219.200:2020/";

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
