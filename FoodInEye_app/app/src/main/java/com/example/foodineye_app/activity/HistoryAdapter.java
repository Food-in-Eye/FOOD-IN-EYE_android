package com.example.foodineye_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private History.HistoryResponse historyList;

    public HistoryAdapter(Context context, History.HistoryResponse historyList) {
        this.context = context;
        this.historyList = historyList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_recyclerview,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        History.HistoryResponse history = historyList;

        holder.date.setText(history.getDate());
        holder.totalPrice.setText(String.valueOf(history.getTotal_price()));

        StringBuilder sNames = new StringBuilder();
        for (String sName : history.getS_names()) {
            sNames.append(sName).append(", ");
        }
        // Remove the trailing comma and space
        sNames.setLength(sNames.length() - 2);
        holder.storeName.setText(sNames.toString());

        //HistoryDetail - h_id
        Intent intent = new Intent(context, OrderHistoryDetailActivity.class);
        intent.putExtra("h_id", history.getH_id());
        intent.putExtra("total", history.getTotal_price());
        if (context instanceof Activity) {
            ((Activity) context).startActivity(intent);
        } else {
            context.startActivity(intent);
        }
    }

    @Override
    public int getItemCount() {
        if (historyList == null || historyList.s_names == null) {
            return 0;
        }
        return historyList.s_names.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date;
        TextView storeName;
        TextView totalPrice;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.history_date);
            storeName = (TextView) itemView.findViewById(R.id.history_store);
            totalPrice = (TextView) itemView.findViewById(R.id.history_total);
        }

    }
}
