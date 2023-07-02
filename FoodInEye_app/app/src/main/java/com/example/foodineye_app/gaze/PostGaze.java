package com.example.foodineye_app.gaze;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PostGaze {

    @SerializedName("page")
    @Expose
    String layout_name;
    @SerializedName("s_num")
    @Expose
    int s_num;
    @SerializedName("f_num")
    @Expose
    int f_num;
    @SerializedName("gaze")
    @Expose
    ArrayList<Gaze> list_gazeInfo;

    public PostGaze(String layout_name, int s_num, int f_num, ArrayList<Gaze> list_gazeInfo) {
        this.layout_name = layout_name;
        this.s_num = s_num;
        this.f_num = f_num;
        this.list_gazeInfo = list_gazeInfo;
    }

    public String toString(){return "layout_name:"+ layout_name+ "s_num: "+ s_num+ " f_num: "+f_num+" list_gazeInfo: "+list_gazeInfo.toString();}

    public static class Gaze{
        @SerializedName("x")
        @Expose
        float x;
        @SerializedName("y")
        @Expose
        float y;
        @SerializedName("t")
        @Expose
        long t;

        public Gaze(float x, float y, long t) {
            this.x = x;
            this.y = y;
            this.t = t;
        }

    }

}
