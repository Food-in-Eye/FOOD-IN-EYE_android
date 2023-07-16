package com.example.foodineye_app.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.GazeTrackerManager;
import com.example.foodineye_app.gaze.PermissionRequester;
import com.example.foodineye_app.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import visual.camp.sample.view.PointView;

public class StorelistActivity extends AppCompatActivity {

    StoreItem storeList; //전체 가게 목록
    List<Stores> storeInfo;
    RecyclerView recyclerView;
    StoreAdapter storeAdapter;

    ConstraintLayout storeLayout;

    //-----------------------------------------------------------------------------------------
    //gazetracker
    Context ctx;
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");

    GazeTrackerManager gazeTracker;

    //-----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelist);

        //-------------------------------------------------------------------------------------

        //start-gaze-tracking
        ctx = getApplicationContext();
        storeLayout = findViewById(R.id.storelistLayout);
        PointView viewpoint = findViewById(R.id.view_point_storelist);

        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
        }

        //-------------------------------------------------------------------------------------


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
                storeList = response.body();

                Log.d("StorelistActivity", storeList.toString());
                storeInfo = storeList.response;

                storeAdapter = new StoreAdapter(getApplicationContext(), storeInfo);
                recyclerView.setAdapter(storeAdapter);
            }

            @Override
            public void onFailure(Call<StoreItem> call, Throwable t) {
                Log.d("StorelistActivity", t.toString());
            }
        });

        //-------------------------------------------------------------------------------------

        LinearLayout schoolFoodLayout = findViewById(R.id.school_food); // school_food LinearLayout을 찾습니다.

        // 레이아웃이 최종적으로 그려진 후에 실행되는 코드 블록
        schoolFoodLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int[] location = new int[2];
                schoolFoodLayout.getLocationOnScreen(location);

                int left = location[0]; // 왼쪽 좌표
                int top = location[1]; // 위쪽 좌표
                int right = left + schoolFoodLayout.getWidth(); // 오른쪽 좌표
                int bottom = top + schoolFoodLayout.getHeight(); // 아래쪽 좌표

                // 결과 출력
                Log.d("location", "left: "+left);
                Log.d("location", "top: "+top);
                Log.d("location", "right: "+right);
                Log.d("location", "bottom: "+bottom);

                // 뷰 트리 옵저버 제거
                schoolFoodLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        //-------------------------------------------------------------------------------------

    }

    //-------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        takeAndSaveScreenShot();
        super.onStop();
        Log.d("StorelistActivity", "onStop");

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("store_list", 0, 0);
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

    //screenshot
    private void takeAndSaveScreenShot(){
//        Bitmap bitmap = getBitmapFromView(storeLayout, storeLayout.getWidth(), storeLayout.getHeight());
        Bitmap bitmap = getBitmapFromRootView(StorelistActivity.this);
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

    private Bitmap getBitmapFromView(ConstraintLayout view, int width, int height){
        view.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY));
        width = view.getMeasuredWidth();
        height = view.getMeasuredHeight();
        Log.d("screenshot", "width= "+width);
        Log.d("screenshot", "heigt= "+height);
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        return bitmap;
    }

    private void saveImage(Bitmap bitmap){
        String fileTitle = "ScreenAll.png";

        File file = new File(this.getFilesDir(), fileTitle);
        Log.d("screenshot", "fileDir"+getFilesDir());

        try {

            if (!file.exists()) { file.createNewFile(); }

            FileOutputStream fos = new FileOutputStream(file);

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();

            show("save success");

        } catch (Exception e){
            show("save fail");
            e.printStackTrace();
        }
    }

}
