package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostLoginResponse {

    @SerializedName("u_id")
    @Expose
    public String user_id; //회원 고유의 ID

    @SerializedName("name")
    @Expose
    public String name; //name

    @SerializedName("token_type")
    @Expose
    public String token_type; //bearer

    @SerializedName("A_Token")
    @Expose
    public String A_Token;

    @SerializedName("R_Token")
    @Expose
    public String R_Token;

    @SerializedName("camera")
    @Expose
    public int eye_permission; //null 0, true 1, false 2

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

    public int getEye_permission() {
        return eye_permission;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "PostLoginResponse{" +
                "user_id='" + user_id + '\'' +
                ", name='" + name + '\'' +
                ", token_type='" + token_type + '\'' +
                ", A_Token='" + A_Token + '\'' +
                ", R_Token='" + R_Token + '\'' +
                ", eye_permission=" + eye_permission +
                '}';
    }
}
