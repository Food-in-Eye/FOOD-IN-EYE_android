package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.R;
import com.example.foodineye_app.data.History;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    private Context context;
    private List<History.HistoryResponse> historyList;

    public HistoryAdapter(Context context, List<History.HistoryResponse> historyList) {
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
        History.HistoryResponse history = historyList.get(position);

        String inputDateTime = history.getDate();
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");

        try {
            Date date = inputFormat.parse(inputDateTime);
            String formattedDate = outputFormat.format(date);

            holder.date.setText(formattedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.totalPrice.setText(String.valueOf(history.getTotal_price()));

        StringBuilder sNames = new StringBuilder();
        for (String sName : history.getS_names()) {
            sNames.append(sName).append(" \n");
        }
        // Remove the trailing comma and space
        sNames.setLength(sNames.length() - 2);
        holder.storeName.setText(sNames.toString());

        holder.toDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //HistoryDetail - h_id
                Intent intent = new Intent(context, OrderHistoryDetailActivity.class);
                intent.putExtra("h_id", history.getH_id());
                intent.putExtra("total", String.valueOf(history.getTotal_price()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (historyList == null) {
            return 0;
        }
        return historyList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date;
        TextView storeName;
        TextView totalPrice;
        Button toDetail;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.history_date);
            storeName = (TextView) itemView.findViewById(R.id.history_store);
            totalPrice = (TextView) itemView.findViewById(R.id.history_total);
            toDetail = (Button) itemView.findViewById(R.id.history_to_detail);
        }

    }
}
