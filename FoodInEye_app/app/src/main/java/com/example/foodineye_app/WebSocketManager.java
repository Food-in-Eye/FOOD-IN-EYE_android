package com.example.foodineye_app;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

import androidx.activity.result.contract.ActivityResultContracts;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class WebSocketManager {
    private static WebSocketManager instance;
    private WebSocket webSocket;
    private static Context context;

    private WebSocketManager(Context context){
        this.context = context;
    }

    public void connectWebSocket(String historyId){
        //WebSocket 연결
        //WebSocket 연결 코드
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("ws://10.0.2.2:8000/api/v2/app_socket/ws?id=" + historyId)
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onOpen(WebSocket webSocket, Response response) {
                //웹소켓이 연결될 때 실행되는 코드
                Log.d("WebSocket", "WebSocket connected");
            }

            @Override
            public void onMessage(WebSocket webSocket, String text) {
                Gson gson = new Gson();
                WebSocketData webSocketData = gson.fromJson(text, WebSocketData.class);
                //update UI
                Intent intent = new Intent(context, OrderDetailActivity.class);
                intent.putExtra("webSocketData", (Parcelable) webSocketData);
                context.startActivity(intent);
            }


            @Override
            public void onMessage(WebSocket webSocket, ByteString bytes) {

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

    public static synchronized WebSocketManager getInstance(){
        if(instance == null){
            instance = new WebSocketManager(context);
        }
        return instance;
    }

    public WebSocket getWebSocket() {
        return webSocket;
    }

}

