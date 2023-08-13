package com.example.foodineye_app.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.R;
import com.lakue.pagingbutton.LakuePagingButton;
import com.lakue.pagingbutton.OnPageSelectListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    LakuePagingButton lpb_buttonlist;
    int page = 1;
    int max_page = 30;
    int now_page;

    Context ctx;
    List<History.HistoryResponse> historyList;
    RecyclerView recyclerView;
    HistoryAdapter historyAdapter;

    String u_id = "6458f67e50bde95733e4b57f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        lpb_buttonlist = findViewById(R.id.lakuePagingButton);


        //한 번에 표시되는 버튼 수 (기본값 : 5)
        lpb_buttonlist.setPageItemCount(4);

        //총 페이지 버튼 수와 현재 페이지 설정
        lpb_buttonlist.addBottomPageButton(max_page,1);

        recyclerView = (RecyclerView) findViewById(R.id.history_recyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        getHistory(page);

        //페이지 리스너를 클릭했을 때의 이벤트
        lpb_buttonlist.setOnPageSelectListener(new OnPageSelectListener() {
            //BeforeButton Click
            @Override
            public void onPageBefore(int now_page) {
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
                Toast.makeText(OrderHistoryActivity.this, "onPageBefore"+now_page, Toast.LENGTH_SHORT).show();
                getHistory(now_page);
            }

            @Override
            public void onPageCenter(int now_page) {
                Toast.makeText(OrderHistoryActivity.this, "onPageCenter"+now_page, Toast.LENGTH_SHORT).show();
                //  lpb_buttonlist.addBottomPageButton(max_page,page);
                getHistory(now_page);
            }

            //NextButton Click
            @Override
            public void onPageNext(int now_page) {
                Toast.makeText(OrderHistoryActivity.this, "onPageNext"+now_page, Toast.LENGTH_SHORT).show();
                lpb_buttonlist.addBottomPageButton(max_page,now_page);
                getHistory(now_page);
            }
        });

        
    }

    public void getHistory(int batch){
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<History> callMenu = apiInterface.getHistory(u_id, batch);
        callMenu.enqueue(new Callback<History>() {
            @Override
            public void onResponse(Call<History> call, Response<History> response) {
                if (response.isSuccessful() && response.body() != null && response.body().historyResponse != null) {
                    History.HistoryResponse historyResponse = response.body().historyResponse;

                    historyAdapter = new HistoryAdapter(getApplicationContext(), historyResponse);
                    recyclerView.setAdapter(historyAdapter);
                    Log.d("OrderHistoryActivity", historyResponse.toString());
                } else {
                    // Handle error here, show a toast or log an error message
                    Log.e("OrderHistoryActivity", "Response unsuccessful or body is null");
                }
            }

            @Override
            public void onFailure(Call<History> call, Throwable t) {
                Log.d("OrderHistoryActivity", t.toString());
            }
        });

    }


}