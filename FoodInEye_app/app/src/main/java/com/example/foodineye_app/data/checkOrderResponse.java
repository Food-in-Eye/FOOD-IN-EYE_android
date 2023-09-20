package com.example.foodineye_app.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class checkOrderResponse {
    @SerializedName("complete")
    @Expose
    Boolean orderComplete;

    public Boolean getOrderComplete() {
        return orderComplete;
    }
}
