package com.example.foodineye_app;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("api/v1/user/stores")
    Call<StoreItem> getData();

    @GET("api/v1/user/menus/641d954618f0b258e9ca0263?food_opt=True")
    Call<MenuItem> getMenuData();

}
