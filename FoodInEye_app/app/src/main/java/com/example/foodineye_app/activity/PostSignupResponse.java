package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostSignupResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("u_id")
    @Expose
    public String u_id;

    @SerializedName("detail")
    @Expose
    public String detail;

    @Override
    public String toString() {
        return "PostSignupResponse{" +
                "request='" + request + '\'' +
                ", u_id='" + u_id + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }

    public String getU_id() {
        return u_id;
    }

    public String getDetail() {
        return detail;
    }
}
