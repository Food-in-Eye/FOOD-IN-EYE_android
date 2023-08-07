package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class History {

    @SerializedName("request")
    @Expose
    public  String request;

    @SerializedName("status")
    @Expose
    public  String status;

    @SerializedName("max_batch")
    @Expose
    public  int batch;

    @SerializedName("response")
    @Expose
    public  HistoryResponse historyResponse;

    public class HistoryResponse{

        @SerializedName("h_id")
        @Expose
        public  String h_id;

        @SerializedName("date")
        @Expose
        public  String date;

        @SerializedName("total_price")
        @Expose
        public  int total_price;

        @SerializedName("s_names")
        @Expose
        public List s_names;
    }

}
