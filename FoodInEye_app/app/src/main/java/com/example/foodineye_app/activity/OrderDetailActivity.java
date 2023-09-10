package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.R;
import com.example.foodineye_app.data.Order;
import com.example.foodineye_app.websocket.UpdateWebSocketModel;

import java.util.List;

import okhttp3.WebSocket;

public class OrderDetailActivity extends AppCompatActivity {

    Toolbar toolbar;

    List<Order> orderList;
    RecyclerView orderRecyclerview;
    OrderDetailAdapter orderDetailAdapter;

    //WebSocket
    WebSocket webSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        toolbar = (Toolbar) findViewById(R.id.order_detail_toolbar);
        setToolBar(toolbar);

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

}