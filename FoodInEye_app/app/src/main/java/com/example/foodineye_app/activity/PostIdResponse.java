package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostIdResponse {

    @SerializedName("state")
    @Expose
    public String state;

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "PostIdResponse{" +
                "state='" + state + '\'' +
                '}';
    }
}
