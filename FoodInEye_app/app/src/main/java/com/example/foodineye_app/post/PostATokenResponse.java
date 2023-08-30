package com.example.foodineye_app.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostATokenResponse {

    @SerializedName("A_Token")
    @Expose
    public String A_Token;

    @Override
    public String toString() {
        return "PostATokenResponse{" +
                "A_Token='" + A_Token + '\'' +
                '}';
    }

    public String getA_Token() {
        return A_Token;
    }
}
