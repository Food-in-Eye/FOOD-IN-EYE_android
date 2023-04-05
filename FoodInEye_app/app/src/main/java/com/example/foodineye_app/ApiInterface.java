package com.example.foodineye_app;

import android.view.Menu;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiInterface {

    @GET("api/v1/user/stores")
    Call<StoreItem> getData();

    @GET("api/v1/user/menus/642506edcc80984129fe020a")
    Call<MenuItem> getMenuData();

    @GET("api/v1/user/menus/{m_id}")
    Call<MenuItem> getMenusData(@Path("m_id") String m_id);
}
