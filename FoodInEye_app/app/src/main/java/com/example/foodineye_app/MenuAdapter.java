package com.example.foodineye_app;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context mContext;
    private List<Menus>


    //ViewHolder 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView menuImg;
        TextView menuName;
        TextView menuPrice;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            menuImg = (ImageView) itemView.findViewById(R.id.menu_image);
            menuName = (TextView) itemView.findViewById(R.id.menu_name);
            menuPrice = (TextView) itemView.findViewById(R.id.menu_price);
        }
    }
}
