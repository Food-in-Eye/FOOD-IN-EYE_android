package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.History;
import com.lakue.pagingbutton.LakuePagingButton;
import com.lakue.pagingbutton.OnPageSelectListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    Toolbar toolbar;
    LakuePagingButton lpb_buttonlist;
    int max_page;

    Context ctx;
    List<History.HistoryResponse> historyList;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;

    SharedPreferences sharedPreferences;
    String u_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        toolbar = (Toolbar) findViewById(R.id.history_toolbar);
        setToolBar(toolbar);

        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        u_id = sharedPreferences.getString("u_id", null);

        lpb_buttonlist = findViewById(R.id.lakuePagingButton);

        // 한 번에 표시되는 버튼 수 (기본값 : 5)
        lpb_buttonlist.setPageItemCount(4);

        //-----------------------------------------------------------------------
        // 초기 페이지 로딩
        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);

        Call<History> callMenu = apiInterface.getHistory(u_id, 1);

        callMenu.enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body() != null) {
                    History history = response.body();
                    Log.d("OrderHistoryActivity", "Response : " + history);

                    List<History.HistoryResponse> historyResponseList = response.body().historyResponse;

                    historyAdapter = new HistoryAdapter(getApplicationContext(), historyResponseList);
                    recyclerView.setAdapter(historyAdapter);

                    // 변경된 max_page 값을 이용해 버튼 업데이트
                    max_page = history.getMax_batch();
                    lpb_buttonlist.addBottomPageButton(max_page, 1);
                    Log.d("OrderHistoryActivity", "max_page : " + max_page);

                } else {
                    // Handle error here, show a toast or log an error message
                    Log.e("OrderHistoryActivity", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                Log.d("OrderHistoryActivity", "onFailure: " + t.toString());
            }
        });

        //-----------------------------------------------------------------------

        recyclerView = findViewById(R.id.history_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 페이지 리스너를 클릭했을 때의 이벤트
        lpb_buttonlist.setOnPageSelectListener(new OnPageSelectListener() {
            // BeforeButton Click
            @Override
            public void onPageBefore(int now_page) {
                lpb_buttonlist.addBottomPageButton(max_page, now_page);
//                Toast.makeText(OrderHistoryActivity.this, "onPageBefore" + now_page, Toast.LENGTH_SHORT).show();
                getHistory(now_page);
            }

            @Override
            public void onPageCenter(int now_page) {
//                Toast.makeText(OrderHistoryActivity.this, "onPageCenter" + now_page, Toast.LENGTH_SHORT).show();
                getHistory(now_page);
            }

            // NextButton Click
            @Override
            public void onPageNext(int now_page) {
                lpb_buttonlist.addBottomPageButton(max_page, now_page);
//                Toast.makeText(OrderHistoryActivity.this, "onPageNext" + now_page, Toast.LENGTH_SHORT).show();
                getHistory(now_page);
            }
        });

    }

    public void getHistory(int batch) {
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<History> callMenu = apiInterface.getHistory(u_id, batch);

        callMenu.enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body() != null) {
                    History history = response.body();
                    Log.d("OrderHistoryActivity", "Response : " + history);

                    List<History.HistoryResponse> historyResponseList = response.body().historyResponse;

                    historyAdapter = new HistoryAdapter(getApplicationContext(), historyResponseList);
                    recyclerView.setAdapter(historyAdapter);

                } else {
                    // Handle error here, show a toast or log an error message
                    Log.e("OrderHistoryActivity", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                Log.d("OrderHistoryActivity", "onFailure: " + t.toString());
            }
        });
    }


    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.history_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.history_home);
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
