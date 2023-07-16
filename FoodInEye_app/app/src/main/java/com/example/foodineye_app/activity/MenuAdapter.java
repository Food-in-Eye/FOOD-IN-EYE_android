package com.example.foodineye_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodineye_app.R;

import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private Context mContext;
    private MenuItem.Response response;
    private List<Menus> menusList;

    private String m_Id,s_Id;
    private String s_name;
    private int s_num;

    private int[] itemOLocation;


    public MenuAdapter(Context mContext, List<Menus> menusList, String m_Id, String s_Id, String s_name, int s_num) {
        this.mContext = mContext;
        this.menusList = menusList;
        this.m_Id = m_Id;
        this.s_Id = s_Id;
        this.s_name = s_name;
        this.s_num = s_num;
    }

    @NonNull
    @Override
    public MenuAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.menu_recyclerview, parent, false);

        if(viewType == 0){
            itemOLocation = new int[2];
            view.getLocationOnScreen(itemOLocation);
        }

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
        food.setF_num(menus.getNum());
        IntentToDetail intentToDetail = new IntentToDetail(s_Id, m_Id, s_name, food, s_num);
        Log.d("MenuAdapter", "intentToDetail"+intentToDetail.toString());


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

        //-----------------------------------------------------------------------------------------
        // 0번째 아이템인 경우
        if (position == 0 && itemOLocation != null) {
            View itemView = holder.itemView;

            // 아이템 뷰의 위치와 크기를 가져오기 위해 ViewTreeObserver를 사용
            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] itemLocation = new int[2];
                    itemView.getLocationOnScreen(itemLocation);

                    int topOffset = itemOLocation[1]; // 아이템 뷰의 위쪽 좌표
                    int left = itemLocation[0]; // 아이템 뷰의 왼쪽 좌표
                    int top = itemLocation[1] - topOffset; // 아이템 뷰의 위쪽 좌표 - topOffset
                    int right = left + itemView.getWidth(); // 아이템 뷰의 오른쪽 좌표
                    int bottom = top + itemView.getHeight(); // 아이템 뷰의 아래쪽 좌표

                    // 결과 출력
                    Log.d("location", "Item 0 - Left: " + left);
                    Log.d("location", "Item 0 - Top: " + top);
                    Log.d("location", "Item 0 - Right: " + right);
                    Log.d("location", "Item 0 - Bottom: " + bottom);
                }
            });
        }

        // 1번째 아이템인 경우
        if (position == 1 && itemOLocation != null) {
            View itemView = holder.itemView;

            // 아이템 뷰의 위치와 크기를 가져오기 위해 ViewTreeObserver를 사용
            itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                    int[] itemLocation = new int[2];
                    itemView.getLocationOnScreen(itemLocation);

                    int topOffset = itemOLocation[1]; // 아이템 뷰의 위쪽 좌표
                    int left = itemLocation[0]; // 아이템 뷰의 왼쪽 좌표
                    int top = itemLocation[1] - topOffset; // 아이템 뷰의 위쪽 좌표 - topOffset
                    int right = left + itemView.getWidth(); // 아이템 뷰의 오른쪽 좌표
                    int bottom = top + itemView.getHeight(); // 아이템 뷰의 아래쪽 좌표

                    // 결과 출력
                    Log.d("location", "Item 1 - Left: " + left);
                    Log.d("location", "Item 1 - Top: " + top);
                    Log.d("location", "Item 1 - Right: " + right);
                    Log.d("location", "Item 1 - Bottom: " + bottom);
                }
            });
        }
        //-----------------------------------------------------------------------------------------

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
