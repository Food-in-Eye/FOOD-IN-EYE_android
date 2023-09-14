package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostPwCheckResponse {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("gender")
    @Expose
    public int gender;

    @SerializedName("age")
    @Expose
    public int age;

    @SerializedName("camera")
    @Expose
    public int eye_permission; //null 0, agree 1, disagree 2

    @Override
    public String toString() {
        return "PostPwCheckResponse{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", gender=" + gender +
                ", age=" + age +
                ", eye_permission=" + eye_permission +
                '}';
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getGender() {
        return gender;
    }

    public int getAge() {
        return age;
    }
}
