package com.example.foodineye_app.websocket;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.foodineye_app.activity.Data;
import com.example.foodineye_app.activity.OrderDetailActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class WebSocketManager {
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private Context context;

    private WebSocketManager(Context context){
        this.context = context;
    }

    public void connectWebSocket(String historyId){

        //WebSocket 연결
        //WebSocket 연결 코드
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
//                .url("ws://10.0.2.2:8000/api/v2/websockets/ws?h_id=" + historyId)
//                .url("ws://203.252.213.200:4040/api/v2/websockets/ws?h_id=" + historyId)
                .url("ws://192.168.219.200:4040/api/v2/websockets/ws?h_id=" + historyId)
//                .url("ws://127.0.0.1:8000//api/v2/websockets/ws?h_id=" + historyId)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                //웹소켓이 연결될 때 실행되는 코드
                Log.d("WebSocket", "WebSocket connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Log.d("WebSocket", "onMessage: " + text);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(text, JsonObject.class);

                if(jsonObject.has("type")){
                    String messageType = jsonObject.get("type").getAsString();
                    Log.d("WebSocketManager", "1번");
                    Log.d("WebSocketManager", "messageType: "+messageType);
                    switch (messageType){
                        case "connect":
                            if(jsonObject.has("result")){
                                Log.d("WebSocketManager", "websocket: " +jsonObject.get("result").getAsString());
                                Log.d("WebSocketManager", "2번");
                            }break;
                        case "update_status":
                            if(jsonObject.has("result")){
                                Log.d("WebSocketManager", "3번");
                                String messageResult = jsonObject.get("result").getAsString();
                                WebSocketModel webSocketModel = new WebSocketModel(messageType, messageResult);
                                Log.d("WebSocketManager", "webSocketModel: "+ webSocketModel.toString());
                                String o_id = jsonObject.get("o_id").getAsString();
                                int status = Integer.parseInt(jsonObject.get("status").getAsString());
                                UpdateWebSocketModel updateWebSocketModel = new UpdateWebSocketModel(webSocketModel, o_id, status);
                                //update UI
                                Intent intent = new Intent(context, OrderDetailActivity.class);
                                intent.putExtra("updateWebSocketModel", (Serializable) updateWebSocketModel);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // FLAG_ACTIVITY_NEW_TASK 플래그 추가
                                Log.d("WebSocketManager", "updateWebSocketModel: "+ updateWebSocketModel.getStatus());
                                context.startActivity(intent);
                            }break;
                        // 추가적인 메시지 타입에 대한 처리 로직 추가
                        default:
                            // 알 수 없는 메시지 타입에 대한 처리 로직
                            break;
                    }
                }

            }

            @Override
            public void onClosing(WebSocket webSocket, int code, String reason) {
                Log.d("WebSocket", "WebSocket onClosing");
            }

            @Override
            public void onClosed(WebSocket webSocket, int code, String reason) {
                Log.d("WebSocket", "WebSocket onClosed");
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.d("WebSocket", "WebSocket onFailure: "+ t.getMessage());

            }
        });
    }

    // WebSocket 연결을 끊는 메서드
    public void disconnectWebSocket() {
        if (webSocket != null) {
            JSONObject jsonMessage = new JSONObject();
            try {
                jsonMessage.put("type", "connect");
                jsonMessage.put("result", "close");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            webSocket.send(jsonMessage.toString()); // JSON 메시지 전송
            webSocket.close(1000, "Connection closed"); // 연결 종료
            Log.d("WebSocket", "WebSocket Closed");

            //데이터 모두 삭제
            Data data = (Data) context;
            data.initializeAllVariables();
        }
    }

    public static synchronized WebSocketManager getInstance(Context context){
        if(instance == null){
            instance = new WebSocketManager(context);
        }
        return instance;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

}

