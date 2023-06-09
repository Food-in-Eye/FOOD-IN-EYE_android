package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MenuItem {

    @SerializedName("request")
    public String request;
    @SerializedName("status")
    public String status;
    @SerializedName("response")
    public Response response;

    public class Response{

        @SerializedName("_id")
        @Expose
        public String _id;

        @SerializedName("s_id")
        @Expose
        public String s_id;

        @SerializedName("date")
        @Expose
        public String date;

        @SerializedName("f_list")
        @Expose
        public List<Menus> menus;

        //getter and setter

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getS_id() {
            return s_id;
        }

        public void setS_id(String s_id) {
            this.s_id = s_id;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public List<Menus> getMenus() {  return menus; }

        public void setMenus(List<Menus> menus) {
            this.menus = menus;
        }
    }
}
