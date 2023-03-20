package com.example.foodineye_app;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {

    private Context c;
    private List<Stores> storesList;

    public StoreAdapter(Context c, List<Stores> storesList) {
        this.c = c;
        this.storesList = storesList;
    }

    @NonNull
    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(c).inflate(R.layout.activity_store_recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreAdapter.MyViewHolder holder, int position) {

        //Bind the data for each item in the list
        Stores store = storesList.get(position);
        holder.storeName.setText(store.getName());

        //Set the background color for each item in the list
        int color = store.isOpen() ? Color.LTGRAY : Color.WHITE;
        holder.itemView.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        if(storesList==null) return 0;
        return storesList.size();
    }

    //ViewHolder 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView storeName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            storeName = (TextView)itemView.findViewById(R.id.store_name);
        }
    }
}