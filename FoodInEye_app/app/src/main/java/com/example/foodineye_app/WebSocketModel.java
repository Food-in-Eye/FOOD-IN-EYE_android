package com.example.foodineye_app;

public class WebSocketModel {

    private String type;
    private String result;

    public WebSocketModel(String type, String result) {
        this.type = type;
        this.result = result;
    }

    public String getType() {    return type;  }

    public void setType(String type) {    this.type = type;   }

    public String getResult() {     return result;   }

    public void setResult(String result) {     this.result = result;   }
}
