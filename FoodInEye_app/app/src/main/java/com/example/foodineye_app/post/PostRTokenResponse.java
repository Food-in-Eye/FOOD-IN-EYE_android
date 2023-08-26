package com.example.foodineye_app.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostRTokenResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("R_Token")
    @Expose
    public String R_Token;

    @Override
    public String toString() {
        return "PostRTokenResponse{" +
                "request='" + request + '\'' +
                ", R_Token='" + R_Token + '\'' +
                '}';
    }

    public String getR_Token() {
        return R_Token;
    }
}
