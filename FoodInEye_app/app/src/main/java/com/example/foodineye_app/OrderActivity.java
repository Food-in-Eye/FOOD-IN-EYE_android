package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    List<Order> orderList = new ArrayList<>();
    List<SubOrder> subOrderList;
    List<Cart> cartList;
    TextView totalPrice;
    int total;

    RecyclerView orderRecyclerview;
    OrderAdapter orderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

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
                groupByStore.put(s_id, grouplist);
            }
        }

        //그룹화된 Cart 객체들을 이용하여 Order 객체 생성 후 orderList에 추가
        for(Map.Entry<String, List<Cart>> entry : groupByStore.entrySet()){
            String s_id = entry.getKey();
            List<Cart> cartGroup = entry.getValue();
            String s_name = cartGroup.get(0).getS_name();
            for(Cart cart : cartGroup){
                String m_id = cart.getM_id();
                String f_id = cart.getF_id();
                String m_name = cart.getM_name();
                int m_price = cart.getM_price();
                String m_imageKey = cart.getM_imageKey();
                int m_count = cart.getM_count();
                SubOrder subOrder = (SubOrder) new SubOrder(f_id, m_name, m_price, m_count);
                subOrderList = new ArrayList<>();
                subOrderList.add(subOrder);
                Order order = new Order(s_id, s_name, m_id, subOrderList);
                orderList.add(order);
            }
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
    }
}