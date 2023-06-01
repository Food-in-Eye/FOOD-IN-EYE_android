package com.example.foodineye_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.foodineye_app.R;
import com.example.foodineye_app.UpdateWebSocketModel;

import java.util.List;

import okhttp3.WebSocket;

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

        //상위 recyclerview 설정
        orderRecyclerview = findViewById(R.id.recyclerView_orderDetailList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderRecyclerview.setLayoutManager(layoutManager);

        orderDetailAdapter = new OrderDetailAdapter(getApplicationContext(), orderList);
        orderRecyclerview.setAdapter(orderDetailAdapter);



        //WebSocket으로 받은 메시지 확인하기
        Intent intent = getIntent();
        UpdateWebSocketModel updateWebSocketModel = (UpdateWebSocketModel) intent.getSerializableExtra("updateWebSocketModel");

        // 웹소켓 메시지를 받았을 때만 RecyclerView 업데이트
        if (updateWebSocketModel != null) {
            orderDetailAdapter.setUpdateWebSocketModel(updateWebSocketModel);
//            // 새로운 주문 목록을 받아온다고 가정
//            List<Order> newOrderList = getUpdatedOrderList(updateWebSocketModel);
            orderDetailAdapter.updateOrderList();
        }


    }

}