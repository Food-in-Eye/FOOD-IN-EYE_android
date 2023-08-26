package com.example.foodineye_app;

import com.example.foodineye_app.activity.History;
import com.example.foodineye_app.activity.HistoryDetail;
import com.example.foodineye_app.activity.MenuItem;
import com.example.foodineye_app.activity.OrderItem;
import com.example.foodineye_app.activity.PostId;
import com.example.foodineye_app.activity.PostIdResponse;
import com.example.foodineye_app.activity.PostLoginResponse;
import com.example.foodineye_app.activity.PostOrder;
import com.example.foodineye_app.activity.PostOrderResponse;
import com.example.foodineye_app.activity.PostSignup;
import com.example.foodineye_app.activity.PostSignupResponse;
import com.example.foodineye_app.activity.PostTestResponse;
import com.example.foodineye_app.activity.StoreItem;
import com.example.foodineye_app.gaze.PostGaze;
import com.example.foodineye_app.gaze.PostGazeResponse;
import com.example.foodineye_app.post.PostPw;
import com.example.foodineye_app.post.PostPwCheckResponse;
import com.example.foodineye_app.post.PostRTokenResponse;
import com.example.foodineye_app.post.PutMyInfoSet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
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

    @GET("api/v2/orders/historys")
    Call<History> getHistory(
            @Query("u_id") String u_id,
            @Query("batch") int batch
    );

//    @GET("api/v2/orders/test/historys")
//    Call<History> getHistory(
//            @Query("u_id") String u_id,
//            @Query("batch") int batch
//    );

    @GET("api/v2/orders/history")
    Call<HistoryDetail> getHistoryDetail(
            @Query("id") String history_id
    );

    @POST("api/v2/users/idcheck")
    Call<PostIdResponse> idCheck(
            @Body PostId postGaze
    );

    @POST("api/v2/users/buyer/signup")
    Call<PostSignupResponse> signUp(
            @Body PostSignup postSignup
    );

    @FormUrlEncoded
    @POST("api/v2/users/buyer/login")
    Call<PostLoginResponse> login(
            @Field("username") String username,
            @Field("password") String password
    );

    @POST("api/v2/users/info")
    Call<PostPwCheckResponse> pwCheck(
            @Query("u_id") String u_id,
            @Body PostPw postPw
    );

    @PUT("api/v2/users/buyer/change")
    Call setInfo(
            @Query("u_id") String u_id,
            @Body PutMyInfoSet putMyInfoSet
    );


    @GET("api/v2/users/test/a_token")
    Call<PostTestResponse> test(
            @Header("Authorization") String authorizationHeader
    );

    @GET("api/v2/users/issue/refresh")
    Call<PostRTokenResponse> getNewRToken(
            @Query("u_id") String u_id,
            @Header("Authorization") String authorizationHeader
    );



}
