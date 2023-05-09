package com.example.foodineye_app;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class Data extends Application {

    private List<Cart> cartList;
    private String recentS_id, recentM_id;
    private List<Order> orderList;

    //orderList
    public List<Order> getOrderList(){ return orderList; }


    //carList
    @Override
    public void onCreate(){
        cartList = new ArrayList<>();
        orderList = new ArrayList<>();
        super.onCreate();
    }

    public List<Cart> getCartList() {  return cartList;  }

    //중복확인
    public boolean containsItem(List<Cart> cartList, Cart cart) {
        for (Cart c : cartList) {
            if (c.getF_id().equals(cart.getF_id())) {
                return true;
            }
        }
        return false;
    }
    public void setCartList(Cart cart){
        if(containsItem(cartList, cart)){
            for(Cart c : cartList){
                if (c.getF_id().equals(cart.getF_id())) {
                    c.increase_count();
                }
            }
        }else{
            cartList.add(cart);
        }
    }
    public int getTotalPrice(){
        int total = 0;
        for(Cart cart : cartList){
            total += cart.getM_TotalPrice();
        }
        return total;
    }

    public String getRecentS_id() {   return recentS_id;  }

    public void setRecentS_id(String recentS_id) {   this.recentS_id = recentS_id;   }

    public String getRecentM_id() {   return recentM_id;   }

    public void setRecentM_id(String recentM_id) {    this.recentM_id = recentM_id;  }

    public void setOrderList(List<Order> orderList) {    this.orderList = orderList;   }
}