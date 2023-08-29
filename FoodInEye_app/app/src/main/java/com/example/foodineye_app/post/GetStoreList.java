package com.example.foodineye_app.post;

import com.example.foodineye_app.activity.Stores;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetStoreList {

    @SerializedName("status")
    public String status;
    @SerializedName("response")
    public List<Stores> response;

}
