package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    List<Order> orderList;
    RecyclerView orderRecyclerview;
    OrderDetailAdapter orderDetailAdapter;

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

        //store에 해당하는 주문내용 get하기
        //OrderDetailAdapter에서 자식recyclerview 세팅할 때
        //detail get 못하면 상위 recyclerview 설정할 떄 같이 보내주기


    }
}