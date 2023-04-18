package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity implements CartAdapter.OnItemClickListener {

    List<Cart> cartList;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    TextView totalPrice;

    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cartList = ((Data) getApplication()).getCartList();
        total = ((Data)getApplication()).getTotalPrice();

        Log.d("ShoppingCart", "total" + total);
        Log.d("ShoppingCart", "cartList" + cartList.toString());

//        Intent intent = getIntent();
//        cartList = (List<Cart>) intent.getSerializableExtra("intent_toCart");
//        Log.d("ShoppingCartActivity", "cart: "+cartList.toString());

        recyclerView = findViewById(R.id.recyclerView_cartList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        cartAdapter = new CartAdapter(getApplicationContext(), cartList, this);
        recyclerView.setAdapter(cartAdapter);

        //총 가격
        totalPrice = (TextView) findViewById(R.id.cart_totalPrice);
        totalPrice.setText(String.valueOf(total));


    }
    @Override
    public void onItemClick(){
        int total = ((Data)getApplication()).getTotalPrice();
        totalPrice.setText(String.valueOf(total));
    }
}