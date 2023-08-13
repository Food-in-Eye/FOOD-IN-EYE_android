package com.example.foodineye_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryDetailActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    HistoryDetailAdapter historyDetailAdapter;
    TextView date;
    TextView total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);


        Intent intent = getIntent();
        String h_id = intent.getStringExtra("h_id");
        String total_price = intent.getStringExtra("total");

        recyclerView = (RecyclerView) findViewById(R.id.history_detail_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        date = (TextView) findViewById(R.id.detail_history_date);
        total = (TextView) findViewById(R.id.detail_history_total);
        total.setText(total_price);


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<HistoryDetail> callHistoryDetail = apiInterface.getHistoryDetail(h_id);

        callHistoryDetail.enqueue(new Callback<HistoryDetail>() {
            @Override
            public void onResponse(Call<HistoryDetail> call, Response<HistoryDetail> response) {
                if (response.isSuccessful() && response.body() != null) {
                    HistoryDetail historyDetail = response.body();
                    Log.d("HistoryDetailActivity", historyDetail.toString());

                    HistoryDetail.HistoryDetailResponse historyDetailResponse = historyDetail.historyDetailResponse;

                    date.setText(historyDetailResponse.getDate());

                    List<HistoryDetail.HistoryDetailResponse.OrderItem> orderItemList = historyDetail.historyDetailResponse.orders;
                    historyDetailAdapter = new HistoryDetailAdapter(getApplicationContext(), orderItemList);
                    recyclerView.setAdapter(historyDetailAdapter);
                    Log.d("HistoryDetailActivity", orderItemList.toString());

                } else {
                    // Handle error here
                    Log.e("OrderHistoryActivity", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<HistoryDetail> call, Throwable t) {
                // Handle failure here
            }
        });



    }
}