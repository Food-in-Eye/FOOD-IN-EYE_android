package com.example.foodineye_app;

import com.example.foodineye_app.activity.History;
import com.example.foodineye_app.activity.HistoryDetail;
import com.example.foodineye_app.activity.MenuItem;
import com.example.foodineye_app.activity.OrderItem;
import com.example.foodineye_app.activity.PostOrder;
import com.example.foodineye_app.activity.PostOrderResponse;
import com.example.foodineye_app.activity.StoreItem;
import com.example.foodineye_app.gaze.PostGaze;
import com.example.foodineye_app.gaze.PostGazeResponse;

import java.util.List;

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

    @POST("api/v2/orders/order/gaze")
    Call<PostGazeResponse> createGaze(
            @Query("h_id") String h_id,
            @Body List<PostGaze> postGaze
    );

//    @GET("api/v2/orders/historys")
//    Call<History> getHistory(
//            @Query("u_id") String u_id,
//            @Query("batch") int batch
//    );

    @GET("api/v2/orders/test/historys")
    Call<History> getHistory(
            @Query("u_id") String u_id,
            @Query("batch") int batch
    );

    @GET("api/v2/orders/history")
    Call<HistoryDetail> getHistoryDetail(
            @Query("id") String history_id
    );

}
