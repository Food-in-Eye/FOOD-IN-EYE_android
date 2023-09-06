package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostId {

    @SerializedName("id")
    @Expose
    String id;

    public PostId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {  return "PostId{" + "id='" + id + '\'' + '}';  }
}
