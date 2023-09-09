package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PutEyePermission {

    @SerializedName("u_id")
    @Expose
    String u_id;

    @SerializedName("eye_permission")
    @Expose
    Boolean eye_permission;

    public PutEyePermission(String u_id, Boolean eye_permission) {
        this.u_id = u_id;
        this.eye_permission = eye_permission;
    }
}
