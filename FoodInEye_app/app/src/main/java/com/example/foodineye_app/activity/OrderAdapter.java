package com.example.foodineye_app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.R;

import java.util.List;

//상위 어댑터
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.MyViewHolder> {

    private Context mContext;
    private List<Order> orderList;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public OrderAdapter(Context mContext, List<Order> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.storeName.setText(order.getStoreName());

        //자식 레이아웃 매니저 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.orderSub.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(order.getSubOrderList().size());

        //자식 어댑터 설정
        SubOrderAdapter subOrderAdapter = new SubOrderAdapter(order.getSubOrderList());

        holder.orderSub.setLayoutManager(layoutManager);
        holder.orderSub.setAdapter(subOrderAdapter);
        holder.orderSub.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    //MyViewHolder 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView storeName;
        RecyclerView orderSub;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            //부모 타이틀 - 가게이름
            storeName = (TextView) itemView.findViewById(R.id.order_storeName);
            //자식 아이템 - 가게별 음식들
            orderSub= itemView.findViewById(R.id.recyclerView_orderSubList);
        }
    }
}
