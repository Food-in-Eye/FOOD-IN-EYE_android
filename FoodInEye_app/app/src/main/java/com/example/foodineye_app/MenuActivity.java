package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    TextView store_intro;
    TextView store_openTime;
    TextView store_notice;

    RecyclerView menurecyclerView;

    StoreItem storeList; //전체 가게 목록
    List<Stores> storeInfo = new ArrayList<>(); //가게 한줄소개, 운영시간, 공지사항
    String storeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        store_intro = (TextView) findViewById(R.id.store_intro);
        store_openTime= (TextView) findViewById(R.id.store_opentime);
        store_notice = (TextView) findViewById(R.id.store_notice);

        //intent에서 _id값 가져오기
        Intent intent = getIntent();
        storeId = intent.getStringExtra("_id");

        //retrofit2로 데이터 받아오기
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<StoreItem> call = apiInterface.getData();
        call.enqueue(new Callback<StoreItem>() {
            @Override
            public void onResponse(Call<StoreItem> call, Response<StoreItem> response) {
                if(response.isSuccessful()){
                    storeList=response.body();
                    storeInfo=storeList.response;
                    Log.d("storeInfo: ", "storeInfo" + storeInfo);
                    for(Stores store: storeInfo){
                        if(store.get_id().equals(storeId)){
                            store_intro.setText(store.getDesc());
                            store_openTime.setText(store.getSchedule());
                            store_notice.setText(store.getNotice());
                            Log.d("STORE", "success");
                            break;
                        }
                        Log.d("STORE", "ID: " + storeId);
                    }
                }
            }
            @Override
            public void onFailure(Call<StoreItem> call, Throwable t) {
                Log.e("STORE", "Error: " + t.getMessage());
            }
        });

        /*
        Call<List<Stores>> call = apiInterface.getStores();
        call.enqueue(new Callback<List<Stores>>() {
            @Override
            public void onResponse(Call<List<Stores>> call, Response<List<Stores>> response) {
                if(response.isSuccessful()){
                    List<Stores> stores = response.body();
                    for(Stores store: stores){
                        if(store.get_id() == storeId){
                            store_intro.setText(store.getDesc());
                            store_openTime.setText(store.getSchedule());
                            store_notice.setText(store.getNotice());
                            break;
                        }
                        Log.d("STORE", "ID: " + storeId);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Stores>> call, Throwable t) {
                Log.e("STORE", "Error: " + t.getMessage());
            }
        });
        */


        //menuRecyclerview
        //GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        //menurecyclerView.setLayoutManager(gridLayoutManager);

    }
}