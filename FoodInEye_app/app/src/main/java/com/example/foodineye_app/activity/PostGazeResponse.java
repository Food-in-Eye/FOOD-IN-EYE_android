package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostGazeResponse {

    @SerializedName("request")
    @Expose
    public String request;
    @SerializedName("status")
    @Expose
    public String status;
    @SerializedName("url")
    @Expose
    public String url;

    public String toString(){return "request: "+ request+ "status: "+ status + "url: "+url;}
}
