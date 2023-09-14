package com.example.foodineye_app.data;

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

    @SerializedName("camera")
    @Expose
    int camera;

    public PutMyInfoSet(String id, String old_pw, String new_pw, String name, int gender, int age, int camera) {
        this.id = id;
        this.old_pw = old_pw;
        this.new_pw = new_pw;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.camera = camera;
    }

    @Override
    public String toString() {
        return "PutMyInfoSet{" +
                "id='" + id + '\'' +
                ", old_pw='" + old_pw + '\'' +
                ", new_pw='" + new_pw + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                '}';
    }
}
