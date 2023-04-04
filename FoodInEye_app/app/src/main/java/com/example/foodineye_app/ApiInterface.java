package com.example.foodineye_app;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiInterface {

    @GET("api/v1/user/stores")
    Call<StoreItem> getData();

    @GET("api/v1/user/menus/642506edcc80984129fe020a?food_opt=True")
    Call<MenuItem> getMenuData();

}
