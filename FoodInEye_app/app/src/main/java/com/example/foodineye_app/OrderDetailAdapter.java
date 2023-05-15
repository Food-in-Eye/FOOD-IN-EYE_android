package com.example.foodineye_app;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.MyViewHolder> {

    OrderItem orderItem; // 가게별 GET
    OrderItem.OrderResponse orderResponses;

    private Context mContext;
    private List<Order> orderList;
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    private UpdateWebSocketModel updateWebSocketModel;

    public void updateOrderList() {
        notifyDataSetChanged();
    }

    //Item 클릭 상태를 저장할 array 객체
    private SparseBooleanArray selectedItems = new SparseBooleanArray();
    //직전에 클릭했던 Item의 postion
    private int prePosition = -1;

    public OrderDetailAdapter(Context mContext, List<Order> orderList) {
        this.mContext = mContext;
        this.orderList = orderList;
    }

    public UpdateWebSocketModel getUpdateWebSocketModel() {   return updateWebSocketModel;   }

    public void setUpdateWebSocketModel(UpdateWebSocketModel updateWebSocketModel) {  this.updateWebSocketModel = updateWebSocketModel;   }

    public int updateStatus(Order order){
        int status = 0;

        for(Order o : orderList){
            if(o.getOrderId() == updateWebSocketModel.getO_id()){
                o.setStatus(updateWebSocketModel.getStatus());
                status = order.getStatus();
            }else{
                status = 0;
            }
        }
        return status;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_detail_recyclerview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Order order = orderList.get(position);
        holder.storeName.setText(order.getStoreName());

        if(updateWebSocketModel != null){
            Log.d("OrderDetailAdapter", "updateWebSocketModel: "+ updateWebSocketModel.getStatus());
            int status = updateStatus(order);

            if(status == 2){
                holder.finishedCooking.setImageResource(R.drawable.status_finished);
                holder.cooking.setImageResource(R.drawable.cooking);
                holder.orderReceived.setImageResource(R.drawable.order_received);
            }else if(status == 1){
                holder.cooking.setImageResource(R.drawable.status_cooking);
                holder.finishedCooking.setImageResource(R.drawable.finished_cooking);
                holder.orderReceived.setImageResource(R.drawable.order_received);
            }else{
                holder.orderReceived.setImageResource(R.drawable.status_order_received);
                holder.finishedCooking.setImageResource(R.drawable.finished_cooking);
                holder.cooking.setImageResource(R.drawable.cooking);
            }
        }

        holder.order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("OrderDetailAdapter", "onclick");
                //Item 클릭 시 클릭 상태 저장하기
                if(selectedItems.get(position)){
                    selectedItems.delete(position);
                }else{
                    selectedItems.delete(prePosition);
                    selectedItems.put(position,true);
                }
                //해당 포지션의 변화를 알림
                if(prePosition!=-1) notifyItemChanged(prePosition);
                notifyItemChanged(position);
                prePosition = position;
            }
        });

        changeVisibility(selectedItems.get(position), holder.orderDetails);

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
        return orderList.size();
    }

    //클릭된 Item 의 상태 변경
    public void changeVisibility(final boolean isExpanded, LinearLayout orderDetails){

        int height = orderDetails.getLayoutParams().height;
        ValueAnimator va = isExpanded ? ValueAnimator.ofInt(0, height) : ValueAnimator.ofInt(height, 0);

        va.setDuration(500);
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                orderDetails.getLayoutParams().height = (int) animation.getAnimatedValue();
                orderDetails.requestLayout();
                orderDetails.setVisibility(isExpanded ? View.VISIBLE : View.GONE);
            }
        });
        va.start();
    }

    //MyViewHodler 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView storeName;
        LinearLayout order;
        LinearLayout orderDetails;
        RecyclerView orderDetail;

        ImageView orderReceived;
        ImageView cooking;
        ImageView finishedCooking;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            order = (LinearLayout) itemView.findViewById(R.id.order_detail);
            //가게이름
            storeName = (TextView) itemView.findViewById(R.id.order_detail_storeName);
            //가게별 주문 상세내용
            orderDetails = (LinearLayout) itemView.findViewById(R.id.order_details);
            orderDetail = itemView.findViewById(R.id.recyclerView_orderDetailList);
            //가게별 status
            orderReceived = (ImageView) itemView.findViewById(R.id.order_received_img);
            cooking = (ImageView) itemView.findViewById(R.id.cooking_img);
            finishedCooking = (ImageView) itemView.findViewById(R.id.finished_cooking_img);

        }
    }

}
