package com.example.foodineye_app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryDetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HistoryDetailAdapter historyDetailAdapter;
    TextView dateTime;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);


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

        Call<HistoryDetail> callHistoryDetail = apiInterface.getHistoryDetail(h_id);

        callHistoryDetail.enqueue(new Callback<HistoryDetail>() {
            @Override
            public void onResponse(Call<HistoryDetail> call, Response<HistoryDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HistoryDetail historyDetail = response.body();
                    Log.d("OrderHistoryDetailActivity", historyDetail.toString());

                    HistoryDetail.HistoryDetailResponse historyDetailResponse = historyDetail.historyDetailResponse;

                    List<HistoryDetail.HistoryDetailResponse.OrderItem> orderItemList = historyDetailResponse.orders;
                    HistoryDetailAdapter historyDetailAdapter = new HistoryDetailAdapter(getApplicationContext(), orderItemList);
                    recyclerView.setAdapter(historyDetailAdapter);


                    String inputDateTime = historyDetailResponse.getDate();
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
            public void onFailure(Call<HistoryDetail> call, Throwable t) {
                Log.d("OrderHistoryDetailActivity", "onFailure: " + t.toString());
            }
        });




    }
}