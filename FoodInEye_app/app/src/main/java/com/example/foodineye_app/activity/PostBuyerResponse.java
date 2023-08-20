package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostBuyerResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("documnet_id")
    @Expose
    public String user_id;

    @Override
    public String toString() {
        return "PostBuyerResponse{" + "request='" + request + '\'' + ", status='" + status + '\'' + ", user_id='" + user_id + '\'' + '}'; }

    public String getUser_id() {
        return user_id;
    }
}
