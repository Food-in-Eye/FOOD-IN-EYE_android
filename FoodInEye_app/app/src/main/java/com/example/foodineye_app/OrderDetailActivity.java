package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.List;

import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    List<Order> orderList;
    RecyclerView orderRecyclerview;
    OrderDetailAdapter orderDetailAdapter;
    
    //WebSocket
    WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //주문내역 store 받기
        orderList = ((Data) getApplication()).getOrderList();

        //WebSocket으로 받은 메시지 확인하기
        Intent intent = getIntent();
        WebSocketData webSocketData = intent.getParcelableExtra("webSocketData");

        //상위 recyclerview 설정
        orderRecyclerview = findViewById(R.id.recyclerView_orderDetailList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderRecyclerview.setLayoutManager(layoutManager);

        orderDetailAdapter = new OrderDetailAdapter(getApplicationContext(), orderList, webSocketData);
        orderRecyclerview.setAdapter(orderDetailAdapter);
        

    }
}