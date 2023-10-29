package com.example.foodineye_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.foodineye_app.activity.Cart;
import com.example.foodineye_app.activity.CustomLoading;
import com.example.foodineye_app.activity.Data;
import com.example.foodineye_app.activity.RouletteActivity;
import com.example.foodineye_app.activity.StorelistActivity;
import com.example.foodineye_app.data.MetaInfoData;
import com.example.foodineye_app.gaze.PostGaze;
import com.example.foodineye_app.gaze.RouletteData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.callback.UserStatusCallback;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.constant.UserStatusOption;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.ScreenState;
import camp.visual.gazetracker.state.TrackingState;
import camp.visual.gazetracker.util.ViewLayoutChecker;
import visual.camp.sample.view.CalibrationViewer;
import visual.camp.sample.view.PointView;

public class GazeTrackerDataStorage {
    private Context context;

    public GazeTrackerDataStorage(Context context) {
        this.context = context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    private static final String TAG = StorelistActivity.class.getSimpleName();

    private String userId;
    private WebView webView;
    private ConstraintLayout constraintLayout;

    private PointView viewPoint;
    private CalibrationViewer viewCalibration;
    private Handler backgroundHandler;
    private GazeTrackerManager gazeTracker;
    private GestureDetector gestureDetector;

    private final HandlerThread backgroundThread = new HandlerThread("background");
    private final OneEuroFilterManager oneEuroFilter = new OneEuroFilterManager(2);

    private ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();

    // for file save --------------------------------------------------------------------------

    private ArrayList<GazeInfo> list_gazeInfo = new ArrayList<GazeInfo>(); // list for gazeInfo
    private PostGaze postGaze; //layout 하나
    private PostGaze.Gaze gaze;
    private ArrayList<PostGaze.Gaze> gazeArrayList = new ArrayList<>();


    //scroll change
    private int scroll;
    private ArrayList list_scroll = new ArrayList();

    //-----------------------------------------------------------------------------------------
    //Handler!!!!!!!!!!
    private boolean gazeDataCapturing = true; // 클릭 시 데이터 수집 여부

    //-----------------------------------------------------------------------------------------

    String layout_name;
    int store_num;
    int food_num;

    int[][] recentGazeCountList;
    private  RouletteData[] recentTop5List = new RouletteData[5]; // Top5를 저장할 배열
    int totalCount = 0;
    int recentCount = 0;
    List<MetaInfoData> metaInfoDataList; //s_num과 f_num의 전체 배열
    List<Cart> cartList;


    LottieAnimationView lottieAnimationView;
    LinearLayout rouletteView;

    public void setGazeTracker(Context context, ConstraintLayout constraintLayout, PointView viewPoint,String layout_name, int s_num, int f_num){

        GazeTrackerManager gazeTrackerManager = new GazeTrackerManager(context);
        setGazeTracker(gazeTrackerManager);
        setconstraintLayout(constraintLayout);

        recentGazeCountList = ((Data)context).getGazeCountList();
        recentTop5List = ((Data)context).getTop5List();
        totalCount = ((Data)context).getTotalCount();
        metaInfoDataList = ((Data)context).getMetaInfoDataList();
        cartList = ((Data)context).getCartList();

        setLayout_name(layout_name);
        Log.d("MyApp", "setGazeTracker: "+layout_name);
        setStore_num(s_num);
        Log.d("MyApp", "setGazeTracker: "+store_num);
        setFood_num(f_num);

        // top5List 출력

        initSpeedDial();
        setViewPoint(viewPoint);
        final Data data = (Data) context;
        viewCalibration = data.getViewCalibration();
        setOffsetOfView();

        initHandler();
        initTouchHandler();

        modelInfo();

        gazeTracker = GazeTrackerManager.makeNewInstance(context); //gazeTracker 설정
        gazeTracker.loadCalibrationData();
        setCalibration();

        gazeTracker.setGazeTrackerCallbacks(
                gazeCallback, calibrationCallback, statusCallback, userStatusCallback);

        runGazeTracker();

    }

    public void stopGazeTracker(Context context, String layout_name, int store_num, int food_num){
        Log.d("GazeTrackerDataStorage", "change layout, stopGazeTracking ");
        gazeTracker.stopGazeTracking();
        viewPoint.setPosition(0,0);
        gazeInfoToJson(layout_name, store_num, food_num); // 지금까지 기록된 gazeInfo를 jsonObject로 저장 + scroll
        list_gazeInfo = new ArrayList<GazeInfo>(); // list 초기화
        Log.d("GazeTrackerDataStorage", "list_gazeInfo_2: " +list_gazeInfo);
        list_gazeInfo = new ArrayList<>(); //list 초기화

        gazeTracker.removeCallbacks(
                gazeCallback, calibrationCallback, statusCallback, userStatusCallback);

        //menu_detail일 때, 어떤 가게 어떤 음식 보는지 -> 해당 페이지에서의 gazeCount/2를 해당하는 배열 값에 넣어주기
        if(layout_name.equals("menu_detail")){
            int halfCount = totalCount/2;
            recentGazeCountList[store_num-1][food_num-1] += halfCount;
            recentCount = recentGazeCountList[store_num-1][food_num-1];
            Log.d("MyApp", "menud_detail_recentCount"+recentCount);

            // 업데이트된 recentCount 값으로 조건 확인
            if ((recentCount % 50) == 0 && recentCount >= 50) {
                findTopList(store_num-1, food_num-1);
            }
        }

        // gazeCountList, top5List, totalCount 설정
        ((Data) context).setGazeCountList(recentGazeCountList);
        // top5List 설정
        ((Data) context).setTop5List(recentTop5List);
        ((Data) context).setTotalCount(totalCount);

        // gazeCountList 출력
        for (int i = 0; i < recentGazeCountList.length; i++) {
            for (int j = 0; j < recentGazeCountList[i].length; j++) {
                Log.d("MyApp", "gazeCountList[" + i + "][" + j + "] = " + recentGazeCountList[i][j]);
            }
        }


    }

    private ProgressDialog progressDialog;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    //no loading
    private void runGazeTracker() {
        CustomLoading loadingDialog = new CustomLoading(context);
        new Thread(() -> {

            initGazeTracker();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            synchronized (list_gazeInfo) {

                gazeTracker.startGazeTracking();
                show("start gaze tracking");
            }
        }).start();
    }

    //loading custom
//    private void runGazeTracker() {
//        CustomLoading loadingDialog = new CustomLoading(context);
//        new Thread(() -> {
//            // 로딩창 표시
//            uiHandler.post(new Runnable() {
//                @Override
//                public void run() {
//                    // UI 작업을 여기에서 수행
//                    //    로딩창을 투명하게
//                    loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//                    loadingDialog.show();
//                }
//            });
//
//            initGazeTracker();
//
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            synchronized (list_gazeInfo) {
//                // 로딩창 닫기
//                uiHandler.post(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if (loadingDialog != null || loadingDialog.isShowing()) {
//                            loadingDialog.dismiss();
//                        }
//                    }
//                });
//                gazeTracker.startGazeTracking();
//                show("start gaze tracking");
//            }
//        }).start();
//    }

    //-----------------------------------------------------------------------------------------

    //setter
    public void setRouletteView(LinearLayout rouletteView) {
        this.rouletteView = rouletteView;
    }

    public void setMetaInfoDataList(List<MetaInfoData> metaInfoDataList) {
        Log.d("MyApp", "metaInfoDataList"+metaInfoDataList.toString());
    }

    public void setLottieAnimationView(LottieAnimationView lottieAnimationView) {
        this.lottieAnimationView = lottieAnimationView;
    }

    public void setRecentGazeCountList(int[][] gazeCountList) {
        this.recentGazeCountList = gazeCountList;
    }


    public void setLayout_name(String layout_name) {
        this.layout_name = layout_name;
    }

    public void setStore_num(int store_num) {
        this.store_num = store_num;
    }

    public void setFood_num(int food_num) {
        this.food_num = food_num;
    }

    public void setGazeTracker(GazeTrackerManager gazeTracker) {
        this.gazeTracker = gazeTracker;
    }

    public void setconstraintLayout(ConstraintLayout constraintLayout) {
        this.constraintLayout = constraintLayout;
    }

    public void setViewPoint(PointView viewPoint) {
        this.viewPoint = viewPoint;
    }

    private void initSpeedDial(){

        Log.d("GazeTrackerDataStorage", "initSpeedDial success!");
        show("start-gaze-tracking");
        releaseGaze();

    }

    public void initCalibrationView(CalibrationViewer calibrationViewer){
        viewCalibration = calibrationViewer;
    }

    private HandlerThread handlerThread;
    private Handler handler;

    private void initHandler() {
        // 이전에 시작된 스레드가 있는 경우 종료
        if (handlerThread != null && handlerThread.isAlive()) {
            handlerThread.quit();
        }

        // 새로운 스레드 인스턴스 생성
        handlerThread = new HandlerThread("HandlerThread");
        handlerThread.start();

        handler = new Handler(handlerThread.getLooper());

        Log.d("GazeTrackerDataStorage", "initHandler success!");

        // 이후에는 핸들러(handler)를 사용하여 작업을 수행
    }


    private void initGazeTracker() {
        UserStatusOption userStatusOption = new UserStatusOption();
        gazeTracker.initGazeTracker((tracker, error) -> {
            if (tracker == null) {
                Log.e(TAG, "Gaze Tracker Is NULL NULL NULL");
            } else {
                Log.e(TAG, "Gaze Tracker INIT SUCCESS");
            }
        }, userStatusOption);
    }

    private void initTouchHandler() {

        Log.d("GazeTrackerDataStorage", "initTouchHandler success!");
        GestureDetector.OnGestureListener handler = new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent motionEvent) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent motionEvent) {
            }

            @Override
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                float x = motionEvent.getX();
                float y = motionEvent.getY();
                onTouchClickEvent(x, y);
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent motionEvent) {

            }

