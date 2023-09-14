package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.Order;
import com.example.foodineye_app.data.PostOrder;
import com.example.foodineye_app.data.PostOrderResponse;
import com.example.foodineye_app.gaze.PostGaze;
import com.example.foodineye_app.gaze.PostGazeResponse;
import com.example.foodineye_app.websocket.WebSocketManager;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import visual.camp.sample.view.PointView;

public class OrderActivity extends AppCompatActivity {
    Data data;

    Toolbar toolbar;

    List<Order> orderList = new ArrayList<>();
    List<SubOrder> subOrderList;
    List<Cart> cartList;
    TextView totalPrice;
    int total;

    RecyclerView orderRecyclerview;
    OrderAdapter orderAdapter;
    Order order;

    List<PostOrder.StoreOrder> content = new ArrayList<>();
    List<PostOrder.StoreOrder.FoodCount> f_list;
    PostOrder postOrder;

    SharedPreferences sharedPreferences;
    int eyePermission;
    String u_id;
    String history_id;

    //-----------------------------------------------------------------------------------------
    //gazeTracker
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");
    JSONArray jsonGazeArray = new JSONArray(); //전체 gaze
    List<PostGaze> postGazes = new ArrayList<>();
    Context ctx;
    ConstraintLayout orderLayout;
    PointView viewpoint;

    //-----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        toolbar = (Toolbar) findViewById(R.id.order_toolbar);
        setToolBar(toolbar);

        data = (Data) getApplication();
        //u_id
        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        u_id = sharedPreferences.getString("u_id", null);

        eyePermission = sharedPreferences.getInt("eye_permission", 0);

        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
        ctx = getApplicationContext();
        orderLayout = findViewById(R.id.orderLayout);
        viewpoint = findViewById(R.id.view_point_order);

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);
        //-------------------------------------------------------------------------------------

        //장바구니 리스트
        cartList = ((Data) getApplication()).getCartList();
        total = ((Data)getApplication()).getTotalPrice();

        //Cart 객체들을 s_id로 그룹화하여 HashMap에 담기
        HashMap<String, List<Cart>> groupByStore = new HashMap<>();
        for(Cart cart : cartList){
            String s_id = cart.getS_id();
            if(groupByStore.containsKey(s_id)){
                groupByStore.get(s_id).add(cart);
            }else{
                List<Cart> grouplist = new ArrayList<>();
                grouplist.add(cart);
                Log.d("OrderActivity", "grouplist"+grouplist.toString());
                groupByStore.put(s_id, grouplist);
            }
        }

        //그룹화된 Cart 객체들을 이용하여 Order 객체 생성 후 orderList에 추가
        for(Map.Entry<String, List<Cart>> entry : groupByStore.entrySet()){
            String s_id = entry.getKey();
            List<Cart> cartGroup = entry.getValue();
            String s_name = cartGroup.get(0).getS_name();
            String m_id = cartGroup.get(0).getM_id();
            subOrderList = new ArrayList<>();
            f_list = new ArrayList<>();
            for(Cart cart : cartGroup){
                String f_id = cart.getF_id();
                String f_name = cart.getM_name();
                int m_price = cart.getM_price();
                String m_imageKey = cart.getM_imageKey();
                int m_count = cart.getM_count();
                SubOrder subOrder = (SubOrder) new SubOrder(f_id, f_name, m_price, m_count);
                subOrderList.add(subOrder);
                //post
                PostOrder.StoreOrder.FoodCount foodCount = (PostOrder.StoreOrder.FoodCount) new PostOrder.StoreOrder.FoodCount(f_id, f_name, m_count, m_price);
                f_list.add(foodCount);
            }
            order = new Order(s_id, s_name, m_id, subOrderList);
            orderList.add(order);
            //post
            PostOrder.StoreOrder storeOrder = new PostOrder.StoreOrder(s_id, s_name, m_id, f_list);
            content.add(storeOrder);
        }

        //상위 recyclerview 설정
        orderRecyclerview = findViewById(R.id.recyclerView_orderList);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        orderRecyclerview.setLayoutManager(layoutManager);

        orderAdapter = new OrderAdapter(getApplicationContext(), orderList);
        orderRecyclerview.setAdapter(orderAdapter);

        //총 가격
        totalPrice = (TextView) findViewById(R.id.order_totalPrice);
        totalPrice.setText(String.valueOf(total));

        //postOrder 세팅
        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);


        //요청 바디에 들어가 PostOrder 객체 생성
        postOrder = new PostOrder(u_id, total, content);
        Log.d("OrderActivity", "postOrder" + postOrder.toString());

        //결제하기 버튼 클릭 + API 요청 보내기 + order, websocket, gaze
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.payBtn);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gazeTrackerDataStorage != null) {
                    gazeTrackerDataStorage.stopGazeTracker("order", 0, 0);
                }
                Log.d("OrderActivity", "결제하기!");
                Call<PostOrderResponse> call = apiInterface.createOrder(postOrder);
                call.enqueue(new Callback<PostOrderResponse>() {
                    @Override
                    public void onResponse(Call<PostOrderResponse> call, Response<PostOrderResponse> response) {
                        if(response.isSuccessful()){

                            //요청이 성공한 경우 처리할 작업

                            PostOrderResponse responseBody = response.body();
                            history_id = responseBody.getHistory_id();
                            data.setHistory_id(history_id);
                            Log.d("OrderDetail", "orderdetail!!!!!!!: "+data.getHistory_id());

                            List<PostOrderResponse.Response> responseList;
                            responseList = responseBody.getResponse();

                            Log.d("OrderActivity", "responseBody"+responseBody.toString());

                            //return 값 받기
                            //s_id에 맞는 order_id 넣기
                            for(Order order : orderList){
                                for(PostOrderResponse.Response result : responseList){
                                    if(order.getStoreId().equals(result.getS_id())){
                                        order.setOrderId(result.getO_id());
                                    }
                                }
                            }
                            Log.d("OrderActivity", "SubOrderList"+subOrderList.toString());
                            Log.d("OrderActivity", "OrderList"+orderList.toString());


                            //웹소켓 연결하기
                            Log.d("WebSocket", "history_id: "+history_id);
                            Log.d("WebSocket", "WebSocket 시도");

                            WebSocketManager.getInstance(getApplicationContext()).connectWebSocket(history_id);

                            //시선 권한 동의 여부 확인
                            if(eyePermission == 1){ //true
                                putGaze();
                            }

                        }else{
                            //요청이 실패한 경우 errorbody
                        }
                    }

                    @Override
                    public void onFailure(Call<PostOrderResponse> call, Throwable t) {
                        //요청이 실패한 경우 처리할 작업
                        Log.d("OrderActivity", "요청실패!");
                    }
                });

                //Data orderList에 주문내용 추가하기
