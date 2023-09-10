package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PutEyePermission {

    @SerializedName("camera")
    @Expose
    Boolean eye_permission;

    public PutEyePermission(Boolean eye_permission) {
        this.eye_permission = eye_permission;
    }
}
