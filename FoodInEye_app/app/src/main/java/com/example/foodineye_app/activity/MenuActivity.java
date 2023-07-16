package com.example.foodineye_app.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import visual.camp.sample.view.PointView;

public class MenuActivity extends AppCompatActivity{

    TextView store_intro;
    TextView store_openTime;
    TextView store_notice;

    RecyclerView menurecyclerView;
    MenuAdapter menuAdapter;

    MenuItem menuItem; // 전체 메뉴판 목록
    //MenuItem.Response menuResponse;
    List<MenuItem.Response> menuResponse = new ArrayList<>();
    List<Menus> menuInfo = new ArrayList<>();

    StoreItem storeList; //전체 가게 목록
    List<Stores> storeInfo = new ArrayList<>();

    TabLayout tabLayout;

    String tab_Id, tabM_id; // 탭에 _ID, m_ID 할당

    //-----------------------------------------------------------------------------------------
    Context ctx;
    ConstraintLayout storeLayout;
    PointView viewpoint;
    //gazetracker
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");
    int sNum, fNum;
    int recent_sNum;

    //-----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        TabLayout tabLayout = findViewById(R.id.store_tab);

        // TabLayout의 위치 정보를 가져오기 위해 ViewTreeObserver를 사용
        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int[] tabLocation = new int[2];
                tabLayout.getLocationOnScreen(tabLocation);

                int left = tabLocation[0]; // TabLayout의 왼쪽 좌표
                int top = tabLocation[1]; // TabLayout의 위쪽 좌표
                int right = left + tabLayout.getWidth(); // TabLayout의 오른쪽 좌표
                int bottom = top + tabLayout.getHeight(); // TabLayout의 아래쪽 좌표

