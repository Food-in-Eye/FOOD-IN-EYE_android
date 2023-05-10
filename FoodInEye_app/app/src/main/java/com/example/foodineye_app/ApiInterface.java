package com.example.foodineye_app;

import android.view.Menu;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("api/v2/stores/")
    Call<StoreItem> getData();

    @GET("api/v2/menus/menu/foods")
    Call<MenuItem> getMenusData(@Query("id") String m_id);

//    @POST("api/v2/orders/order")
//    Call<PostOrder> createOrder(@Body PostOrder postOrder);

//    @POST("api/v2/orders/order")
//    Call<ResponseBody> createOrder(@Body PostOrder postOrder);

    @POST("api/v2/orders/order")
    Call<PostOrderResponse> createOrder(@Body PostOrder postOrder);
}
