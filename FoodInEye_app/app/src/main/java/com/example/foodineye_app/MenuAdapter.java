package com.example.foodineye_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context mContext;
    private List<Menus> menusList;

    public MenuAdapter(Context mContext, List<Menus> menusList) {
        this.mContext = mContext;
        this.menusList = menusList;
    }

    @NonNull
    @Override
    public MenuAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        //Bind the data for each item in the list
        Menus menus = menusList.get(position);
        holder.menuName.setText(menus.getName());
        holder.menuPrice.setText(String.valueOf(menus.getPrice()));
        //holder.menuImg.setImage(menus.getImg_key());
        String imageKey = menus.getImg_key();
        String imageUrl = "https://foodineye.s3.ap-northeast-2.amazonaws.com/" + imageKey;
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .circleCrop()
                .into(holder.menuImg);
    }

    @Override
    public int getItemCount() {
        if(menusList==null) return 0;
        return menusList.size();
    }

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