                // 결과 출력
                Log.d("location", "TabLayout - Left: " + left);
                Log.d("location", "TabLayout - Top: " + top);
                Log.d("location", "TabLayout - Right: " + right);
                Log.d("location", "TabLayout - Bottom: " + bottom);
            }
        });

        LinearLayout store_description = findViewById(R.id.store_description);

        // store_description 위치 정보를 가져오기 위해 ViewTreeObserver를 사용
        tabLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                store_description.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                int[] tabLocation = new int[2];
                store_description.getLocationOnScreen(tabLocation);

                int left = tabLocation[0]; // TabLayout의 왼쪽 좌표
                int top = tabLocation[1]; // TabLayout의 위쪽 좌표
                int right = left + store_description.getWidth(); // TabLayout의 오른쪽 좌표
                int bottom = top + store_description.getHeight(); // TabLayout의 아래쪽 좌표

                // 결과 출력
                Log.d("location", "store_description - Left: " + left);
                Log.d("location", "store_description - Top: " + top);
                Log.d("location", "store_description - Right: " + right);
                Log.d("location", "store_description - Bottom: " + bottom);
            }
        });


        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
        ctx = getApplicationContext();
        storeLayout = findViewById(R.id.menuLayout);
        viewpoint = findViewById(R.id.view_point_menu);

        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
        }


        //-------------------------------------------------------------------------------------


        //menuRecyclerview
        menurecyclerView = findViewById(R.id.recyclerView_menuList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        menurecyclerView.setLayoutManager(gridLayoutManager);


        //가게 한줄소개, 운영시간, 공지사항
        store_intro = (TextView) findViewById(R.id.store_intro);
        store_openTime= (TextView) findViewById(R.id.store_opentime);
        store_notice = (TextView) findViewById(R.id.store_notice);


        Intent intent = getIntent();
        if(intent.hasExtra("_id")){
            String storeId = intent.getStringExtra("_id");
            String menuId = intent.getStringExtra("m_id");
            showStore(storeId, menuId);
        }else if(intent.hasExtra("intent_SId")){
            String intentS_Id = intent.getStringExtra("intent_SId");
            String intentM_Id = intent.getStringExtra("intent_mId");
            showStore(intentS_Id, intentM_Id);
        }else if(intent.hasExtra("intent_recentSId")){
            String intent_recentSId = intent.getStringExtra("intent_recentSId");
            String intent_cartMId = intent.getStringExtra("intent_cartMId");
            showStore(intent_recentSId, intent_cartMId);

        }else {
            Log.e("MenuActivity", "No Intent data found.");
        }

    }
    public void showMenu(String m_id, String tabs_id, String s_name, int s_num){
        //menuList 세팅
        ApiInterface apiInterface1 = ApiClient.getClient().create(ApiInterface.class);
        Log.d("MenuActivity", "showMenu_M: " + m_id);
        Log.d("MenuActivity", "showMenu_S: " + tabs_id);

        Call<MenuItem> callMenu = apiInterface1.getMenusData(m_id);
        callMenu.enqueue(new Callback<MenuItem>() {
            @Override
            public void onResponse(Call<MenuItem> call, Response<MenuItem> response) {
                if(response.isSuccessful() && response.body() != null){
                    //menuResponse = response.body().response;
                    menuInfo = response.body().response.getMenus();
                    menuAdapter = new MenuAdapter(getApplicationContext(), menuInfo, m_id, tabs_id, s_name, s_num);
                    menurecyclerView.setAdapter(menuAdapter);
                }else{
                    //menuInfo = response.body().response.getMenus();
                    menuAdapter = new MenuAdapter(getApplicationContext(), menuInfo, m_id, tabs_id, s_name, s_num);
                    menurecyclerView.setAdapter(menuAdapter);
                }
            }

            @Override
            public void onFailure(Call<MenuItem> call, Throwable t) {

            }
        });
    }
    public void showStore(String s_id, String m_id){
        //retrofit2로 데이터 받아오기
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<StoreItem> call = apiInterface.getData();
        call.enqueue(new Callback<StoreItem>() {
            @Override
            public void onResponse(@NonNull Call<StoreItem> call, @NonNull Response<StoreItem> response) {
                if(response.isSuccessful()){
                    storeList=response.body();
                    storeInfo=storeList.response;
                    String store_name = null;
                    Log.d("storeInfo: ", "storeInfo" + storeInfo);

                    //categoryActivity -> MenuActivity
                    for(Stores store: storeInfo){
                        if(store.get_id().equals(s_id)){
                            store_name = store.getName();
                            store_intro.setText(store.getDesc());
                            store_openTime.setText(store.getSchedule());
                            store_notice.setText(store.getNotice());
                            recent_sNum = store.getS_num();
                            break;
                        }
                    }
                    showMenu(m_id, s_id, store_name, recent_sNum);

                    //tabLayout
                    tabLayout = findViewById(R.id.store_tab);
                    for (int i = 0; i < storeInfo.size(); i++) {
                        String tabTitle = storeInfo.get(i).getName();
                        tab_Id = storeInfo.get(i).get_id();
                        TabLayout.Tab tab = tabLayout.newTab().setText(tabTitle);
                        tab.setTag(tab_Id);
                        tabLayout.addTab(tab);
                        //초기 tab 설정 category -> Menu
                        if(tab_Id.equals(s_id)){
                            tabLayout.getTabAt(i).select();
                        }

                        // 가게가 열리지 않았을 때 탭을 비활성화하고 색상을 변경
                        if (!storeInfo.get(i).isOpen()) {
                            tab.view.setClickable(false);
                            tab.view.setEnabled(false);
                            tab.view.setBackgroundColor(Color.LTGRAY);
                        }
                    }
                    //tabLayout 클릭시 동작
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            //GazeTracker
                            if (gazeTrackerDataStorage != null) {
                                gazeTrackerDataStorage.stopGazeTracker("store_menu", recent_sNum, 0);
                                gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
                            }

                            //선택
                            String store_name = null;
                            int position = tab.getPosition();
                            String tabId = (String) tabLayout.getTabAt(position).getTag();
                            for(Stores store: storeInfo){
                                if(store.get_id().equals(tabId)){
                                    store_name = store.getName();
                                    store_intro.setText(store.getDesc());
                                    store_openTime.setText(store.getSchedule());
                                    store_notice.setText(store.getNotice());
                                    tabM_id = store.getM_id();
                                    Log.d("MenuActivity","tabM_id"+tabM_id);
                                    Log.d("MenuActivity","tabId"+tabId);
                                    showMenu(tabM_id, store.get_id(), store_name, recent_sNum);
                                    sNum = store.getS_num();
                                    recent_sNum = sNum;
                                }
                            }


                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            //선택 해제
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            //다시 선택
                        }
                    });

                }
            }
            @Override
            public void onFailure(Call<StoreItem> call, Throwable t) {
                Log.e("STORE", "Error: " + t.getMessage());
            }
        });
    }

    //-------------------------------------------------------------------------------------------

    @Override
    protected void onStop() {
        captureFullScreenshot();
        super.onStop();
        Log.d("StorelistActivity", "onStop");

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("store_menu", recent_sNum, 0);
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

    //sceenshot

    private Bitmap captureScreen() {
        View rootView = getWindow().getDecorView().getRootView();
        rootView.setDrawingCacheEnabled(true);
        return rootView.getDrawingCache();
    }

    public Bitmap captureRecyclerView(RecyclerView recyclerView) {
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();

        if (layoutManager instanceof GridLayoutManager && adapter != null) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();

            int itemCount = adapter.getItemCount();
            int columns = spanCount;

            int width = recyclerView.getWidth();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmapCache = new LruCache<>(cacheSize);

            for (int i = 0; i < itemCount; i++) {
                int column = i % columns;
                int row = i / columns;

                RecyclerView.ViewHolder holder = adapter.createViewHolder(recyclerView, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(
                        View.MeasureSpec.makeMeasureSpec(width / columns, View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(
                        column * (width / columns),
                        row * holder.itemView.getMeasuredHeight(),
                        (column + 1) * (width / columns),
                        (row + 1) * holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {
                    bitmapCache.put(String.valueOf(i), drawingCache);
                }

                // 계산된 높이
                if (column == 0) {
                    height += holder.itemView.getMeasuredHeight();
                }
            }

            Bitmap bigBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            Drawable backgroundDrawable = recyclerView.getBackground();
            if (backgroundDrawable != null) {
                backgroundDrawable.draw(bigCanvas);
            } else {
                bigCanvas.drawColor(Color.WHITE);
            }

            for (int i = 0; i < itemCount; i++) {
                Bitmap bitmap = bitmapCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, (i % columns) * (width / columns), iHeight, paint);

                // 계산된 높이
                if ((i + 1) % columns == 0) {
                    iHeight += bitmap.getHeight();
                }

                bitmap.recycle();
            }
            return bigBitmap;
        }

        return null;
    }

// 전체 화면 캡쳐하기
    private void captureFullScreenshot() {
        // 전체 화면 캡쳐
        Bitmap fullScreenshot = captureScreen();

        // RecyclerView의 모든 아이템을 포함하는 비트맵 캡쳐
        Bitmap recyclerViewScreenshot = captureRecyclerView(menurecyclerView);

        // 캡쳐한 비트맵을 합성
        int combinedWidth = Math.max(fullScreenshot.getWidth(), recyclerViewScreenshot.getWidth());
        int combinedHeight = 667 + recyclerViewScreenshot.getHeight();
        Log.d("screenshot", "recyclerViewScreenshot_height: " + recyclerViewScreenshot.getHeight());

        Bitmap combinedBitmap = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);
        canvas.drawBitmap(fullScreenshot, 0, 0, null);

        // recyclerViewScreenshot을 그릴 위치 계산
        int recyclerViewLeft = 26;
        int recyclerViewTop = 667;

        // 배경을 하얀색으로 그리기
        canvas.drawColor(Color.WHITE);

        // 이미지 그리기
        canvas.drawBitmap(fullScreenshot, 0, 0, null);
        canvas.drawBitmap(recyclerViewScreenshot, recyclerViewLeft, recyclerViewTop, null);

        // 필요에 따라 비트맵을 저장하거나 처리할 수 있습니다.
        saveImage(combinedBitmap);

        // 메모리 해제
        fullScreenshot.recycle();
        recyclerViewScreenshot.recycle();
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