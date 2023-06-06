package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;

import visual.camp.sample.view.PointView;

public class MenuDetailActivity extends AppCompatActivity {

    LinearLayout order_btn;

    ImageView menu_Img;
    TextView menu_name;
    TextView menu_desc;
    TextView menu_allergy;
    TextView menu_origin;
    TextView menu_price;

    String m_Id, s_Id, s_name, f_Id, m_name, m_imageKey;
    int m_price;

    Cart cart;

    int s_num, f_num;

    //----------------------------------------------------------------------
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");

    //----------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
        Context ctx = getApplicationContext();
        ConstraintLayout storeLayout = findViewById(R.id.menuDetailLayout);
        PointView viewpoint = findViewById(R.id.view_point_menuDetail);

        GazeTrackerDataStorage gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
        }


        //-------------------------------------------------------------------------------------

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

        s_Id = intentToDetail.getS_id();
        s_name = intentToDetail.getS_name();
        m_Id = intentToDetail.getM_id();
        f_Id = intentToDetail.food.getFood_id();
        m_name = intentToDetail.food.getM_name();
        m_price = intentToDetail.food.getM_price();
        m_imageKey = intentToDetail.food.getM_img_key();
        s_num = intentToDetail.getS_num();
        f_num = intentToDetail.food.getF_num();



        Log.d("intentToDetail", "intentToDetail_sid" + s_Id);
        Log.d("intentToDetail", "intentToDetail_mid" + m_Id);


        menu_name.setText(intentToDetail.food.getM_name());
        menu_desc.setText(intentToDetail.food.getM_desc());
        menu_allergy.setText(intentToDetail.food.getM_allergy());
        menu_origin.setText(intentToDetail.food.getM_origin());
        menu_price.setText(String.valueOf(intentToDetail.food.getM_price()));

        String imageUrl = "https://foodineye.s3.ap-northeast-2.amazonaws.com/" + m_imageKey;
        Glide.with(this)
                .load(imageUrl)
                .circleCrop()
                .into(menu_Img);

        final Data data = (Data) getApplicationContext();
        order_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cart = new Cart(s_Id, s_name, m_Id, f_Id, m_name, m_price, m_imageKey);
                data.setCartList(cart);
                data.setRecentS_id(cart.s_id);
                data.setRecentM_id(cart.m_id);
                Log.d("MenuDetailActivity", "cart: "+cart.toString());
                showDialog();
            }
        });
    }

    public void showDialog(){

        LayoutInflater layoutInflater = LayoutInflater.from(MenuDetailActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_custom, null);

        AlertDialog alertDialog = new AlertDialog.Builder(MenuDetailActivity.this, R.style.CustomAlertDialog)
                .setView(view)
                .create();

        TextView menuName = view.findViewById(R.id.alert_menuName);
        TextView toMenu = view.findViewById(R.id.alert_toMenu);
        TextView toCart = view.findViewById(R.id.alert_toCart);
        ImageView delete = view.findViewById(R.id.alert_delete);

        menuName.setText(m_name);
        toMenu.setText("더 담으러 가기");
        toCart.setText("장바구니 가기");

        toMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                Log.d("MenuDetailActivity", "storeID"+s_Id);
                intent.putExtra("intent_SId", s_Id);
                intent.putExtra("intent_mId", m_Id);
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


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("MenuDetailActivitysss", "onStop");

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("menu_detail", s_num,f_num);
        }
//        gazeTracker.removeCallbacks(
//                gazeCallback, calibrationCallback, statusCallback, userStatusCallback);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backgroundThread.quitSafely();
    }

    //
    // Miscellaneous
    //

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}