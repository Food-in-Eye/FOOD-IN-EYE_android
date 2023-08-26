package com.example.foodineye_app.post;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PutMyInfoSet {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("old_pw")
    @Expose
    String old_pw;

    @SerializedName("new_pw")
    @Expose
    String new_pw;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("gender")
    @Expose
    int gender;

    @SerializedName("age")
    @Expose
    int age;

    public PutMyInfoSet(String id, String old_pw, String new_pw, String name, int gender, int age) {
        this.id = id;
        this.old_pw = old_pw;
        this.new_pw = new_pw;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }
}
