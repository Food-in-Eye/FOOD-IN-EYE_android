package com.example.foodineye_app;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuDetailActivity extends AppCompatActivity {

    LinearLayout order_btn;

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

        order_btn = (LinearLayout) findViewById(R.id.menuD_btn);

        //메뉴 한줄소개, 알러지, 원산지
        menu_name = (TextView) findViewById(R.id.menuD_name);
        menu_Img = (ImageView) findViewById(R.id.menuD_img);
        menu_desc = (TextView) findViewById(R.id.menuD_desc);
        menu_allergy = (TextView) findViewById(R.id.menuD_allergy);
        menu_origin = (TextView) findViewById(R.id.menuD_origin);
        menu_price = (TextView) findViewById(R.id.menuD_price);

        //intent에서 Food 가져오기
        Intent intent = getIntent();
        IntentToDetail intentToDetail = (IntentToDetail) intent.getSerializableExtra("intentToDetail");

        menu_name.setText(intentToDetail.food.getM_name());
        menu_desc.setText(intentToDetail.food.getM_desc());
        menu_allergy.setText(intentToDetail.food.getM_allergy());
        menu_origin.setText(intentToDetail.food.getM_origin());
        menu_price.setText(String.valueOf(intentToDetail.food.getM_price()));

        String imageKey = intentToDetail.food.getM_img_key();
        String imageUrl = "https://foodineye.s3.ap-northeast-2.amazonaws.com/" + imageKey;
        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(menu_Img);

        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


    }
    public void showDialog(){
        AlertDialog.Builder msgBuilder = new AlertDialog.Builder(MenuDetailActivity.this)
                .setTitle("Order")
                .setMessage("Order")
                .setNegativeButton("더 담으러 가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                        //intent.putExtra("_id", )
                        startActivity(intent);
                    }
                })
                .setPositiveButton("장바구니 담으러가기", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), ShoppingCartActivity.class);
                        startActivity(intent);
                    }
                });
        AlertDialog msgDlg = msgBuilder.create();
        msgDlg.show();
    }
}