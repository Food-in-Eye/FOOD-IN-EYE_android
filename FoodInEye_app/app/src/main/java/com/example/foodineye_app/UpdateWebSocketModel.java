package com.example.foodineye_app;

public class UpdateWebSocketModel {

    private WebSocketModel webSocketModel;
    private String o_id;
    private String status;

    public UpdateWebSocketModel(WebSocketModel webSocketModel, String o_id, String status) {
        this.webSocketModel = webSocketModel;
        this.o_id = o_id;
        this.status = status;
    }

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public WebSocketModel getWebSocketModel() {    return webSocketModel;   }

    public void setWebSocketModel(WebSocketModel webSocketModel) {    this.webSocketModel = webSocketModel;   }
}
