package com.example.foodineye_app.websocket;

import java.io.Serializable;

//Response Websocket
public class UpdateWebSocketModel implements Serializable {

    private WebSocketModel webSocketModel;
    private String o_id;
    private int status;

    public UpdateWebSocketModel(WebSocketModel webSocketModel, String o_id, int status) {
        this.webSocketModel = webSocketModel;
        this.o_id = o_id;
        this.status = status;
    }

    public String toString(){return "webSocketModel:"+ webSocketModel+ "o_id: "+ o_id+ " status: "+status;}

    public String getO_id() {
        return o_id;
    }

    public void setO_id(String o_id) {
        this.o_id = o_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public WebSocketModel getWebSocketModel() {    return webSocketModel;   }

    public void setWebSocketModel(WebSocketModel webSocketModel) {    this.webSocketModel = webSocketModel;   }
}
