package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.GetOrder;
import com.example.foodineye_app.data.Order;
import com.example.foodineye_app.websocket.WebSocketManager;

import java.util.ArrayList;
import java.util.List;

import okhttp3.WebSocket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    Toolbar toolbar;

    List<Order> orderList;
    RecyclerView orderRecyclerview;
    OrderDetailAdapter orderDetailAdapter;

    //WebSocket
    WebSocket webSocket;
    Data data;
    String h_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        toolbar = (Toolbar) findViewById(R.id.order_detail_toolbar);
        setToolBar(toolbar);

        data = (Data) getApplication();

        h_id = data.getHistory_id();
//        Log.d("OrderDetail", "orderdetail: "+data.getHistory_id());
//        getOrder(data.getHistory_id()); //h_id로 GET하기

        setOrderRecyclerview();

    }
    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.order_detail_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.order_detail_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> home
                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }

    private void getOrder(String h_id){

        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);
        Call<GetOrder> call = apiInterface.getOrder(h_id);
        call.enqueue(new Callback<GetOrder>() {
            @Override
            public void onResponse(Call<GetOrder> call, Response<GetOrder> response) {
                if(response.isSuccessful()){

                    data.initializeAllVariables();
                    data.setHistory_id(h_id);
                    // 총 주문 내역 불러오기
                    List<GetOrder.nOrder> orderList = response.body().orderLists;
                    List<Order> orderList1 = new ArrayList<>();

                    for (GetOrder.nOrder order : orderList) {
                        // Order 객체에서 필요한 정보를 추출합니다.
                        String orderId = order.o_id;
                        String storeId = order.s_id;
                        String storeName = order.s_name;
                        String menuId = order.m_id;
                        int status = order.status;

                        // SubOrder 목록을 가져옵니다.
                        List<GetOrder.nOrder.FoodList> foodList = order.f_list;
                        List<SubOrder> subOrderList = new ArrayList<>();

                        // SubOrder 목록 출력
                        for (GetOrder.nOrder.FoodList foodItem : foodList) {
                            String foodId = foodItem.f_id;
                            String foodName = foodItem.name;
                            int price = foodItem.price;
                            int count = foodItem.count;

                            SubOrder subOrder = new SubOrder(foodId, foodName, price, count);
                            subOrderList.add(subOrder);
                        }
                        Order newOrder = new Order(storeId, storeName, menuId, subOrderList);
                        newOrder.setOrderId(orderId);
                        newOrder.setStatus(status);

                        orderList1.add(newOrder);
                        data.setOrderList(orderList1);


                        Log.d("OrderDetailActivity", "modify!!!!!!!!!login: "+data.getOrderList());
                        setOrderRecyclerview();

//                        connectWebSocket(h_id);
                    }
                }else{
//                    show("현재 주문 내역이 없습니다.");
                }

            }

            @Override
            public void onFailure(Call<GetOrder> call, Throwable t) {

            }
        });
    }

    //recyclerview setting
    public void setOrderRecyclerview(){
        //주문내역 store 받기
        orderList = data.getOrderList();

        //상위 recyclerview 설정
        orderRecyclerview = findViewById(R.id.recyclerView_orderDetailList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderRecyclerview.setLayoutManager(layoutManager);

        orderDetailAdapter = new OrderDetailAdapter(getApplicationContext(), orderList);
        orderRecyclerview.setAdapter(orderDetailAdapter);

        //WebSocket으로 받은 메시지 확인하기
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null && extras.getString("order update") != null) {
            orderDetailAdapter.updateOrderList();
        }
    }

    //웹소켓 연결
    public void connectWebSocket(String history_id){
        //웹소켓 연결하기
        Log.d("WebSocket", "history_id: "+history_id);
        Log.d("WebSocket", "WebSocket 시도");

        WebSocketManager.getInstance(getApplicationContext()).connectWebSocket(history_id);
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}