package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostTestResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("a_token")
    @Expose
    public String a_token;

    @Override
    public String toString() {
        return "PostTestResponse{" +
                "request='" + request + '\'' +
                ", a_token='" + a_token + '\'' +
                '}';
    }
}