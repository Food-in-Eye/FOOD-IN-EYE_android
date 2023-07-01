package com.example.foodineye_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

//자식 어댑터
public class SubOrderDetailAdapter extends RecyclerView.Adapter<SubOrderDetailAdapter.SubOrderDetailViewHolder> {

    private List<SubOrder> subOrderList;

    public SubOrderDetailAdapter(List<SubOrder> subOrderList) {
        this.subOrderList = subOrderList;
    }

    @NonNull
    @Override
    public SubOrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_sub_recyclerview, parent, false);
        return new SubOrderDetailAdapter.SubOrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubOrderDetailViewHolder holder, int position) {
        SubOrder subOrder = subOrderList.get(position);
        holder.foodName.setText(subOrder.getFoodName());
        holder.foodCount.setText(String.valueOf(subOrder.getFoodCount()));
        holder.foodPrice.setText(String.valueOf(subOrder.getFoodPrice()));
    }

    @Override
    public int getItemCount() {  return subOrderList.size();  }

    public class SubOrderDetailViewHolder extends RecyclerView.ViewHolder{
        TextView foodName;
        TextView foodCount;
        TextView foodPrice;

        SubOrderDetailViewHolder(View itemView){
            super(itemView);
            foodName = itemView.findViewById(R.id.order_foodName);
            foodCount = itemView.findViewById(R.id.order_foodCount);
            foodPrice = itemView.findViewById(R.id.order_foodPrice);
        }
    }
}
