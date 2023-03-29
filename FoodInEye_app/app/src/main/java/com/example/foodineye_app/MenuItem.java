package com.example.foodineye_app;

import android.view.Menu;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import com.example.foodineye_app.Menus;

public class MenuItem {

    @SerializedName("request")
    public String request;
    @SerializedName("status")
    public String status;
    @SerializedName("response")
    public List<response> response;

    public class response{

        @SerializedName("_id")
        @Expose
        private String _id;

        @SerializedName("s_id")
        @Expose
        private String s_id;

        @SerializedName("date")
        @Expose
        private String date;

        @SerializedName("f_list")
        @Expose
        public List<Menus> menus;
    }
}
