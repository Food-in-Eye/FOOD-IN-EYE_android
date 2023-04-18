package com.example.foodineye_app;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class Data extends Application {

    private List<Cart> cartList;

    @Override
    public void onCreate(){
        cartList = new ArrayList<>();
        super.onCreate();
    }

    public List<Cart> getCartList() {  return cartList;  }
//    public void setCartList(Cart cart){  cartList.add(cart);  }

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
}