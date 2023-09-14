package com.example.foodineye_app.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiClientEx;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.RefreshTokenService;
import com.example.foodineye_app.data.GetOrder;
import com.example.foodineye_app.data.Order;
import com.example.foodineye_app.websocket.WebSocketManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    EditText editId, editPw;

    String id, password;

    String at, rt; //tokens
    String user_id;
    int eye_permission;
    String name, h_id;

    private SharedPreferences sharedPreferences;
    Data data;

    // 앱의 메인 코드(예: 애플리케이션 클래스 또는 액티비티)에서 BroadcastReceiver를 등록합니다.
    private BroadcastReceiver startLoginActivityReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if ("start_login_activity".equals(intent.getAction())) {
                // 여기서 LoginActivity를 시작합니다.
                startLoginActivity(context);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        data = (Data) getApplication();

        // 커스텀 이벤트를 수신하기 위한 BroadcastReceiver를 등록합니다.
        IntentFilter filter = new IntentFilter("start_login_activity");
        registerReceiver(startLoginActivityReceiver, filter);

        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);

        //아이디 입력
        editId = (EditText) findViewById(R.id.login_id);
        editId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                id = s.toString();

            }
        });
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + id);

        //패스워드 입력
        editPw = (EditText) findViewById(R.id.login_pw);
        editPw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                password = s.toString();

            }
        });

        //로그인하기
        Button loginBtn = (Button) findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                HomeActivity로 이동
//                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
//                startActivity(loginIntent);
                login();
            }
        });

        //회원가입하기
        TextView textView = (TextView) findViewById(R.id.sign_up);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singupIntent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivity(singupIntent);
            }
        });
    }

    //로그인하기
    private void login(){

        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);

        Call<PostLoginResponse> call = apiInterface.login(id, password);
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + id);
        Log.i("LoginActivity", "!!!!!!!!로그인 : " + password);

        call.enqueue(new Callback<PostLoginResponse>() {
            @Override
            public void onResponse(Call<PostLoginResponse> call, Response<PostLoginResponse> response) {
                if (response.isSuccessful()) {
                    PostLoginResponse postLoginResponse = response.body();
                    if (postLoginResponse != null) {
                        Log.d("LoginActivity", "로그인 서버 성공: " + postLoginResponse.toString());

                        at = postLoginResponse.getA_Token();
                        rt = postLoginResponse.getR_Token();
                        user_id = postLoginResponse.getUser_id(); // 회원 고유의 ID
                        eye_permission = postLoginResponse.getEye_permission(); //회원 시선 수집 동의 여부
                        name = postLoginResponse.getName();

                        // Access Token과 Refresh Token 저장
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("access_token", at);
                        editor.putString("refresh_token", rt);
                        editor.putString("u_id", user_id);
                        editor.putInt("eye_permission", eye_permission);
                        editor.putString("name", name);

                        editor.apply();

                        Log.d("LoginActivity", "isOrder: "+data.isOrder());

                        // 로그인 후 R_Token Handler 실행
                        Intent serviceIntent = new Intent(getApplicationContext(), RefreshTokenService.class);
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                            // 안드로이드 Oreo (API 레벨 26) 이상에서는 startForegroundService 사용
                            startForegroundService(serviceIntent);
                        } else {
                            // Oreo 이전 버전에서는 그냥 startService 사용
                            startService(serviceIntent);
                        }

                        //------------------------------------------------------------
//                        if(postLoginResponse.getH_id() != null){
//                            //해당 user의 진행중인 주문이 있을 경우
//                            h_id = postLoginResponse.getH_id();
//                            data.setHistory_id(h_id);
//
//                            Log.d("modify!!!!!!!!!", "modify!!!!!!!!!login: "+data.getHistory_id());
//
//                            getOrder(h_id);
////                            connectWebSocket(h_id);
//                        }
                        //------------------------------------------------------------


                        //HomeActivity로 이동
                        Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(loginIntent);

                    } else {
                        Log.d("LoginActivity", "로그인 응답 오류: response.body()가 null입니다.");
                    }
                } else {

                    // 응답이 실패한 경우에 대한 처리를 여기서 수행합니다.
                    String errorBody = null; // 실패한 응답의 본문을 얻음
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.d("LoginActivity", "로그인 응답 오류: " + response.code() + ", " + errorBody);

                    // 여기서 오류 메시지를 해석하고 사용자에게 알맞은 안내를 제공합니다.
                    if (errorBody.contains("Logon failed.")) {
                        show("로그인에 실패했습니다!");

                    } else {
                        // 다른 오류 처리 로직을 추가할 수 있습니다.
                    }
                }
            }

            @Override
            public void onFailure(Call<PostLoginResponse> call, Throwable t) {
                Log.i("LoginActivity", "로그인 응답 오류: " + t.toString());
            }
        });

    }

    // ApiInterceptor 클래스 내부에서
    private void startLoginActivity(Context context) {
        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(loginIntent);
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //웹소켓 연결
    public void connectWebSocket(String history_id){
        //웹소켓 연결하기
        Log.d("WebSocket", "history_id: "+history_id);
        Log.d("WebSocket", "WebSocket 시도");

        WebSocketManager.getInstance(getApplicationContext()).connectWebSocket(history_id);
    }

    private void getOrder(String h_id){

        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);
        Call<GetOrder> call = apiInterface.getOrder(h_id);
        call.enqueue(new Callback<GetOrder>() {
            @Override
            public void onResponse(Call<GetOrder> call, Response<GetOrder> response) {
                if(response.isSuccessful()){

                    data.initializeAllVariables();

                    data.setHistory_id(h_id);
                    // 총 주문 내역 불러오기
                    List<GetOrder.nOrder> orderList = response.body().orderLists;
                    List<Order> orderList1 = new ArrayList<>();

                    for (GetOrder.nOrder order : orderList) {
                        // Order 객체에서 필요한 정보를 추출합니다.
                        String orderId = order.o_id;
                        String storeId = order.s_id;
                        String storeName = order.s_name;
                        String menuId = order.m_id;
                        int status = order.status;

                        // SubOrder 목록을 가져옵니다.
                        List<GetOrder.nOrder.FoodList> foodList = order.f_list;
                        List<SubOrder> subOrderList = new ArrayList<>();

                        // SubOrder 목록 출력
                        for (GetOrder.nOrder.FoodList foodItem : foodList) {
                            String foodId = foodItem.f_id;
                            String foodName = foodItem.name;
                            int price = foodItem.price;
                            int count = foodItem.count;

                            SubOrder subOrder = new SubOrder(foodId, foodName, price, count);
                            subOrderList.add(subOrder);
                        }
                        Order newOrder = new Order(storeId, storeName, menuId, subOrderList);
                        newOrder.setOrderId(orderId);
                        newOrder.setStatus(status);

                        orderList1.add(newOrder);
                        data.setOrderList(orderList1);

                        connectWebSocket(h_id);

                    }
                }else{
                    show("현재 주문 내역이 없습니다.");
                }

            }

            @Override
            public void onFailure(Call<GetOrder> call, Throwable t) {

            }
        });
    }
}