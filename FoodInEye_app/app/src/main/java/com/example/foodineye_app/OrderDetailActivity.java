package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.List;

public class OrderDetailActivity extends AppCompatActivity {

    List<Order> orderList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        //주문내역 store 받기
        orderList = ((Data) getApplication()).getOrderList();


        //store에 해당하는 주문내용 get하기

    }
}