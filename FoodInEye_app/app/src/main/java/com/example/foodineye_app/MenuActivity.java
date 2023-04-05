package com.example.foodineye_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuActivity extends AppCompatActivity {

    TextView store_intro;
    TextView store_openTime;
    TextView store_notice;

    RecyclerView menurecyclerView;
    MenuAdapter menuAdapter;
    
    MenuItem menuItem; // 전체 메뉴판 목록
    Response menuResponse;
    List<Menus> menuInfo = new ArrayList<>();

    StoreItem storeList; //전체 가게 목록
    List<Stores> storeInfo = new ArrayList<>();
    String storeId;

    TabLayout tabLayout;

    String tab_Id, tabM_id; // 탭에 _ID, m_ID 할당
    String tabId;

    String m_Id; //해당 store의 m_ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //menuRecyclerview
        menurecyclerView = findViewById(R.id.recyclerView_menuList);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        menurecyclerView.setLayoutManager(gridLayoutManager);


        //가게 한줄소개, 운영시간, 공지사항
        store_intro = (TextView) findViewById(R.id.store_intro);
        store_openTime= (TextView) findViewById(R.id.store_opentime);
        store_notice = (TextView) findViewById(R.id.store_notice);

        //intent에서 _id값 가져오기
        Intent intent = getIntent();
        storeId = intent.getStringExtra("_id");

        //retrofit2로 데이터 받아오기
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<StoreItem> call = apiInterface.getData();
        call.enqueue(new Callback<StoreItem>() {
            @Override
            public void onResponse(@NonNull Call<StoreItem> call, @NonNull Response<StoreItem> response) {
                if(response.isSuccessful()){
                    storeList=response.body();
                    storeInfo=storeList.response;
                    Log.d("storeInfo: ", "storeInfo" + storeInfo);

                    //categoryActivity -> MenuActivity
                    for(Stores store: storeInfo){
                        if(store.get_id().equals(storeId)){
                            store_intro.setText(store.getDesc());
                            store_openTime.setText(store.getSchedule());
                            store_notice.setText(store.getNotice());
                            m_Id = store.getM_id();
                            break;
                        }
                    }
                    showMenu(m_Id);

                    //tabLayout
                    tabLayout = findViewById(R.id.store_tab);
                    for (int i = 0; i < storeInfo.size(); i++) {
                        String tabTitle = storeInfo.get(i).getName();
                        tab_Id = storeInfo.get(i).get_id();
                        TabLayout.Tab tab = tabLayout.newTab().setText(tabTitle);
                        tab.setTag(tab_Id);
                        tabLayout.addTab(tab);
                        //초기 tab 설정 category -> Menu
                        if(tab_Id.equals(storeId)){
                            tabLayout.getTabAt(i).select();
                        }
                    }
                    //tabLayout 클릭시 동작
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            //선택
                            int position = tab.getPosition();
                            String tabId = (String) tabLayout.getTabAt(position).getTag();
                            for(Stores store: storeInfo){
                                if(store.get_id().equals(tabId)){
                                    store_intro.setText(store.getDesc());
                                    store_openTime.setText(store.getSchedule());
                                    store_notice.setText(store.getNotice());
                                    tabM_id = store.getM_id();
                                    showMenu(tabM_id);
                                    //menuAdapter.notifyDataSetChanged();
                                    break;
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


        /*
        callMenu.enqueue(new Callback<MenuItem>() {
            @Override
            public void onResponse(Call<MenuItem> call, Response<MenuItem> response) {

                menuItem=response.body();
                Log.d("menuItem", "menuItem" + menuItem);
                menuInfo=menuItem.response.getMenus();
                Log.d("MenuActivity", menuInfo.toString());

                menuAdapter = new MenuAdapter(getApplicationContext(), menuInfo);
                menurecyclerView.setAdapter(menuAdapter);

            }

            @Override
            public void onFailure(Call<MenuItem> call, Throwable t) {
                Log.d("MenuActivity", menuInfo.toString());
            }
        });
        */



    }
    public void showMenu(String m_id){
        //menuList 세팅
        ApiInterface apiInterface1 = ApiClient.getClient().create(ApiInterface.class);
        Log.d("StoreMenuId", "showM_id: " + m_id);

        Call<MenuItem> callMenu = apiInterface1.getMenusData(m_id);
        callMenu.enqueue(new Callback<MenuItem>() {
            @Override
            public void onResponse(Call<MenuItem> call, Response<MenuItem> response) {
                if(response.isSuccessful() && response.body() != null){
                    menuInfo = response.body().response.getMenus();
                    menuAdapter = new MenuAdapter(getApplicationContext(), menuInfo);
                    menurecyclerView.setAdapter(menuAdapter);
                }else{
                    menuInfo = response.body().response.getMenus();
                    menuAdapter = new MenuAdapter(getApplicationContext(), menuInfo);
                    menurecyclerView.setAdapter(menuAdapter);
                }
            }

            @Override
            public void onFailure(Call<MenuItem> call, Throwable t) {

            }
        });
    }
}