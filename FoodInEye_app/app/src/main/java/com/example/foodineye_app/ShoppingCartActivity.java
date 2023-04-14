package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity {

    List<Cart> cartList;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cartList = ((Data) getApplication()).getCartList();

//        Intent intent = getIntent();
//        cartList = (List<Cart>) intent.getSerializableExtra("intent_toCart");
//        Log.d("ShoppingCartActivity", "cart: "+cartList.toString());

        recyclerView = findViewById(R.id.recyclerView_cartList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(getApplicationContext(), cartList);
        recyclerView.setAdapter(cartAdapter);

    }
}