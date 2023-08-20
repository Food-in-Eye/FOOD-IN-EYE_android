package com.example.foodineye_app.activity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostBuyer {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("pw")
    @Expose
    String pw;

    @SerializedName("name")
    @Expose
    String str;

    @SerializedName("gender")
    @Expose
    int gender; //male(1), female(2)

    @SerializedName("age")
    @Expose
    int age;
}
