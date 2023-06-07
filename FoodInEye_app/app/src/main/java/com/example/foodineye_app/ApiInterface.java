package com.example.foodineye_app;

import com.example.foodineye_app.activity.MenuItem;
import com.example.foodineye_app.activity.OrderItem;
import com.example.foodineye_app.activity.PostGazeResponse;
import com.example.foodineye_app.activity.PostOrder;
import com.example.foodineye_app.activity.PostOrderResponse;
import com.example.foodineye_app.activity.StoreItem;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

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

    @POST("api/v2/orders/order/gaze")
    Call<PostGazeResponse> createGaze(
            @Query("h_id") String h_id,
            @Query("save") String isSave,
            @Body JSONArray jsonGazeArray
    );

}
