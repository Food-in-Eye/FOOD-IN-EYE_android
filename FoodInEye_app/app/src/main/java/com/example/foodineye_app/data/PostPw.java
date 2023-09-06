package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostPw {

    @SerializedName("pw")
    @Expose
    String pw;

    public PostPw(String pw) {
        this.pw = pw;
    }

    @Override
    public String toString() {
        return "PostPw{" +
                "pw='" + pw + '\'' +
                '}';
    }
}
