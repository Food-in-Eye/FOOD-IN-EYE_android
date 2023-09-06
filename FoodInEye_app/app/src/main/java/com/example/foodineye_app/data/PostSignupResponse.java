package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostSignupResponse {

    @SerializedName("u_id")
    @Expose
    public String u_id;

    @Override
    public String toString() {
        return "PostSignupResponse{" +
                "u_id='" + u_id + '\'' +
                '}';
    }

    public String getU_id() {
        return u_id;
    }
}
