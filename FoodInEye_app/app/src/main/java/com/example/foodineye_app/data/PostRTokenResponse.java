package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostRTokenResponse {

    @SerializedName("R_Token")
    @Expose
    public String R_Token;

    @Override
    public String toString() {
        return "PostRTokenResponse{" +
                "R_Token='" + R_Token + '\'' +
                '}';
    }

    public String getR_Token() {
        return R_Token;
    }
}
