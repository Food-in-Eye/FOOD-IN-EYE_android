package com.example.foodineye_app;

import android.view.Menu;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {

    @GET("api/v2/stores/")
    Call<StoreItem> getData();

    @GET("api/v2/menus/menu/foods")
    Call<MenuItem> getMenusData(@Query("id") String m_id);

    @POST("api/v2/orders/order")
    Call<PostOrderResponse> createOrder(@Body PostOrder postOrder);

    @GET("api/v2/orders/order")
    Call<OrderItem> getOrder(
            @Query("id") String o_id,
            @Query("detail") String isDetail);

//    @GET("api/v2/orders/order")
//    Call<OrderItem> getOrder(@QueryMap Map<String, String> queryParams);

}
