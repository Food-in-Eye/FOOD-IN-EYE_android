package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderActivity extends AppCompatActivity {

    List<Order> orderList = new ArrayList<>();
    List<SubOrder> subOrderList;
    List<Cart> cartList;
    TextView totalPrice;
    int total;

    RecyclerView orderRecyclerview;
    OrderAdapter orderAdapter;
    Order order;

    List<PostOrder.StoreOrder> content = new ArrayList<>();
    List<PostOrder.StoreOrder.FoodCount> f_list;
    PostOrder postOrder;
    String u_id = "6458f67e50bde95733e4b57f";
    String history_id;

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
                Log.d("OrderActivity", "grouplist"+grouplist.toString());
                groupByStore.put(s_id, grouplist);
            }
        }

        //그룹화된 Cart 객체들을 이용하여 Order 객체 생성 후 orderList에 추가
        for(Map.Entry<String, List<Cart>> entry : groupByStore.entrySet()){
            String s_id = entry.getKey();
            List<Cart> cartGroup = entry.getValue();
            String s_name = cartGroup.get(0).getS_name();
            String m_id = cartGroup.get(0).getM_id();
            subOrderList = new ArrayList<>();
            f_list = new ArrayList<>();
            for(Cart cart : cartGroup){
                String f_id = cart.getF_id();
                String m_name = cart.getM_name();
                int m_price = cart.getM_price();
                String m_imageKey = cart.getM_imageKey();
                int m_count = cart.getM_count();
                SubOrder subOrder = (SubOrder) new SubOrder(f_id, m_name, m_price, m_count);
                subOrderList.add(subOrder);
                //post
                PostOrder.StoreOrder.FoodCount foodCount = (PostOrder.StoreOrder.FoodCount) new PostOrder.StoreOrder.FoodCount(f_id, m_count, m_price);
                f_list.add(foodCount);
            }
            order = new Order(s_id, s_name, m_id, subOrderList);
            orderList.add(order);
            //post
            PostOrder.StoreOrder storeOrder = new PostOrder.StoreOrder(s_id, m_id, f_list);
            content.add(storeOrder);
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

        //postOrder 세팅
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        //요청 바디에 들어가 PostOrder 객체 생성
        postOrder = new PostOrder(u_id, total, content);

        Log.d("OrderActivity", "postOrder" + postOrder.toString());

        //결제하기 버튼 클릭 + API 요청 보내기
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.payBtn);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrderActivity", "결제하기!");
                Call<PostOrderResponse> call = apiInterface.createOrder(postOrder);
                call.enqueue(new Callback<PostOrderResponse>() {
                    @Override
                    public void onResponse(Call<PostOrderResponse> call, Response<PostOrderResponse> response) {
                        if(response.isSuccessful()){
                            //요청이 성공한 경우 처리할 작업

                            PostOrderResponse responseBody = response.body();
                            history_id = responseBody.getHistory_id();
                            List<PostOrderResponse.Response> responseList;
                            responseList = responseBody.getResponse();

                            Log.d("OrderActivity", "history_id"+history_id);

                            //return 값 받기
                            //s_id에 맞는 order_id 넣기
                            for(Order order : orderList){
                                for(PostOrderResponse.Response result : responseList){
                                    if(order.getStoreId().equals(result.getS_id())){
                                        order.setOrderId(result.getO_id());
                                    }
                                }
                            }
                            Log.d("OrderActivity", "SubOrderList"+subOrderList.toString());
                            Log.d("OrderActivity", "OrderList"+orderList.toString());
                        }
                    }

                    @Override
                    public void onFailure(Call<PostOrderResponse> call, Throwable t) {
                        //요청이 실패한 경우 처리할 작업
                        Log.d("OrderActivity", "요청실패!");
                    }
                });
                //Data orderList에 주문내용 추가하기
                final Data data = (Data) getApplicationContext();
                data.setHistory_id(history_id); //웹소켓에 필요한 history_id
                data.setOrderList(orderList);

                //order_detail 이동
                Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                startActivity(intent);
            }//onClick
        });

    }
}