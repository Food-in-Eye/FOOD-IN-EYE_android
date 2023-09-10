package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodineye_app.ApiClient;
import com.example.foodineye_app.ApiInterface;
import com.example.foodineye_app.GazeTrackerDataStorage;
import com.example.foodineye_app.R;
import com.example.foodineye_app.data.GetMenu;
import com.example.foodineye_app.data.GetStoreList;
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

    Toolbar toolbar;

    TextView store_intro;
    TextView store_openTime;
    TextView store_notice;

    RecyclerView menurecyclerView;
    MenuAdapter menuAdapter;

    List<GetMenu.Menus> menuInfo = new ArrayList<>(); //store의 메뉴판(음식들)

    GetStoreList storeList; //전체 가게 목록
    List<GetStoreList.Stores> storeInfo = new ArrayList<>();

    TabLayout tabLayout;

    String tab_Id, tabM_id; // 탭에 _ID, m_ID 할당

    int previousTabIndex = -1;
    //-----------------------------------------------------------------------------------------
    Context ctx;
    ConstraintLayout menuLayout;
    PointView viewpoint;
    //gazetracker
    GazeTrackerDataStorage gazeTrackerDataStorage;
    private final HandlerThread backgroundThread = new HandlerThread("background");
    int sNum, fNum;
    int recent_sNum;
    boolean isGazeTrackerRunning = false; // GazeTracker가 실행 중인지 여부를 저장하는 변수

    //-----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        toolbar = findViewById(R.id.menu_toolbar);
        setToolBar(toolbar);

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
        menuLayout = findViewById(R.id.menuLayout);
        viewpoint = findViewById(R.id.view_point_menu);

        setGazeTrackerDataStorage();

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
        ApiClient apiClient = new ApiClient(getApplicationContext());
        apiClient.initializeHttpClient();

        ApiInterface apiInterface1 = apiClient.getClient().create(ApiInterface.class);

        Log.d("MenuActivity", "showMenu_M: " + m_id);
        Log.d("MenuActivity", "showMenu_S: " + tabs_id);

        Call<GetMenu> callMenu = apiInterface1.getMenusData(m_id);
        callMenu.enqueue(new Callback<GetMenu>() {
            @Override
            public void onResponse(Call<GetMenu> call, Response<GetMenu> response) {
                if(response.isSuccessful()){
                    menuInfo = response.body().getMenus();

                    menuAdapter = new MenuAdapter(getApplicationContext(), menuInfo, m_id, tabs_id, s_name, s_num);
                    menurecyclerView.setAdapter(menuAdapter);
                }else{
                    menuInfo = new ArrayList<>(); //빈 목록을 생성하여 초기화

                }
            }

            @Override
            public void onFailure(Call<GetMenu> call, Throwable t) {

            }
        });

    }
    public void showStore(String s_id, String m_id){
        //retrofit2로 데이터 받아오기
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);

        Call<GetStoreList> call = apiInterface.getStore();
        call.enqueue(new Callback<GetStoreList>() {
            @Override
            public void onResponse(@NonNull Call<GetStoreList> call, @NonNull Response<GetStoreList> response) {
                if(response.isSuccessful()){
                    storeList=response.body();
                    storeInfo=storeList.response;
                    String store_name = null;
                    Log.d("storeInfo: ", "storeInfo" + storeInfo);

                    //categoryActivity -> MenuActivity
                    for(GetStoreList.Stores store: storeInfo){
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
                        // 초기 tab 설정 category -> Menu
                        if (tab_Id.equals(s_id)) {
                            tabLayout.getTabAt(i).select();
                        }

                        // 가게가 열리지 않았을 때 탭을 비활성화하고 색상을 변경
                        if (!storeInfo.get(i).isOpen()) {
                            tab.view.setClickable(false);
                            tab.view.setEnabled(false);
                            tab.view.setBackgroundColor(Color.LTGRAY);
                        }

                        // 탭의 너비를 130dp로 설정
                        sizesmallTab(tab);
                    }
                    customTab(tabLayout);

                    //tabLayout 클릭시 동작
                    tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                        @Override
                        public void onTabSelected(TabLayout.Tab tab) {
                            tab.view.setBackgroundResource(R.drawable.tab_background_selector);
                            // 탭의 너비를 200dp로 설정
                            sizeLargeTab(tab);
                            // 중복 호출을 방지하기 위해 선택된 탭에서만 동작하도록 수정
                            int selectedTabIndex = tab.getPosition();
                            if (selectedTabIndex != previousTabIndex) {
                                // 이전 탭에서 GazeTracker 중지
                                stopGazeTracker();

                                // 선택된 탭에서 GazeTracker 시작
                                setGazeTrackerDataStorage();
                                Log.d("gazetrackerrunning", "onTabselected: " + isGazeTrackerRunning);

                                // 최근 선택된 탭의 인덱스 업데이트
                                previousTabIndex = selectedTabIndex;

                                // 나머지 코드 생략...
                                //선택
                                String store_name = null;
                                int position = tab.getPosition();
                                String tabId = (String) tabLayout.getTabAt(position).getTag();
                                for (GetStoreList.Stores store : storeInfo) {
                                    if (store.get_id().equals(tabId)) {
                                        store_name = store.getName();
                                        store_intro.setText(store.getDesc());
                                        store_openTime.setText(store.getSchedule());
                                        store_notice.setText(store.getNotice());
                                        tabM_id = store.getM_id();
                                        Log.d("MenuActivity", "tabM_id" + tabM_id);
                                        Log.d("MenuActivity", "tabId" + tabId);
                                        Log.d("intentToDetail", "intentToDetail_snum: " + store.getS_num());
                                        showMenu(tabM_id, store.get_id(), store_name, store.getS_num());
                                        sNum = store.getS_num();
                                        recent_sNum = sNum;

                                        // 선택된 탭에서 GazeTracker 시작
                                        setGazeTrackerDataStorage();
                                        break;
                                    }
                                }
                            }
                        }

                        @Override
                        public void onTabUnselected(TabLayout.Tab tab) {
                            // 선택 해제
                            isnotSelectTab();
                            sizesmallTab(tab);
                        }

                        @Override
                        public void onTabReselected(TabLayout.Tab tab) {
                            tab.view.setBackgroundResource(R.drawable.tab_background_selector);
                            sizeLargeTab(tab);
                            // 다시 선택
                            // 동일한 탭에서 GazeTracker 재시작
                            int selectedTabIndex = tab.getPosition();
                            setGazeTrackerDataStorage();

                        }
                    });

                }
            }
            @Override
            public void onFailure(Call<GetStoreList> call, Throwable t) {
                Log.e("STORE", "Error: " + t.getMessage());
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
        super.onStop();
        captureFullScreenshot();

        gazeTrackerDataStorage.stopGazeTracker("store_menu", recent_sNum, 0);
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

    private void setGazeTrackerDataStorage(){
        gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, menuLayout, viewpoint);
        }
    }

    private void stopGazeTracker(){
        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.stopGazeTracker("store_menu", recent_sNum, 0);
        }
    }

    //
    // Miscellaneous
    //

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //screenshot
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

    private void captureFullScreenshot() {
        // 전체 화면 캡쳐
        Bitmap fullScreenshot = captureScreen();

        // RecyclerView의 모든 아이템을 포함하는 비트맵 캡쳐
        Bitmap recyclerViewScreenshot = captureRecyclerView(menurecyclerView);

        // 캡쳐한 비트맵을 합성
        int combinedWidth = Math.max(fullScreenshot.getWidth(), recyclerViewScreenshot.getWidth());
        int combinedHeight = 868 + recyclerViewScreenshot.getHeight();
        Log.d("screenshot", "recyclerViewScreenshot_height: " + recyclerViewScreenshot.getHeight());

        Bitmap combinedBitmap = Bitmap.createBitmap(combinedWidth, combinedHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(combinedBitmap);
        canvas.drawBitmap(fullScreenshot, 0, 0, null);

        // recyclerViewScreenshot을 그릴 위치 계산
        int recyclerViewLeft = 26;
        int recyclerViewTop = 817;

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

    //tab custom
    public void customTab(TabLayout tabLayout){

        // 3가지 색상을 순환할 인덱스 변수
        int colorIndex = 0;

        int tabWidth = (int) (100 * getResources().getDisplayMetrics().density); // 고정된 너비 (예: 100dp)
        int tabHeight = (int) (50 * getResources().getDisplayMetrics().density); // 고정된 길이 (예: 60dp)

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            if (tab != null) {
                // 선택되지 않은 탭에 색상을 순환하여 설정
                // 3가지 색상 중 하나를 선택하고 배경색으로 설정
                int[] colors = {Color.parseColor("#4DFF9345"), Color.parseColor("#4D337DB1"), Color.parseColor("#4DD9D9D9")};
                tab.view.setBackgroundResource(R.drawable.tab_background_selector);
                GradientDrawable shapeDrawable = new GradientDrawable();
                shapeDrawable.setColor(colors[colorIndex]);
                shapeDrawable.setCornerRadii(new float[]{30, 30, 30, 30, 0, 0, 0, 0});

                // 고정된 너비와 길이 설정
                ViewGroup.LayoutParams layoutParams = tab.view.getLayoutParams();
                layoutParams.width = tabWidth;
                layoutParams.height = tabHeight;
                tab.view.setLayoutParams(layoutParams);

                tab.view.setBackground(shapeDrawable);

                // 다음 색상 인덱스로 이동 (0, 1, 2, 0, 1, 2, ...)
                colorIndex = (colorIndex + 1) % colors.length;
            }
        }
    }


    public void isnotSelectTab(){
        int colorIndex = 0;

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);

            if (tab != null) {
                // 선택되지 않은 탭에 색상을 순환하여 설정
                // 3가지 색상 중 하나를 선택하고 배경색으로 설정
                int[] colors = {Color.parseColor("#33FF9345"), Color.parseColor("#33337DB1"), Color.parseColor("#33D9D9D9")};
                tab.view.setBackgroundResource(R.drawable.tab_background_selector);
                GradientDrawable shapeDrawable = new GradientDrawable();
                shapeDrawable.setColor(colors[colorIndex]);
                shapeDrawable.setCornerRadii(new float[]{30, 30, 30, 30, 0, 0, 0, 0});
                tab.view.setBackground(shapeDrawable);

                // 다음 색상 인덱스로 이동 (0, 1, 2, 0, 1, 2, ...)
                colorIndex = (colorIndex + 1) % colors.length;
            }
        }
    }

    public void sizeLargeTab(TabLayout.Tab tab){

        // 선택된 탭의 너비를 조정 (예: 200dp)
        ViewGroup.LayoutParams layoutParams = tab.view.getLayoutParams();
        layoutParams.width = (int) (130 * getResources().getDisplayMetrics().density);

        // 선택된 탭의 높이를 조정 (예: 100dp)
//        layoutParams.height = (int) (60 * getResources().getDisplayMetrics().density);

        tab.view.setLayoutParams(layoutParams);

    }

    public void sizesmallTab(TabLayout.Tab tab){

        // 선택된 탭의 너비를 조정 (예: 200dp)
        ViewGroup.LayoutParams layoutParams = tab.view.getLayoutParams();
        layoutParams.width = (int) (100 * getResources().getDisplayMetrics().density);

        // 선택된 탭의 높이를 조정 (예: 50dp)
//        layoutParams.height = (int) (50 * getResources().getDisplayMetrics().density);

        tab.view.setLayoutParams(layoutParams);

    }

    //---------------------------------------------------------------------------------------------------
    //toolbar
    private void setToolBar(androidx.appcompat.widget.Toolbar toolbar){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        ImageView backBtn = (ImageView) findViewById(R.id.menu_back);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
                onBackPressed();
            }
        });

        ImageView homeBtn = (ImageView) findViewById(R.id.menu_home);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_back, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // 메뉴 항목을 클릭했을 때의 동작을 처리합니다.
        switch (item.getItemId()) {
            case R.id.action_back:
                show("뒤로가기 버튼");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //---------------------------------------------------------------------------------------------------

}