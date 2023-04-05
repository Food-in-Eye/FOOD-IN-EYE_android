package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuDetailActivity extends AppCompatActivity {

    ImageView menu_Img;
    TextView menu_name;
    TextView menu_desc;
    TextView menu_allergy;
    TextView menu_origin;
    TextView menu_price;

    String menu_Id;
    List<Menus> menuInfo = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        //메뉴 한줄소개, 알러지, 원산지
        menu_name = (TextView) findViewById(R.id.menuD_name);
        menu_desc = (TextView) findViewById(R.id.menuD_desc);
        menu_allergy = (TextView) findViewById(R.id.menuD_allergy);
        menu_origin = (TextView) findViewById(R.id.menuD_origin);

        //intent에서 _id값 가져오기
        Intent intent = getIntent();
        menu_Id = intent.getStringExtra("f_id");
        Log.d("f_id", "f_id : " + menu_Id);

        //retrofit2로 데이터 받아오기
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<MenuItem> callMenu = apiInterface.getMenusData(menu_Id);
        callMenu.enqueue(new Callback<MenuItem>() {
            @Override
            public void onResponse(Call<MenuItem> call, Response<MenuItem> response) {
                if (response.isSuccessful()) {
                    menuInfo = response.body().response.getMenus();

                    for (Menus menu : menuInfo) {
                        if (menu.getf_id().equals(menu_Id)) {
                            menu_name.setText(menu.getName());
                            menu_desc.setText(menu.getM_desc());
                            menu_allergy.setText(menu.getAllergy());
                            menu_origin.setText(menu.getOrigin());
                            menu_price.setText(menu.getPrice());
                            break;
                        }
                    }

                }
            }
            @Override
            public void onFailure(Call<MenuItem> call, Throwable t) {
                Log.e("STORE", "Error: " + t.getMessage());
            }
        });


    }
}