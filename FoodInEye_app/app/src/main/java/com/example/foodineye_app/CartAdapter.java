package com.example.foodineye_app;

import android.content.Context;
import android.util.Log;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.MyViewHolder> {

    private Context mContext;
    private List<Cart> cartList;

    private int count;

    public CartAdapter(Context mContext, List<Cart> cartList) {
        this.mContext = mContext;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_recyclerview, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.MyViewHolder holder, int position) {
        Cart cart = cartList.get(position);

        Log.d("CartAdapter", "cart: "+cart.toString());

        String m_imageKey = cart.getM_imageKey();
        String imageUrl = "https://foodineye.s3.ap-northeast-2.amazonaws.com/" + m_imageKey;
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .circleCrop()
                .into(holder.menuImg);
        holder.menuName.setText(cart.getM_name());
        holder.menuPrice.setText(String.valueOf(cart.getM_price()));

        holder.totalCount.setText(String.valueOf(cart.getM_count()));

        //수량 조절
        holder.plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart.increase_count();
                holder.totalCount.setText(String.valueOf(cart.getM_count()));
            }
        });
        holder.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cart.getM_count() > 1){
                    cart.decrease_count();
                    holder.totalCount.setText(String.valueOf(cart.getM_count()));
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if(cartList==null) return 0;
        return cartList.size();
    }

    //ViewHolder 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView menuImg;
        TextView menuName;
        TextView menuPrice;

        TextView totalCount;
        Button plusBtn;
        Button minusBtn;


        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            menuImg = (ImageView) itemView.findViewById(R.id.cart_menuImg);
            menuName = (TextView) itemView.findViewById(R.id.cart_menuName);
            menuPrice = (TextView) itemView.findViewById(R.id.cart_menuPrice);

            totalCount = (TextView) itemView.findViewById(R.id.cart_totalCount);
            plusBtn = (Button) itemView.findViewById(R.id.cart_plusBtn);
            minusBtn = (Button) itemView.findViewById(R.id.cart_minusBtn);
        }
    }
}
