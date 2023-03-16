package com.example.foodineye_app;

import com.google.gson.annotations.SerializedName;

import java.util.List;
import com.example.foodineye_app.Stores;

public class StoreItem {

    @SerializedName("request")
    public String request;
    @SerializedName("response")
    public List<Stores> response;

    @Override
    public String toString(){
        return "StoreItem{"+"body="+response +"}";
    }
}
