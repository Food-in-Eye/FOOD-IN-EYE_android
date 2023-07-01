package com.example.foodineye_app;

import android.content.Context;
import android.content.Intent;
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

import org.w3c.dom.Text;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnItemClickListener{
        void onItemClick();
        void onDeleteClick(int position);
        void onToMenuClick();
    }

    private Context mContext;
    private List<Cart> cartList;
    private OnItemClickListener mListener;

    private final int TYPE_ITEM = 0;
    private final int TYPE_FOOTER = 1;

    //position별로 item의 View Type을 정의
    @Override
    public int getItemViewType(int position){
        Log.d("CartAdapter", "position" + position);
        Log.d("CartAdapter", "carList.size: "+cartList.size()) ;
        if(position == cartList.size()){
            return TYPE_FOOTER;
        }else if(position == cartList.size()){
            return TYPE_ITEM;
        }else{
            return TYPE_ITEM;
        }
    }

    public CartAdapter(Context mContext, List<Cart> cartList, OnItemClickListener listener) {
        this.mContext = mContext;
        this.cartList = cartList;
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        RecyclerView.ViewHolder holder;
        View view;
        if(viewType == TYPE_FOOTER){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_footer, parent, false);
            holder = new FooterViewHolder(view);
        }else{
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_recyclerview, parent, false);
            holder = new MyViewHolder(view);
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof FooterViewHolder){
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.setToMenu();
        }else{
            MyViewHolder myViewHolder = (MyViewHolder) holder;
            Log.d("CartAdapter", "cartListPosition: "+ position);

            myViewHolder.onBind(cartList.get(position), position-1);
        }
    }

    @Override
    public int getItemCount() {
        if(cartList == null) return 0;
        return cartList.size() + 1;
    }

    //MyViewHolder 정의
    public class MyViewHolder extends RecyclerView.ViewHolder{
        ImageView menuImg;
        TextView storeName;
        TextView menuName;
        TextView menuPrice;

        TextView totalCount;
        Button plusBtn;
        Button minusBtn;

        ImageView toDelete;

        Cart cart;
        int position;

        public MyViewHolder(@NonNull View itemView){
            super(itemView);

            menuImg = (ImageView) itemView.findViewById(R.id.cart_menuImg);
            storeName = (TextView) itemView.findViewById(R.id.cart_storeName);
            menuName = (TextView) itemView.findViewById(R.id.cart_menuName);
            menuPrice = (TextView) itemView.findViewById(R.id.cart_menuPrice);

            totalCount = (TextView) itemView.findViewById(R.id.cart_totalCount);
            plusBtn = (Button) itemView.findViewById(R.id.cart_plusBtn);
            minusBtn = (Button) itemView.findViewById(R.id.cart_minusBtn);

            toDelete = (ImageView) itemView.findViewById(R.id.menu_delete);
        }

        public void onBind(Cart cart, int position) {
            this.cart = cart;
            this.position = position;

            String m_imageKey = cart.getM_imageKey();
            String imageUrl = "https://foodineye.s3.ap-northeast-2.amazonaws.com/" + m_imageKey;
            Glide.with(itemView.getContext())
                    .load(imageUrl)
                    .circleCrop()
                    .into(menuImg);
            storeName.setText(cart.getS_name());
            menuName.setText(cart.getM_name());
            menuPrice.setText(String.valueOf(cart.getM_price()));

            totalCount.setText(String.valueOf(cart.getM_count()));
            Log.d("CartAdapter", "cart: "+cart.toString());
            Log.d("CartAdapter", "carList: "+cartList.toString()) ;

            //수량 조절
            plusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    cart.increase_count();
                    totalCount.setText(String.valueOf(cart.getM_count()));
                    mListener.onItemClick();
                }
            });
            minusBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(cart.getM_count() > 1){
                        cart.decrease_count();
                        totalCount.setText(String.valueOf(cart.getM_count()));
                        mListener.onItemClick();
                    }
                }
            });


            //메뉴 삭제
            toDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("CartAdapter", "removeposition: " + position);
                    mListener.onDeleteClick(position);
                    mListener.onItemClick();
                    Log.d("CartAdapter", "getItemCount: "+getItemCount());
                    notifyDataSetChanged();
                }
            });
        }
    }

    //FooterViewHolder 클래스 정의
    public class FooterViewHolder extends RecyclerView.ViewHolder{
        TextView toMenu;

        public FooterViewHolder(View footerView){
            super(footerView);
            toMenu = (TextView) footerView.findViewById(R.id.cart_toMenu);
        }
        public void setToMenu(){

            toMenu.setVisibility(View.VISIBLE);

            toMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("CartAdapter", "toMenuClick!");
                    mListener.onToMenuClick();
                }
            });
        }
    }

}
