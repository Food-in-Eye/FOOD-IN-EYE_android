package com.example.foodineye_app.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.R;

import java.util.List;

public class HistoryDetailAdapter extends RecyclerView.Adapter<HistoryDetailAdapter.MyViewHolder> {

    private Context context;
    private List<HistoryDetail.HistoryDetailResponse.OrderItem> orderItemList;

    public HistoryDetailAdapter(Context context, List<HistoryDetail.HistoryDetailResponse.OrderItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_detail_recyclerview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HistoryDetail.HistoryDetailResponse.OrderItem orderItem = orderItemList.get(position);

        holder.storeName.setText(orderItem.s_name);
        holder.foodName.setText(orderItem.f_name);
        holder.count.setText(String.valueOf(orderItem.count));
        holder.foodPrice.setText(String.valueOf(orderItem.price));
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView storeName;
        TextView foodName;
        TextView count;
        TextView foodPrice;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            storeName = (TextView) itemView.findViewById(R.id.detail_history_storeName);
            foodName = (TextView) itemView.findViewById(R.id.detail_history_foodName);
            count = (TextView) itemView.findViewById(R.id.detail_history_foodCount);
            foodPrice = (TextView) itemView.findViewById(R.id.detail_history_foodPrice);
        }
    }
}
