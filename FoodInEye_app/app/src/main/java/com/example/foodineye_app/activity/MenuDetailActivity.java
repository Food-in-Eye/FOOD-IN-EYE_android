package com.example.foodineye_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;

import java.io.File;
import java.io.FileOutputStream;

import visual.camp.sample.view.PointView;

public class MenuDetailActivity extends AppCompatActivity {

    androidx.appcompat.widget.Toolbar toolbar;

    LinearLayout order_btn;
    ImageView menu_Img;
    TextView menu_name;
    String menuName;
    TextView menu_desc;
    TextView menu_allergy;
    TextView menu_origin;
    TextView menu_price;

    String m_Id, s_Id, s_name, f_Id, m_name, m_imageKey;
    int m_price;

    Cart cart;

    int s_num, f_num;

    //----------------------------------------------------------------------
    Context ctx;
    ConstraintLayout menuDetailLayout;
    PointView viewpoint;
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");
    boolean isCartClicked = false;

    //----------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_detail);

        toolbar = (Toolbar) findViewById(R.id.menu_detail_toolbar);
        setToolBar(toolbar);

        //-------------------------------------------------------------------------------------
        //screenshot
        TextView menuD_name = findViewById(R.id.menuD_name);

        // 레이아웃이 최종적으로 그려진 후에 실행되는 코드 블록
        menuD_name.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                menuD_name.getLocationOnScreen(location);

                int left = location[0]; // 왼쪽 좌표
                int top = location[1]; // 위쪽 좌표
                int right = left + menuD_name.getWidth(); // 오른쪽 좌표
                int bottom = top + menuD_name.getHeight(); // 아래쪽 좌표

                // 결과 출력
                Log.d("location", "menuD_name_left: "+left);
                Log.d("location", "menuD_name_top: "+top);
                Log.d("location", "menuD_name_right: "+right);
                Log.d("location", "menuD_name_bottom: "+bottom);

                // 뷰 트리 옵저버 제거
                menuD_name.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        ImageView menuD_img = findViewById(R.id.menuD_img);

        // 레이아웃이 최종적으로 그려진 후에 실행되는 코드 블록
        menuD_name.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                menuD_img.getLocationOnScreen(location);

                int left = location[0]; // 왼쪽 좌표
                int top = location[1]; // 위쪽 좌표
                int right = left + menuD_img.getWidth(); // 오른쪽 좌표
                int bottom = top + menuD_img.getHeight(); // 아래쪽 좌표

                // 결과 출력
                Log.d("location", "menuD_img_left: "+left);
                Log.d("location", "menuD_img_top: "+top);
                Log.d("location", "menuD_img_right: "+right);
                Log.d("location", "menuD_img_bottom: "+bottom);

                // 뷰 트리 옵저버 제거
                menuD_img.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        LinearLayout menu_description = findViewById(R.id.menu_description);

        // 레이아웃이 최종적으로 그려진 후에 실행되는 코드 블록
        menuD_name.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                menu_description.getLocationOnScreen(location);

                int left = location[0]; // 왼쪽 좌표
                int top = location[1]; // 위쪽 좌표
                int right = left + menu_description.getWidth(); // 오른쪽 좌표
                int bottom = top + menu_description.getHeight(); // 아래쪽 좌표

                // 결과 출력
                Log.d("location", "menu_description_left: "+left);
                Log.d("location", "menu_description_top: "+top);
                Log.d("location", "menu_description_right: "+right);
                Log.d("location", "menu_description_bottom: "+bottom);

                // 뷰 트리 옵저버 제거
                menu_description.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        LinearLayout menuD_btn = findViewById(R.id.menuD_btn); // school_food LinearLayout을 찾습니다.

        // 레이아웃이 최종적으로 그려진 후에 실행되는 코드 블록
        menuD_btn.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                menuD_btn.getLocationOnScreen(location);

                int left = location[0]; // 왼쪽 좌표
                int top = location[1]; // 위쪽 좌표
                int right = left + menuD_btn.getWidth(); // 오른쪽 좌표
                int bottom = top + menuD_btn.getHeight(); // 아래쪽 좌표

                // 결과 출력
                Log.d("location", "menuD_btn_left: "+left);
                Log.d("location", "menuD_btn_top: "+top);
                Log.d("location", "menuD_btn_right: "+right);
                Log.d("location", "menuD_btn_bottom: "+bottom);

                // 뷰 트리 옵저버 제거
                menuD_btn.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
        ctx = getApplicationContext();
        menuDetailLayout = findViewById(R.id.menuDetailLayout);
        viewpoint = findViewById(R.id.view_point_menuDetail);

        setGazeTrackerDataStorage();
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

        Log.d("intentToDetail", "intentToDetail_sid " + s_Id);
        Log.d("intentToDetail", "intentToDetail_mid " + m_Id);
        Log.d("intentToDetail", "intentToDetail_snum: " + s_num);
        Log.d("intentToDetail", "intentToDetail_fnum:  " + f_num);


        menu_name.setText(intentToDetail.food.getM_name());
        menuName = intentToDetail.food.getM_name();
        menu_desc.setText(intentToDetail.food.getM_desc());
        menu_allergy.setText(intentToDetail.food.getM_allergy());
        menu_origin.setText(intentToDetail.food.getM_origin());
        menu_price.setText(String.valueOf(intentToDetail.food.getM_price()));

        String imageUrl = "https://foodineye2.s3.ap-northeast-2.amazonaws.com/" + m_imageKey;
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

    @Override
    protected void onStart() {
        super.onStart();
        setGazeTrackerDataStorage();
    }

    @Override
    protected void onStop() {
        takeAndSaveScreenShot();
        super.onStop();
        Log.d("MenuDetailActivity", "onStop");
        stopGazeTracker();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        gazeTrackerDataStorage.quitBackgroundThread();
        backgroundThread.quitSafely();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finish();
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
                isCartClicked = true;

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

    //gazeTracker
    private void setGazeTrackerDataStorage(){
        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, menuDetailLayout, viewpoint);
        }
    }

    private void stopGazeTracker(){
        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("menu_detail", s_num, f_num);
        }
    }

    //
    // Miscellaneous
    //

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //screenshot
    private void takeAndSaveScreenShot(){
        Bitmap bitmap = getBitmapFromRootView(MenuDetailActivity.this);
        saveImage(bitmap);
    }

    private Bitmap getBitmapFromRootView(Activity context){
        View root = context.getWindow().getDecorView().getRootView();
        root.setDrawingCacheEnabled(true);
        root.buildDrawingCache();
        //루트뷰의 캐시를 가져옴
        Bitmap screenshot = root.getDrawingCache();

        // get view coordinates
        int[] location = new int[2];
        root.getLocationInWindow(location);

        // 이미지를 자를 수 있으나 전체 화면을 캡쳐 하도록 함
        Bitmap bmp = Bitmap.createBitmap(screenshot, location[0], location[1], root.getWidth(), root.getHeight(), null, false);

        return bmp;
    }

    private void saveImage(Bitmap bitmap) {
        String fileTitle = "menu_detail_" + menuName + ".png";

        File file = new File(this.getFilesDir(), fileTitle);
        Log.d("screenshot", "fileDir" + getFilesDir());


        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            show("save success");
        } catch (Exception e) {
            show("save fail");
            e.printStackTrace();
        }
    }

    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.menu_detail_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.menu_detail_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> home
                Intent loginIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(loginIntent);
                finish();
            }
        });

    }

}