//                final Data data = (Data) getApplicationContext();
                data.setOrderList(orderList);
                data.setHistory_id(history_id);

                //OrderDetial 화면으로 이동
                Intent intent = new Intent(getApplicationContext(), OrderDetailActivity.class);
                startActivity(intent);

            }//onClick
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //시선 권한 동의 여부 확인
        if(eyePermission == 1){ //true
            setGazeTrackerDataStorage();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(eyePermission == 1){
            stopGazeTracker();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(eyePermission == 1){
            gazeTrackerDataStorage.quitBackgroundThread();
            backgroundThread.quitSafely();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    //gazeTracker
    private void setGazeTrackerDataStorage(){
        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, orderLayout, viewpoint);
        }
    }

    private void stopGazeTracker(){
        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("order", 0, 0);
        }
    }

    //POST gaze
    public void putGaze(){

        //websocket 연결 후에 gaze 보내기
        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface1 = apiClient.getClient().create(ApiInterface.class);

        jsonGazeArray = ((Data) getApplication()).getJsonArray();

        postGazes = (List<PostGaze>) ((Data)getApplication()).getGazeList();


        Call<PostGazeResponse> gazeCall = apiInterface1.createGaze(history_id, postGazes);
        Log.d("Response", "postGaze: "+postGazes.toString());

        gazeCall.enqueue(new Callback<PostGazeResponse>() {
            @Override
            public void onResponse(Call<PostGazeResponse> call, Response<PostGazeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // 성공적인 응답을 받은 경우
                    //요청이 성공할 경우 처리할 작업
                    PostGazeResponse postGazeResponse = response.body();
                    Log.d("Response", "postGazeResponse: " +postGazeResponse.toString());
                    // postGazeResponse를 사용하여 원하는 작업 수행
                } else {
                    // 응답이 실패하거나 response.body()가 null인 경우
                    Log.e("Response", "Response is unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<PostGazeResponse> call, Throwable t) {
                Log.d("OrderActivity", "postGazeResponse 전송 실패");
            }
        });

    }


    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.order_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.order_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> home
                if(eyePermission == 1){
                    gazeTrackerDataStorage.stopGazeDataCapturing();
                }
                showDialog();
            }
        });

    }

    //home_dialog
    public void showDialog(){

        LayoutInflater layoutInflater = LayoutInflater.from(OrderActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_home, null);

        AlertDialog alertDialog = new AlertDialog.Builder(OrderActivity.this, R.style.CustomAlertDialog)
                .setView(view)
                .create();

        TextView homeTxt = view.findViewById(R.id.home_alert_home);
        TextView orderTxt = view.findViewById(R.id.home_alert_order);
        ImageView delete = view.findViewById(R.id.home_alert_delete);

        //홈 화면 이동
        homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storelist
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                //현재 데이터 삭제하기
                Data data = (Data) getApplication();
                data.initializeAllVariables();

            }
        });

        //계속 주문하기
        orderTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eyePermission == 1){
                    gazeTrackerDataStorage.startGazeDataCapturing();
                }
                alertDialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(eyePermission == 1){
                    gazeTrackerDataStorage.startGazeDataCapturing();
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    //
    // Miscellaneous
    //

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}