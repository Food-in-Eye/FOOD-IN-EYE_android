package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
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

        }else{
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
//                                    showMenu(tabM_id, tab_Id, store_name);
                                    showMenu(tabM_id, store.get_id(), store_name, recent_sNum);
                                    //menuAdapter.notifyDataSetChanged();
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

}