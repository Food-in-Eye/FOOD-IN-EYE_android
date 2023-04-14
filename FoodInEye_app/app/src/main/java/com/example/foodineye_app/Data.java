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

    public void setCartList(Cart cart){
        cartList.add(cart);
    }

//    public void setCartList(List<Cart> cartList) {    this.cartList = cartList;  }
}
