package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostLoginResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("u_id")
    @Expose
    public String user_id; //회원 고유의 ID

    @SerializedName("token_type")
    @Expose
    public String token_type; //bearer

    @SerializedName("A_Token")
    @Expose
    public String A_Token;

    @SerializedName("R_Token")
    @Expose
    public String R_Token;

    @SerializedName("detail")
    @Expose
    public String detail; //error_message

    public String getUser_id() {
        return user_id;
    }

    public String getToken_type() {
        return token_type;
    }

    public String getA_Token() {
        return A_Token;
    }

    public String getR_Token() {
        return R_Token;
    }

    public String getDetail() {
        return detail;
    }

    @Override
    public String toString() {
        return "PostLoginResponse{" +
                "request='" + request + '\'' +
                ", user_id='" + user_id + '\'' +
                ", token_type='" + token_type + '\'' +
                ", A_Token='" + A_Token + '\'' +
                ", R_Token='" + R_Token + '\'' +
                ", detail='" + detail + '\'' +
                '}';
    }
}
