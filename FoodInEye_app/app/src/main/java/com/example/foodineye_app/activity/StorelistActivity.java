package com.example.foodineye_app.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StorelistActivity extends AppCompatActivity {

    StoreItem storeList; //전체 가게 목록
    List<Stores> storeInfo;
    RecyclerView recyclerView;
    StoreAdapter storeAdapter;

    //gazetracker
    GazeTrackerDataStorage gazeTrackerDataStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelist);

        //start-gaze-tracking
        LinearLayout linearLayout = findViewById(R.id.storelistLinearlayout);
        gazeTrackerDataStorage.setGazeTracker(linearLayout);

        //storeInfo 객체
        storeInfo = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_StoreList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //storeList 세팅
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<StoreItem> call = apiInterface.getData();

        call.enqueue(new Callback<StoreItem>() {
            @Override
            public void onResponse(Call<StoreItem> call, Response<StoreItem> response) {
                storeList=response.body();

                Log.d("StorelistActivity", storeList.toString());
                storeInfo=storeList.response;

                storeAdapter = new StoreAdapter(getApplicationContext(), storeInfo);
                recyclerView.setAdapter(storeAdapter);
            }

            @Override
            public void onFailure(Call<StoreItem> call, Throwable t) {
                Log.d("StorelistActivity", t.toString());
            }
        });

    }
}