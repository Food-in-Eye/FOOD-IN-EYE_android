package com.example.foodineye_app;

import com.example.foodineye_app.activity.PostLoginResponse;
import com.example.foodineye_app.data.GetHistoryDetail;
import com.example.foodineye_app.data.GetMenu;
import com.example.foodineye_app.data.GetOrder;
import com.example.foodineye_app.data.GetStoreList;
import com.example.foodineye_app.data.History;
import com.example.foodineye_app.data.PostATokenResponse;
import com.example.foodineye_app.data.PostId;
import com.example.foodineye_app.data.PostIdResponse;
import com.example.foodineye_app.data.PostOrder;
import com.example.foodineye_app.data.PostOrderResponse;
import com.example.foodineye_app.data.PostPw;
import com.example.foodineye_app.data.PostPwCheckResponse;
import com.example.foodineye_app.data.PostRTokenResponse;
import com.example.foodineye_app.data.PostSignup;
import com.example.foodineye_app.data.PostSignupResponse;
import com.example.foodineye_app.data.PutEyePermission;
import com.example.foodineye_app.data.PutMyInfoSet;
import com.example.foodineye_app.data.checkOrderResponse;
import com.example.foodineye_app.gaze.PostGaze;
import com.example.foodineye_app.gaze.PostGazeResponse;

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
    Call<GetStoreList> getStore();

    @GET("api/v2/menus/menu/foods")
    Call<GetMenu> getMenusData(@Query("id") String m_id);

    @POST("api/v2/orders/order")
    Call<PostOrderResponse> createOrder(@Body PostOrder postOrder);

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

    @GET("api/v2/orders/history")
    Call<GetHistoryDetail> getHistoryDetail(
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

    @POST("api/v2/users/buyer/info")
    Call<PostPwCheckResponse> pwCheck(
            @Query("u_id") String u_id,
            @Body PostPw postPw
    );

    @PUT("api/v2/users/buyer/change")
    Call<Void> setInfo(
            @Query("u_id") String u_id,
            @Body PutMyInfoSet putMyInfoSet
    );

    @GET("api/v2/users/issue/refresh")
    Call<PostRTokenResponse> getNewRToken(
            @Query("u_id") String u_id,
            @Header("Authorization") String authorizationHeader
    );

    @GET("api/v2/users/issue/access")
    Call<PostATokenResponse> getNewAToken(
            @Query("u_id") String u_id,
            @Header("Authorization") String authorizationHeader
    );

    @PUT("api/v2/users/buyer/camera")
    Call<Void> putEyePermission(
            @Query("u_id") String u_id,
            @Body PutEyePermission putEyePermission
    );

    //h_id로 현재 진행 주문 불러오기
    @GET("api/v2api/v2/orders/order/h")
    Call<GetOrder> getOrder(
            @Query("id") String h_id
    );


    //h_id로 현재 주문이 끝났는지 true, 진행중인지
    @GET("api/v2api/v2/orders/history/status")
    Call<checkOrderResponse> checkOrderStatus(
            @Query("id") String h_id
    );
}