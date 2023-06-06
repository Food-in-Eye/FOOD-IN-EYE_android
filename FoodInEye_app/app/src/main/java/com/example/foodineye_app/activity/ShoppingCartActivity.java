package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;

import java.util.List;

import visual.camp.sample.view.PointView;

public class ShoppingCartActivity extends AppCompatActivity implements CartAdapter.OnItemClickListener {

    List<Cart> cartList;
    RecyclerView recyclerView;
    CartAdapter cartAdapter;
    TextView totalPrice;
    TextView toOrder;

    int total;

    //-----------------------------------------------------------------------------------------
    //gazetracker
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");

    //-----------------------------------------------------------------------------------------


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);

        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
        Context ctx = getApplicationContext();
        ConstraintLayout storeLayout = findViewById(R.id.cartLayout);
        PointView viewpoint = findViewById(R.id.view_point_cart);

        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
        }


        //-------------------------------------------------------------------------------------

        cartList = ((Data) getApplication()).getCartList();
        total = ((Data)getApplication()).getTotalPrice();

        Log.d("ShoppingCart", "total" + total);
        Log.d("ShoppingCart", "cartList" + cartList.toString());

        recyclerView = findViewById(R.id.recyclerView_cartList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        cartAdapter = new CartAdapter(getApplicationContext(), cartList, this);
        recyclerView.setAdapter(cartAdapter);

        //총 가격
        totalPrice = (TextView) findViewById(R.id.cart_totalPrice);
        totalPrice.setText(String.valueOf(total));

        //주문하기
        toOrder = (TextView) findViewById(R.id.orderBtn);
        toOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), OrderActivity.class);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onItemClick(){
        int total = ((Data)getApplication()).getTotalPrice();
        totalPrice.setText(String.valueOf(total));
    }
    @Override
    public void onDeleteClick(int position){
        cartList.remove(position);
        cartAdapter.notifyItemRemoved(position);
    }
    @Override
    public void onToMenuClick(){
        String s_id = ((Data)getApplication()).getRecentS_id();
        String m_id = ((Data)getApplication()).getRecentM_id();
        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
        intent.putExtra("intent_recentSId", s_id);
        intent.putExtra("intent_cartMId", m_id);
        startActivity(intent);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d("StorelistActivity", "onStop");

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker();
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