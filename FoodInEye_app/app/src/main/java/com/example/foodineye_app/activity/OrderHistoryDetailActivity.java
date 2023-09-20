package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.GetHistoryDetail;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryDetailActivity extends AppCompatActivity {

    Toolbar toolbar;

    RecyclerView recyclerView;
    HistoryDetailAdapter historyDetailAdapter;
    TextView dateTime;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);

        toolbar = (Toolbar) findViewById(R.id.history_detail_toolbar);
        setToolBar(toolbar);

        Intent intent = getIntent();
        String h_id = intent.getStringExtra("h_id");
        String total_price = intent.getStringExtra("total");

        Log.d("OrderHistoryDetailActivity", "h_id: "+h_id);


        recyclerView = (RecyclerView) findViewById(R.id.history_detail_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        dateTime = (TextView) findViewById(R.id.detail_history_date);
        total = (TextView) findViewById(R.id.detail_history_total);
        total.setText(total_price);

        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);

        Call<GetHistoryDetail> callHistoryDetail = apiInterface.getHistoryDetail(h_id);

        callHistoryDetail.enqueue(new Callback<GetHistoryDetail>() {
            @Override
            public void onResponse(Call<GetHistoryDetail> call, Response<GetHistoryDetail> response) {
                if (response.isSuccessful() && response.body() != null) {

                    GetHistoryDetail getHistoryDetail = response.body();

                    List<GetHistoryDetail.OrderItem> orderItemList = getHistoryDetail.getOrders();
                    HistoryDetailAdapter historyDetailAdapter = new HistoryDetailAdapter(getApplicationContext(), orderItemList);
                    recyclerView.setAdapter(historyDetailAdapter);

                    String inputDateTime = getHistoryDetail.getDate();
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy년 MM월 dd일 HH:mm:ss");

                    try {
                        Date date = inputFormat.parse(inputDateTime);
                        String formattedDate = outputFormat.format(date);

                        dateTime.setText(formattedDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                } else {
                    // Handle error here, show a toast or log an error message
                    Log.e("OrderHistoryDetailActivity", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<GetHistoryDetail> call, Throwable t) {
                Log.d("OrderHistoryDetailActivity", "onFailure: " + t.toString());
            }
        });


    }


    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.history_detail_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.history_detail_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> home
                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }
}