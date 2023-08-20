package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostIdResponse {

    @SerializedName("request")
    @Expose
    public String request;

    @SerializedName("status")
    @Expose
    public String status;

    @SerializedName("response")
    @Expose
    public String response;

    @Override
    public String toString() { return "PostIdResponse{" + "request='" + request + '\'' + ", status='" + status + '\'' + ", response=" + response + '}'; }

    public String getResponse() {
        return response;
    }
}
