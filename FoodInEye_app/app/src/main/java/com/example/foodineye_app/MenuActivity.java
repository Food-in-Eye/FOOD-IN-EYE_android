package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import retrofit2.Call;

public class MenuActivity extends AppCompatActivity {

    TextView store_intro;
    TextView store_openTime;
    TextView store_notice;

    RecyclerView menurecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //가게 한줄소개, 운영시간, 공지사항
        store_intro = (TextView) findViewById(R.id.store_intro);
        store_openTime= (TextView) findViewById(R.id.store_opentime);
        store_notice = (TextView) findViewById(R.id.store_notice);

        //category액티비티에서 받아온 데이터
        Intent intent = getIntent();

        //apiinterface에서 받아오는거
        //storeList 세팅
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<StoreItem> call = apiInterface.getData();

        //menuRecyclerview
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        menurecyclerView.setLayoutManager(gridLayoutManager);


        Button s1m1Btn = (Button) findViewById(R.id.smBtn);
        s1m1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent s1m1Intent = new Intent(getApplicationContext(), MenuDetailActivity.class);
                startActivity(s1m1Intent);
            }
        });
    }
}