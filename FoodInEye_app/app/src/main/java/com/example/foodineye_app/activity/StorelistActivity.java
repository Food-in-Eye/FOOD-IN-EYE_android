package com.example.foodineye_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.GetStoreList;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import visual.camp.sample.view.PointView;

public class StorelistActivity extends AppCompatActivity {

    Toolbar toolbar;

    GetStoreList storeList; //전체 가게 목록
    List<GetStoreList.Stores> storeInfo;
    RecyclerView recyclerView;
    StoreAdapter storeAdapter;
    ConstraintLayout storeLayout;

    SharedPreferences sharedPreferences;
    int eyePermission;

    //-----------------------------------------------------------------------------------------
    //gazeTracker
    Context ctx;
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");
    PointView viewpoint;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storelist);

        toolbar = (Toolbar) findViewById(R.id.stolelist_toolbar);
        setToolBar(toolbar);

        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        eyePermission = sharedPreferences.getInt("eye_permission", 0);

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

        //시선 권한 동의 여부 확인
        if(eyePermission == 1){ //true
            setGazeTrackerDataStorage();
        }
    }

    @Override
    protected void onStop() {
//        takeAndSaveScreenShot();
        dpToPixel();
        super.onStop();
        if(eyePermission == 1){
            stopGazeTracker();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(eyePermission == 1){
            gazeTrackerDataStorage.quitBackgroundThread();
            backgroundThread.quitSafely();
        }
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
        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface = apiClient.getClient().create(ApiInterface.class);

//        ApiInterface apiInterface = ApiClientEx.getExClient().create(ApiInterface.class);

        Call<GetStoreList> call = apiInterface.getStore();
        call.enqueue(new Callback<GetStoreList>() {
            @Override
            public void onResponse(Call<GetStoreList> call, Response<GetStoreList> response) {
                if(response.isSuccessful()){

                    storeList = response.body();
                    storeInfo = storeList.response;

                    storeAdapter = new StoreAdapter(getApplicationContext(), storeInfo);
                    recyclerView.setAdapter(storeAdapter);


                }else{
                    // 응답이 실패한 경우에 대한 처리를 여기서 수행합니다.
                    String errorBody = null; // 실패한 응답의 본문을 얻음
                    try {
                        errorBody = response.errorBody().string();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    Log.i("StorelistActivity", "로그인 응답 오류: " + response.code() + ", " + errorBody);
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

        } catch (Exception e){
//            show("save fail");
            e.printStackTrace();
        }
    }

    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.storelist_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.storelist_home);
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-> home, gaze 일시정지
                if(eyePermission == 1){ //true
                    gazeTrackerDataStorage.stopGazeDataCapturing();
                }

                showDialog();
            }
        });

    }

    //home_dialog
    public void showDialog(){

        LayoutInflater layoutInflater = LayoutInflater.from(StorelistActivity.this);
        View view = layoutInflater.inflate(R.layout.alert_dialog_home, null);

        AlertDialog alertDialog = new AlertDialog.Builder(StorelistActivity.this, R.style.CustomAlertDialog)
                .setView(view)
                .create();

        TextView homeTxt = view.findViewById(R.id.home_alert_home);
        TextView orderTxt = view.findViewById(R.id.home_alert_order);
        ImageView delete = view.findViewById(R.id.home_alert_delete);

        //홈 화면 이동
        homeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //storelist
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                //현재 데이터 삭제하기
                Data data = (Data) getApplication();
                data.initializeAllVariables();

                Log.d("Data 초기화: ", "data 초기화 : "+data.toString());
            }
        });

        //계속 주문하기
        orderTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // orderBtn 클릭 시 Gaze 데이터 수집 재개
                if(eyePermission == 1){ //true
                    gazeTrackerDataStorage.startGazeDataCapturing();
                }

                alertDialog.dismiss();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(eyePermission == 1){ //true
                    gazeTrackerDataStorage.startGazeDataCapturing();
                }
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void dpToPixel(){
        float dpValue = 230; // 변환하려는 dp 값
        float density = getResources().getDisplayMetrics().density;
        int pixelValue = (int) (dpValue * density + 0.5f);
        Log.d("location", "pixelToDP"+pixelValue);
    }


}
