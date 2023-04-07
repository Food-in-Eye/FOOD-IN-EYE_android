package com.example.foodineye_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

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
        menu_Img = (ImageView) findViewById(R.id.menuD_img);
        menu_desc = (TextView) findViewById(R.id.menuD_desc);
        menu_allergy = (TextView) findViewById(R.id.menuD_allergy);
        menu_origin = (TextView) findViewById(R.id.menuD_origin);
        menu_price = (TextView) findViewById(R.id.menuD_price);

        //intent에서 Food 가져오기
        Intent intent = getIntent();
        Food food = (Food) intent.getSerializableExtra("food");

        menu_name.setText(food.getM_name());
        menu_desc.setText(food.getM_desc());
        menu_allergy.setText(food.getM_allergy());
        menu_origin.setText(food.getM_origin());
        menu_price.setText(String.valueOf(food.getM_price()));

        String imageKey = food.getM_img_key();
        String imageUrl = "https://foodineye.s3.ap-northeast-2.amazonaws.com/" + imageKey;
        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(menu_Img);

    }
}