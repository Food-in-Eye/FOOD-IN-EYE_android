package com.example.foodineye_app.activity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StoreItem {

    @SerializedName("request")
    public String request;
    @SerializedName("status")
    public String status;
    @SerializedName("response")
    public List<Stores> response;

    @Override
    public String toString(){
        return "request"+request+"status"+status+
                "StoreItem{"+"body="+response +"}";
    }
}
