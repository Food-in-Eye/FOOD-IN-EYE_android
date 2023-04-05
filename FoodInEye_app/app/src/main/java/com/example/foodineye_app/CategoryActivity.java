package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryActivity extends AppCompatActivity {

    StoreItem storeList; //전체 가게 목록
    List<Stores> storeInfo;

    RecyclerView recyclerView;
    StoreAdapter storeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

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

                Log.d("CategoryActivity", storeList.toString());
                storeInfo=storeList.response;

                storeAdapter = new StoreAdapter(getApplicationContext(), storeInfo);
                recyclerView.setAdapter(storeAdapter);
            }

            @Override
            public void onFailure(Call<StoreItem> call, Throwable t) {
                Log.d("CategoryActivity", t.toString());
            }
        });

        //category -> menu
        /*Button s1Btn = (Button) findViewById(R.id.sBtn);
        s1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s1Intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(s1Intent);
            }
        });

         */
    }
}