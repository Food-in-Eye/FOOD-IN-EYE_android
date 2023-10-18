package com.example.foodineye_app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.example.foodineye_app.gaze.RouletteData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RouletteActivity extends AppCompatActivity {

    LuckyWheel luckyWheel;
    List<WheelItem> wheelItems;
    String point;
    Button startBtn;

    RouletteData[] receivedTop5List;

    List<Cart> cartCandList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roulette);

        // top5List 데이터가져옴
        receivedTop5List = (RouletteData[]) getIntent().getSerializableExtra("top5List");
        Log.d("룰렛", "receivedTop5List: "+receivedTop5List.toString());

        luckyWheel = findViewById(R.id.luck_wheel);
        startBtn = findViewById(R.id.spin_btn);
        setRoulette();

    }

    public void setRoulette(){

        //점수판 데이터 생성
        findNamesForTop5List();

        //점수판 타겟 정해지면 이벤트 발생
        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {

                if (point != null && !point.isEmpty()) {
                    int pointValue = Integer.parseInt(point) - 1;
                    if (pointValue >= 0 && pointValue < wheelItems.size()) {
                        WheelItem wheelItem = wheelItems.get(pointValue);
                        String menu = wheelItem.text;
                        showDialog(menu);
                    } else {
                        // 처리할 수 없는 인덱스
                    }
                } else {
                    // point 값이 비어있거나 null인 경우 처리
                }

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
        toRoulette.setText("한 번 더");
        toCart.setText("장바구니 가기");

        toRoulette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        toCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(Cart cart: cartCandList){
                    if(cart.m_name.equals(m_name)){
                        Data data = (Data) getApplicationContext();
                        data.setCartList(cart);
                    }
                }

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

    //s_num, f_num으로 f_name 찾기
    private void findNamesForTop5List() {
        if (receivedTop5List != null) {
            wheelItems = new ArrayList<>(); // 새 ArrayList로 초기화

            // 나머지 코드는 그대로 유지
            String[] colors = new String[]{"#7892B5", "#E9B9AA", "#D98481", "#EDCA7F", "#91B5A9"};
            for (int i = 0; i < receivedTop5List.length; i++) {
                RouletteData rouletteData = receivedTop5List[i];
                if (rouletteData != null) {
                    int s_num = rouletteData.getS_num();
                    int f_num = rouletteData.getF_num();
                    Cart cartCand = ((Data)getApplication()).findMenu(s_num, f_num);
                    cartCandList.add(cartCand);
                    String foodName = cartCand.m_name;

                    if (foodName != null) {
                        String color = colors[i % colors.length];
                        WheelItem wheelItem = new WheelItem(Color.parseColor(color), foodName);
                        wheelItems.add(wheelItem);
                    }
                }
            }

            // 색상 및 음식 이름이 추가된 wheelItems를 설정
            luckyWheel.addWheelItems(wheelItems);
        }
    }

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
}