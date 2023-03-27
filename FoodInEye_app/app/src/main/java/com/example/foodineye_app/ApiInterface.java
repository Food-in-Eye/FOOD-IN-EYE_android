package com.example.foodineye_app;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("api/v1/user/stores")
    Call<StoreItem> getData();

}
