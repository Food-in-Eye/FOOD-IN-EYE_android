package com.example.foodineye_app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import com.example.foodineye_app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteActivity extends AppCompatActivity {

    LuckyWheel luckyWheel;
    List<WheelItem> wheelItems;
    String point;
    Button startBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        luckyWheel = findViewById(R.id.luck_wheel);
        startBtn = findViewById(R.id.spin_btn);
        setRoulette();

    }

    public void setRoulette(){

        //점수판 데이터 생성
        generateWheelItems();

        //점수판 타겟 정해지면 이벤트 발생
        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {

                //아이템 변수에 담기
                WheelItem wheelItem = wheelItems.get(Integer.parseInt(point)-1);

                //아이템 텍스트 변수에 담기
                String menu = wheelItem.text;

                //show(menu);
                showDialog(menu); //결과 보여주기
            }
        });

        //시작 버튼
        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Random random = new Random();
                point = String.valueOf(random.nextInt(5)+1); //1~5
                luckyWheel.rotateWheelTo(Integer.parseInt(point));
            }
        });

    }

    public void generateWheelItems(){

        wheelItems = new ArrayList<>();
        wheelItems.add(new WheelItem(Color.parseColor("#7892B5"), "시오라멘"));

        wheelItems.add(new WheelItem(Color.parseColor("#E9B9AA"), "뚝배기 불고기"));

        wheelItems.add(new WheelItem(Color.parseColor("#D98481"), "해장 라면"));

        wheelItems.add(new WheelItem(Color.parseColor("#EDCA7F"), "순두부 찌개"));

        wheelItems.add(new WheelItem(Color.parseColor("#91B5A9"), "바질 새우 파스타"));

        //데이터 넣기
        luckyWheel.addWheelItems(wheelItems);

    }

    //show dialog
    public void showDialog(String m_name){

        LayoutInflater layoutInflater = LayoutInflater.from(RouletteActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_roulette, null);

        AlertDialog alertDialog = new AlertDialog.Builder(RouletteActivity.this, R.style.CustomAlertDialog)
                .setView(view)
                .create();

        TextView menuName = view.findViewById(R.id.alert_roulette_menuName);
        TextView toRoulette = view.findViewById(R.id.alert_roulette_toMenu);
        TextView toCart = view.findViewById(R.id.alert_roulette_toCart);
        ImageView delete = view.findViewById(R.id.alert_roulette_delete);

        menuName.setText(m_name);
        toRoulette.setText("다시 돌리러 가기");
        toCart.setText("장바구니 가기");

        toRoulette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RouletteActivity.class);
                startActivity(intent);
            }
        });

        toCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ShoppingCartActivity.class);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}