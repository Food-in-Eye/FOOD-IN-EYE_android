package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostIdResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("state")
    @Expose
    public String state;

    public String getState() {
        return state;
    }

    @Override
    public String toString() {
        return "PostIdResponse{" +
                "request='" + request + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
