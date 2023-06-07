package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;
import com.example.foodineye_app.WebSocketManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import visual.camp.sample.view.PointView;

public class OrderActivity extends AppCompatActivity {

    List<Order> orderList = new ArrayList<>();
    List<SubOrder> subOrderList;
    List<Cart> cartList;
    TextView totalPrice;
    int total;

    RecyclerView orderRecyclerview;
    OrderAdapter orderAdapter;
    Order order;

    List<PostOrder.StoreOrder> content = new ArrayList<>();
    List<PostOrder.StoreOrder.FoodCount> f_list;
    PostOrder postOrder;
    String u_id = "6458f67e50bde95733e4b57f";
    String history_id;

    //-----------------------------------------------------------------------------------------
    //gazetracker
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");
    JSONArray jsonGazeArray; //전체 gaze

    //-----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
        Context ctx = getApplicationContext();
        ConstraintLayout storeLayout = findViewById(R.id.orderLayout);
        PointView viewpoint = findViewById(R.id.view_point_order);

        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
        }


        //-------------------------------------------------------------------------------------

        //장바구니 리스트
        cartList = ((Data) getApplication()).getCartList();
        total = ((Data)getApplication()).getTotalPrice();

        //Cart 객체들을 s_id로 그룹화하여 HashMap에 담기
        HashMap<String, List<Cart>> groupByStore = new HashMap<>();
        for(Cart cart : cartList){
            String s_id = cart.getS_id();
            if(groupByStore.containsKey(s_id)){
                groupByStore.get(s_id).add(cart);
            }else{
                List<Cart> grouplist = new ArrayList<>();
                grouplist.add(cart);
                Log.d("OrderActivity", "grouplist"+grouplist.toString());
                groupByStore.put(s_id, grouplist);
            }
        }

        //그룹화된 Cart 객체들을 이용하여 Order 객체 생성 후 orderList에 추가
        for(Map.Entry<String, List<Cart>> entry : groupByStore.entrySet()){
            String s_id = entry.getKey();
            List<Cart> cartGroup = entry.getValue();
            String s_name = cartGroup.get(0).getS_name();
            String m_id = cartGroup.get(0).getM_id();
            subOrderList = new ArrayList<>();
            f_list = new ArrayList<>();
            for(Cart cart : cartGroup){
                String f_id = cart.getF_id();
                String m_name = cart.getM_name();
                int m_price = cart.getM_price();
                String m_imageKey = cart.getM_imageKey();
                int m_count = cart.getM_count();
                SubOrder subOrder = (SubOrder) new SubOrder(f_id, m_name, m_price, m_count);
                subOrderList.add(subOrder);
                //post
                PostOrder.StoreOrder.FoodCount foodCount = (PostOrder.StoreOrder.FoodCount) new PostOrder.StoreOrder.FoodCount(f_id, m_count, m_price);
                f_list.add(foodCount);
            }
            order = new Order(s_id, s_name, m_id, subOrderList);
            orderList.add(order);
            //post
            PostOrder.StoreOrder storeOrder = new PostOrder.StoreOrder(s_id, m_id, f_list);
            content.add(storeOrder);
        }

        //상위 recyclerview 설정
        orderRecyclerview = findViewById(R.id.recyclerView_orderList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderRecyclerview.setLayoutManager(layoutManager);

        orderAdapter = new OrderAdapter(getApplicationContext(), orderList);
        orderRecyclerview.setAdapter(orderAdapter);

        //총 가격
        totalPrice = (TextView) findViewById(R.id.order_totalPrice);
        totalPrice.setText(String.valueOf(total));

        //postOrder 세팅
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        //요청 바디에 들어가 PostOrder 객체 생성
        postOrder = new PostOrder(u_id, total, content);
        Log.d("OrderActivity", "postOrder" + postOrder.toString());


        ApiInterface apiInterface1 = ApiClient.getClient().create(ApiInterface.class);
        jsonGazeArray = Data.getJsonArray();

        //결제하기 버튼 클릭 + API 요청 보내기 + order, websocket, gaze
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.payBtn);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrderActivity", "결제하기!");
                Call<PostOrderResponse> call = apiInterface.createOrder(postOrder);
                call.enqueue(new Callback<PostOrderResponse>() {
                    @Override
                    public void onResponse(Call<PostOrderResponse> call, Response<PostOrderResponse> response) {
                        if(response.isSuccessful()){
                            //요청이 성공한 경우 처리할 작업

                            PostOrderResponse responseBody = response.body();
                            history_id = responseBody.getHistory_id();
                            List<PostOrderResponse.Response> responseList;
                            responseList = responseBody.getResponse();

                            Log.d("OrderActivity", "responseBody"+responseBody.toString());

                            //return 값 받기
                            //s_id에 맞는 order_id 넣기
                            for(Order order : orderList){
                                for(PostOrderResponse.Response result : responseList){
                                    if(order.getStoreId().equals(result.getS_id())){
                                        order.setOrderId(result.getO_id());
                                    }
                                }
                            }
                            Log.d("OrderActivity", "SubOrderList"+subOrderList.toString());
                            Log.d("OrderActivity", "OrderList"+orderList.toString());


                            //웹소켓 연결하기
                            Log.d("WebSocket", "history_id: "+history_id);
                            Log.d("WebSocket", "WebSocket 시도");

                            WebSocketManager.getInstance(getApplicationContext()).connectWebSocket(history_id);

                            // gaze 보내기
                            Call<PostGazeResponse> gazeCall = apiInterface1.createGaze(history_id, "True",jsonGazeArray);
                            gazeCall.enqueue(new Callback<PostGazeResponse>() {
                                @Override
                                public void onResponse(Call<PostGazeResponse> call, Response<PostGazeResponse> response) {
                                    if (response.isSuccessful() && response.body() != null) {
                                        // 성공적인 응답을 받은 경우
                                        //요청이 성공할 경우 처리할 작업
                                        PostGazeResponse postGazeResponse = response.body();
                                        Log.d("OrderActivity", "postGazeResponse: " +postGazeResponse.toString());
                                        // postGazeResponse를 사용하여 원하는 작업 수행
                                    } else {
                                        // 응답이 실패하거나 response.body()가 null인 경우
                                        Log.e("Response", "Response is unsuccessful or body is null");
                                    }
                                }

                                @Override
                                public void onFailure(Call<PostGazeResponse> call, Throwable t) {
                                    Log.d("OrderActivity", "postGazeResponse 전송 실패");
                                }
                            });



                        }
                    }

                    @Override
                    public void onFailure(Call<PostOrderResponse> call, Throwable t) {
                        //요청이 실패한 경우 처리할 작업
                        Log.d("OrderActivity", "요청실패!");
                    }
                });
                //Data orderList에 주문내용 추가하기
                final Data data = (Data) getApplicationContext();
                data.setOrderList(orderList);

                //OrderDetial 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                startActivity(intent);

            }//onClick
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("OrderActivity", "onStop");

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("order", 0, 0);
        }
//        gazeTracker.removeCallbacks(
//                gazeCallback, calibrationCallback, statusCallback, userStatusCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundThread.quitSafely();
    }

    //
    // Miscellaneous
    //

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}