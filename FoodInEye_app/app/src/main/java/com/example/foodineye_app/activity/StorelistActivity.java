package com.example.foodineye_app.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClientEx;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.GetStoreList;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import visual.camp.sample.view.PointView;

public class StorelistActivity extends AppCompatActivity {

    GetStoreList storeList; //전체 가게 목록
    List<GetStoreList.Stores> storeInfo;
    RecyclerView recyclerView;
    StoreAdapter storeAdapter;
    ConstraintLayout storeLayout;

    //-----------------------------------------------------------------------------------------
    //gazeTracker
    Context ctx;
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");
    PointView viewpoint;

    //-----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelist);

        //start-gaze-tracking
        ctx = getApplicationContext();
        storeLayout = findViewById(R.id.storelistLayout);
        viewpoint = findViewById(R.id.view_point_storelist);

        //storeInfo 객체
        storeInfo = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_StoreList);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        setStoreList();

        //-------------------------------------------------------------------------------------
        //screenshot
        LinearLayout schoolFoodLayout = findViewById(R.id.school_food);

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


    }

    //-------------------------------------------------------------------------------------------

    @Override
    protected void onStart() {
        super.onStart();
        setGazeTrackerDataStorage();
    }

    @Override
    protected void onStop() {
        takeAndSaveScreenShot();
        super.onStop();
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

    //gazeTracker
    private void setGazeTrackerDataStorage(){
        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
        }
    }

    private void stopGazeTracker(){
        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("store_list", 0, 0);
        }
    }

    public void setStoreList(){

        //storeList 세팅
//        ApiClient apiClient = new ApiClient(getApplicationContext());
//        apiClient.initializeHttpClient();
//
//        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);

        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);

        Call<GetStoreList> call = apiInterface.getData();
        call.enqueue(new Callback<GetStoreList>() {
            @Override
            public void onResponse(Call<GetStoreList> call, Response<GetStoreList> response) {
                if(response.isSuccessful()){

                    storeList = response.body();
                    storeInfo = storeList.response;

                    storeAdapter = new StoreAdapter(getApplicationContext(), storeInfo);
                    recyclerView.setAdapter(storeAdapter);


                }else{

                }
            }

            @Override
            public void onFailure(Call<GetStoreList> call, Throwable t) {
                Log.d("StorelistActivity", t.toString());
            }
        });


    }

    //
    // Miscellaneous
    //

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //screenshot
    private void takeAndSaveScreenShot(){
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
