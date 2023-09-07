package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostSignup {

    @SerializedName("id")
    @Expose
    String id;

    @SerializedName("pw")
    @Expose
    String pw;

    @SerializedName("name")
    @Expose
    String name;

    @SerializedName("gender")
    @Expose
    int gender; //male(1), female(2)

    @SerializedName("age")
    @Expose
    int age;

    public PostSignup(String id, String pw, String name, int gender, int age) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    @Override
    public String toString() {
        return "PostBuyer{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                '}';
    }
}
