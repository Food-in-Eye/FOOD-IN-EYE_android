package com.example.foodineye_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
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
    private MenuItem.Response response;
    private List<Menus> menusList;

    private String m_id,s_id;


    public MenuAdapter(Context mContext, List<Menus> menusList, String m_id, String s_id) {
        this.mContext = mContext;
        this.menusList = menusList;
        this.m_id = m_id;
        this.s_id = s_id;
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
        String imageUrl = "https://foodineye.s3.ap-northeast-2.amazonaws.com/" + menus.getImg_key();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .circleCrop()
                .into(holder.menuImg);


        Food food = new Food();//객체 생성
        food.setFood_id(menus.getf_id());
        food.setM_name(menus.getName());
        food.setM_price(menus.getPrice());
        food.setM_img_key(menus.getImg_key());
        food.setM_desc(menus.getM_desc());
        food.setM_allergy(menus.getAllergy());
        food.setM_origin(menus.getOrigin());
        IntentToDetail intentToDetail = new IntentToDetail();
        intentToDetail.setFood(food);
        intentToDetail.setM_id(m_id);
        intentToDetail.setM_id(s_id);


        //Click Menu Detail, intent에 Food 객체 전달, MenuDetailActivity와 연결
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, MenuDetailActivity.class);
                intent.putExtra("intentToDetail", intentToDetail);
                Log.d("intentToDetail", "intentToDetail: " + intentToDetail);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (context instanceof Activity) {
                    ((Activity) context).startActivity(intent);
                } else {
                    context.startActivity(intent);
                }
            }
        });
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
