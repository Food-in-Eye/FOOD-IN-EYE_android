package com.example.foodineye_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.foodineye_app.R;

import java.util.List;

public class ShoppingCartActivity extends AppCompatActivity implements CartAdapter.OnItemClickListener {

    List<Cart> cartList;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    TextView totalPrice;
    TextView toOrder;

    int total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        cartList = ((Data) getApplication()).getCartList();
        total = ((Data)getApplication()).getTotalPrice();

        Log.d("ShoppingCart", "total" + total);
        Log.d("ShoppingCart", "cartList" + cartList.toString());

        recyclerView = findViewById(R.id.recyclerView_cartList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(getApplicationContext(), cartList, this);
        recyclerView.setAdapter(cartAdapter);

        //총 가격
        totalPrice = (TextView) findViewById(R.id.cart_totalPrice);
        totalPrice.setText(String.valueOf(total));

        //주문하기
        toOrder = (TextView) findViewById(R.id.orderBtn);
        toOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onItemClick(){
        int total = ((Data)getApplication()).getTotalPrice();
        totalPrice.setText(String.valueOf(total));
    }
    @Override
    public void onDeleteClick(int position){
        cartList.remove(position);
        cartAdapter.notifyItemRemoved(position);
    }
    @Override
    public void onToMenuClick(){
        String s_id = ((Data)getApplication()).getRecentS_id();
        String m_id = ((Data)getApplication()).getRecentM_id();
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.putExtra("intent_recentSId", s_id);
        intent.putExtra("intent_cartMId", m_id);
        startActivity(intent);
    }
}