            @Override
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
                return false;
            }
        };
        gestureDetector = new GestureDetector(context, handler);
        constraintLayout.setOnTouchListener((view, event) -> gestureDetector.onTouchEvent(event));
    }


    private void releaseGaze() {
        gazeTracker.deinitGazeTracker();
    }

    public void removeGaze(){
        gazeTracker.removeGazeTracker();
    }

    private void setCalibration() {
        GazeTrackerManager.LoadCalibrationResult result = gazeTracker.loadCalibrationData();
        switch (result) {
            case SUCCESS:
                show("setCalibrationData success");
                break;
            case FAIL_DOING_CALIBRATION:
                show("calibrating");
                break;
            case FAIL_NO_CALIBRATION_DATA:
                show("Calibration data is null");
                break;
            case FAIL_HAS_NO_TRACKER:
                show("No tracker has initialized");
                break;
        }
    }

    //
    // callbacks - gaze
    //

    private final GazeCallback gazeCallback = gazeInfo -> {
        Log.d("GazeTrackerDataStorage", "onStart");
        if (gazeInfo.trackingState != TrackingState.SUCCESS) {
            Log.d("GazeTrackerDataStorage", "FAIL");
            return;
        }
        if (gazeTracker.isCalibrating()) {
            return;
        }

        float[] filtered_gaze = filterGaze(gazeInfo);
        float gx = filtered_gaze[0];
        float gy = filtered_gaze[1];
        Log.d("GazeTrackerDataStorage", "gazeInfo_gx: "+gx);
        Log.d("GazeTrackerDataStorage", "gazeInfo_gy: "+gy);

        //화면에서 gazepoint 보여주기
//        showGazePoint(gx, gy, gazeInfo.screenState);

//        context.runOnUiThread(() -> {
//            onGazeEvent(gx, gy);
//            Log.v("gaze", "x=" + gx + ", y=" + gy + "scroll=" + scroll);
//        });


        //save gazeInfo in arraylist -----
        Log.d("!!!!!!!!!!gazeDataCapturing", "gazeDataCapturing: "+gazeDataCapturing);
        if(gazeDataCapturing){

            //(gx, gy)좌표로 가게, 음식 찾기
            //Log.d("MyApp", "gaze[" + gx + ", " + gy+scroll + "]");

            find(gx, gy + scroll);
            totalCount++;

            list_gazeInfo.add(gazeInfo);
            list_scroll.add(scroll);
        }
    };


    private float[] filterGaze(GazeInfo gazeInfo) {
        if (oneEuroFilter.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
            return oneEuroFilter.getFilteredValues();
        }
        return new float[]{gazeInfo.x, gazeInfo.y};
    }

    private void showGazePoint(final float x, final float y, final ScreenState type) {
        ((Activity)context).runOnUiThread(() -> {
            viewPoint.setType(type == ScreenState.INSIDE_OF_SCREEN ? PointView.TYPE_DEFAULT : PointView.TYPE_OUT_OF_SCREEN);
            viewPoint.setPosition(x, y);
        });
    }

    //
    // callbacks - calibration
    //
    private final CalibrationCallback calibrationCallback = new CalibrationCallback() {
        @Override
        public void onCalibrationProgress(float progress) {
//            runOnUiThread(() -> viewCalibration.setPointAnimationPower(progress));
        }

        @Override
        public void onCalibrationNextPoint(final float x, final float y) {
//            runOnUiThread(() -> {
//                viewCalibration.setVisibility(View.VISIBLE);
//                viewCalibration.changeDraw(true, null);
//                viewCalibration.setPointPosition(x, y);
//                viewCalibration.setPointAnimationPower(0);
//            });

            // Give time to eyes find calibration coordinates, then collect data samples
            backgroundHandler.postDelayed(() -> startCollectSamples(), 1000);
        }

        @Override
        public void onCalibrationFinished(double[] calibrationData) {
            // When calibration is finished, calibration data is stored to SharedPreference
//            runOnUiThread(() -> viewCalibration.setVisibility(View.INVISIBLE));
//            runOnUiThread(() -> webView.setVisibility(View.VISIBLE));
        }
    };

    private boolean startCollectSamples() {
        boolean isSuccess = gazeTracker.startCollectingCalibrationSamples();
        return isSuccess;
    }

    //
    // callbacks - status
    //
    private final StatusCallback statusCallback = new StatusCallback() {
        @Override
        public void onStarted() {
        }

        @Override
        public void onStopped(StatusErrorType error) {
        }
    };

    //
    // callbacks - userStatus
    //
    private final UserStatusCallback userStatusCallback = new UserStatusCallback() {
        @Override
        public void onAttention(long timestampBegin, long timestampEnd, float attentionScore) {
        }

        @Override
        public void onBlink(long timestamp, boolean isBlinkLeft, boolean isBlinkRight, boolean isBlink, float eyeOpenness) {
        }

        @Override
        public void onDrowsiness(long timestamp, boolean isDrowsiness) {
        }
    };

    //--------------------------------------------------------------------

    private void show(String message) {

//        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    //
    // js
    //
    private void executeJs(String cmd) {
        webView.evaluateJavascript(cmd, s -> Log.e(TAG, ">>> " + s));
    }

    private void writeTerm(String msg) {
        webView.evaluateJavascript("writeTerm('" + msg + "')", null);
    }

    private void onGazeEvent(float gx, float gy) {
        int x = pxToDp(gx);
        int y = pxToDp(gy);
        String cmd = "onGazeEvent('" + x + "','" + y + "')";

//        linearLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                linearLayout.evaluateJavascript(cmd, null);
//            }
//        });

//        webView.evaluateJavascript(cmd, null);
    }

    private void onTouchClickEvent(float gx, float gy) {
        int x = pxToDp(gx);
        int y = pxToDp(gy);

        String cmd = "onTouchClick('" + x + "','" + y + "')";

//        linearLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                linearLayout.evaluateJavascript(cmd, null);
//            }
//        });

//        webView.evaluateJavascript(cmd, null);
    }

    private void inspect(float gx, float gy) {
        int x = pxToDp(gx);
        int y = pxToDp(gy);
        webView.evaluateJavascript("inspect('" + x + "','" + y + "')", s -> Log.e(TAG, ">>> " + s));
    }

    private int pxToDp(float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp / density);
    }

    /// for save file ----------------------------------------------------------------------
    private void gazeInfoToJson(String layout_name, int store_num, int food_num) {

        String layoutName = layout_name;
        int s_num = store_num;
        int f_num = food_num;

        Log.d("Gaze", "!!!!!!!!: "+System.identityHashCode(list_gazeInfo));

        for(int j = 0; j<list_gazeInfo.size(); j++){
            long t = list_gazeInfo.get(j).timestamp;
            float x = list_gazeInfo.get(j).x;
            float y = (list_gazeInfo.get(j).y) + scroll;




            gaze = new PostGaze.Gaze(x, y, t);
            gazeArrayList.add(gaze);
        }

        postGaze = new PostGaze(layoutName, s_num, f_num, gazeArrayList);
        Log.d("Gaze", "gaze: "+postGaze);
        Data.addGazeList(postGaze);
    }

    public void stopGazeDataCapturing() {
        gazeDataCapturing = false;
    }

    public void startGazeDataCapturing() {
        gazeDataCapturing = true;
    }


    private void modelInfo(){

        Log.d("GazeTrackerDataStorage", "modelInfo success!");
        String modelName = Build.MODEL;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;
        float screen_xdpi = displayMetrics.xdpi;
        float screen_ydpi = displayMetrics.ydpi;
        float screen_density_dpi = displayMetrics.densityDpi;
        float screen_dpi_ration = displayMetrics.densityDpi / displayMetrics.xdpi;

        Log.i("model", "modelName:" + modelName);
        Log.i("model", "screen_size(h,w):[" + screen_height + "," + screen_width + "]");
        Log.i("model", "screen_dpi(x,y):[" + screen_xdpi + "," + screen_ydpi + "]");
        Log.i("model", "screen_density_dpi:" + screen_density_dpi);
        Log.i("model", "screen_dpi_ration:" + screen_dpi_ration);
    }

    // The gaze or calibration coordinates are delivered only to the absolute coordinates of the entire screen.
    // The coordinate system of the Android view is a relative coordinate system,
    // so the offset of the view to show the coordinates must be obtained and corrected to properly show the information on the screen.
    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(viewPoint, new ViewLayoutChecker.ViewLayoutListener() {
            @Override
            public void getOffset(int x, int y) {
                viewPoint.setOffset(x, y);
//                viewCalibration.setOffset(x, y);
            }
        });
    }

    public void quitBackgroundThread(){
        backgroundThread.quitSafely();
    }

    /// (x, y)으로  s_num, f_num 파악하기-------------------------------------------------------------
    public void find(float x, float y){

        if (layout_name.equals("store_menu")) {
            checkAndPlayAnimation();

            //어떤 가게 어떤 음식 보는지
            findStoreFood(x, y);
        }
    }

    public void findStoreFood(float x, float y){

        int recent_food_num = 0;
        recent_food_num = findFood(x, y) - 1; //실제 f_num-1은 index
        Log.d("MyApp", "findStoreFood" + findFood(x, y));

        if(recent_food_num >= 0){
            int recent_store_num = store_num - 1; //실제 s_num-1은 index
            switch (store_num){
                case 1:
                    recentGazeCountList[0][recent_food_num] += 1;
                    recentCount = recentGazeCountList[0][recent_food_num];
                    Log.d("MyApp", "recentGazeCountList[" + 0 + "][" + recent_food_num + "] = "+recentGazeCountList[0][recent_food_num]);
                    break;
                case 2:
                    recentGazeCountList[1][recent_food_num] += 1;
                    recentCount = recentGazeCountList[1][recent_food_num];
                    Log.d("MyApp", "recentGazeCountList[" + 1 + "][" + recent_food_num + "] = "+recentGazeCountList[1][recent_food_num]);
                    break;
                case 3:
                    recentGazeCountList[2][recent_food_num] += 1;
                    recentCount = recentGazeCountList[2][recent_food_num];
                    Log.d("MyApp", "recentGazeCountList[" + 2 + "][" + recent_food_num + "] = "+recentGazeCountList[2][recent_food_num]);
                    break;
                case 4:
                    recentGazeCountList[3][recent_food_num] += 1;
                    recentCount = recentGazeCountList[3][recent_food_num];
                    Log.d("MyApp", "recentGazeCountList[" + 3 + "][" + recent_food_num + "] = "+recentGazeCountList[3][recent_food_num]);
                    break;
                case 5:
                    recentGazeCountList[4][recent_food_num] += 1;
                    recentCount = recentGazeCountList[4][recent_food_num];
                    Log.d("MyApp", "recentGazeCountList[" + 4 + "][" + recent_food_num + "] = "+recentGazeCountList[4][recent_food_num]);
                    break;
                default:
                    break;
            }
            // 업데이트된 recentCount 값으로 조건 확인
            Log.d("MyApp", "store_menu_recentCount"+recentCount);

            if ((recentCount % 50) == 0 && recentCount >= 50) {

                findTopList(recent_store_num, recent_food_num);
            }
        }
    }

    public int findFood(float x, float y){
        Log.d("MyApp", "findFood!!!!!!!!!!!!!!!!!!");
        Log.d("MyApp", "findFood_store_num: "+store_num);

        if (x >= 26 && x <= 369 && y >= 884 && y <= 1409) {
            int f_num = getFNumBySNum(store_num, 0);
            return f_num; //실제 food의 고유한 f_num

        }else if(x >= 369 && x <= 711 && y >= 884 && y <= 1409){
            int f_num = getFNumBySNum(store_num, 1);
            return f_num;

        }else if(x >= 711 && x <= 1054 && y >= 884 && y <= 1409) {
            int f_num = getFNumBySNum(store_num, 2);
            return f_num;

        }else if(x >= 26 && x <= 369 && y >= 1409 && y <= 1934) {
            int f_num = getFNumBySNum(store_num, 3);
            return f_num;

        }else if(x >= 369 && x <= 711 && y >= 1409 && y <= 1934){
            int f_num = getFNumBySNum(store_num, 4);
            return f_num;

        }else if(x >= 711 && x <= 1054 && y >= 1409 && y <= 1934) {
            int f_num = getFNumBySNum(store_num, 5);
            return f_num;

        }else if(x >= 26 && x <= 369 && y >= 1934 && y <= 2459) {
            int f_num = getFNumBySNum(store_num, 6);
            return f_num;

        }else if(x >= 369 && x <= 711 && y >= 1934 && y <= 2459){
            int f_num = getFNumBySNum(store_num, 7);
            return f_num;
        }
        else{
            return 0;
        }
    }



    //Top5에 들어갈 수 있는지 찾기
    public void findTopList(int i, int j){
        Log.d("MyApp", "findTopList");

        boolean inCart;
        inCart = findFoodCart(i+1, j+1);

        //장바구니에 있는지 없는지 확인
        if(!inCart) {
            Log.d("MyApp", "장바구니에 없음" + inCart);
            //장바구니에 없음
            //이미 Top5List에 있는지 확인
            boolean inTopList;
            inTopList = findTop(i+1, j+1);
            if(!inTopList){
                Log.d("MyApp", "Top5List에 없음" + inTopList);
                //Top5List에 없음
                addTop5List(i, j);
            }
        }
    }

    public void addTop5List(int i, int j) {

        Log.d("MyApp", "addTop5List");

        int gazeCount = recentGazeCountList[i][j];

        int minGazeCountIndex = 0; // recentTop5List에서 최소 gazeCount를 가진 객체의 인덱스를 찾음
        int minGazeCount = Integer.MAX_VALUE;

        for (int k = 0; k < 5; k++) {
            if (recentTop5List[k] == null) {

                // 요소가 null이면 아직 채워지지 않았으므로 새 객체를 추가할 수 있음
                recentTop5List[k] = new RouletteData(i+1, j+1, recentGazeCountList[i][j]);
                Log.d("MyApp", "addTop5List!!!!recentTop5List: " + recentTop5List[k]);
                Log.d("MyApp", "addTop5List!!!!recentTop5List: " + Arrays.toString(recentTop5List));
                return;

            } else if (recentGazeCountList[i][j] < minGazeCount) {
                minGazeCount = recentTop5List[k].getRecentGazeCountListValue();
                minGazeCountIndex = k; //최솟값 기억
            }
        }

        if (gazeCount > minGazeCount) {
            // 제공된 값으로 새 RouletteData 객체를 생성
            RouletteData rouletteData = new RouletteData(i+1, j+1, gazeCount);
            // 최소 gazeCount를 가진 객체를 새 RouletteData 객체로 교체
            recentTop5List[minGazeCountIndex] = rouletteData;
        }

        Log.d("MyApp", "addTop5List!!!!recentTop5List: " + Arrays.toString(recentTop5List));
    }

    public boolean findTop(int s_num, int f_num) {

        if (recentTop5List != null) { // recentTop5List가 null이 아닌지 확인
            for (RouletteData rouletteData : recentTop5List) {
                if (rouletteData != null) { // rouletteData가 null이 아닌지 확인
                    if (rouletteData.getS_num() == s_num && rouletteData.getF_num() == f_num) {
                        return true; // 장바구니에 있음
                    }
                }
            }
        }
        return false; // 장바구니에 없음
    }

    //룰렛 생성 트리거
    public void checkAndPlayAnimation() {

        if (totalCount > 300 && recentTop5List != null && recentTop5List.length >= 2) {
            rouletteView.setVisibility(View.VISIBLE);
            lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE); // 무한 반복
            lottieAnimationView.playAnimation();

            //룰렛 클릭했을 때,
            rouletteView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //룰렛페이지로 이동할 때, recentTop5List만 넘겨주면 됨
                    Intent rouletteIntent = new Intent(context, RouletteActivity.class);
                    rouletteIntent.putExtra("top5List", recentTop5List);
                    context.startActivity(rouletteIntent);

                }
            });
        }
    }

    //sNum을 알 때 index의 위치를 알 때 f_num 값 return 하기
    public int getFNumBySNum(int sNum, int index) {
        for (MetaInfoData metaInfoData : metaInfoDataList) {
            if (metaInfoData.getsNum() == sNum) {
                int[] fNumList = metaInfoData.getfNumList();
                if (index >= 0 && index < fNumList.length) {
                    return fNumList[index];
                }
            }
        }
        return -1; // 해당하는 sNum이나 index를 찾을 수 없을 때 예외 처리
    }

    //s_num과 f_num으로 장바구니에 있는 음식 찾기
    public boolean findFoodCart(int s_num, int f_num){

        for(Cart cart : cartList){
            if((cart.getS_num() == s_num) && (cart.getF_num() == f_num)) {
                return true;
            }
        }
        return false;
    }
}