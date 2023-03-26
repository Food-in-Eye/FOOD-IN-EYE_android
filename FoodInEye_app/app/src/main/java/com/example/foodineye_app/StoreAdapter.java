package com.example.foodineye_app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class StoreAdapter extends RecyclerView.Adapter<StoreAdapter.MyViewHolder> {

    private Context mContext;
    private List<Stores> storesList;

    public StoreAdapter(Context context, List<Stores> storesList) {
        mContext = context;
        this.storesList = storesList;
    }

    @NonNull
    @Override
    public StoreAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.activity_store_recyclerview, parent, false);
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

        //store ID 값 받아오기
        String storeId = store.get_id();

        //Click Store Detail, intent에 가게 "_id" 전달, MenuActivity와 연결
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MenuActivity.class);
                intent.putExtra("_id", storeId);
                ((CategoryActivity)context).startActivity(intent);
                //context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(storesList==null) return 0;
        return storesList.size();
    }

    //ViewHolder 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView storeName;
        Button storeBtn;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            storeName = (TextView)itemView.findViewById(R.id.store_name);
            storeBtn = (Button)itemView.findViewById(R.id.sBtn);
        }
    }
}