package com.example.foodineye_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {

    private Context mContext;
    private List<Order> orderList;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public OrderDetailAdapter(Context mContext, List<Order> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_recyclerview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.storeName.setText(order.getStoreName());

        //자식 레이아웃 매니저 설정
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder.orderDetail.getContext(),
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(order.getSubOrderList().size());

        //자식 어댑터 설정
        SubOrderAdapter subOrderAdapter = new SubOrderAdapter(order.getSubOrderList());

        holder.orderDetail.setLayoutManager(layoutManager);
        holder.orderDetail.setAdapter(subOrderAdapter);
        holder.orderDetail.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return return orderList.size();
    }

    //MyViewHodler 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView storeName;
        RecyclerView orderDetail;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            //가게이름
            storeName = (TextView) itemView.findViewById(R.id.order_detail_storeName);
            //가게별 주문 상세내용
            orderDetail = itemView.findViewById(R.id.recyclerView_orderDetailList);
        }
    }

}
