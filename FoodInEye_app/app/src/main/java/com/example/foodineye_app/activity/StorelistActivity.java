package com.example.foodineye_app.activity;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.webkit.WebView;
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
import com.example.foodineye_app.PermissionRequester;
import com.example.foodineye_app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.callback.UserStatusCallback;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.constant.UserStatusOption;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.TrackingState;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import visual.camp.sample.view.CalibrationViewer;
import visual.camp.sample.view.PointView;

public class StorelistActivity extends AppCompatActivity {

    StoreItem storeList; //전체 가게 목록
    List<Stores> storeInfo;
    RecyclerView recyclerView;
    StoreAdapter storeAdapter;

    //gazetracker
    GazeTrackerDataStorage gazeTrackerDataStorage;

    //-----------------------------------------------------------------------------------------
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

    private boolean isWebViewRedirected;

    // for file save --------------------------------------------------------------------------
    private String curDateTime;

    private ArrayList<GazeInfo> list_gazeInfo = new ArrayList<GazeInfo>(); // list for gazeInfo
    private JSONObject jsonObject; //json

    //scroll change
    private int scroll;
    private ArrayList list_scroll = new ArrayList();

    // task
    private CountDownTimer timer;
    //-----------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //camera permissionrequester
        PermissionRequester.request(this);
        setContentView(R.layout.activity_storelist);
        ActivityCompat.requestPermissions(this, new String[]
                {Manifest.permission.WRITE_EXTERNAL_STORAGE}, MODE_PRIVATE);

        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
        Context ctx = getApplicationContext();
        ConstraintLayout storeLayout = findViewById(R.id.storelistLayout);
        PointView viewpoint = findViewById(R.id.view_point_storelist);

        GazeTrackerDataStorage gazeTrackerDataStorage = new GazeTrackerDataStorage(this);
        gazeTrackerDataStorage.setContext(this);

        if (gazeTrackerDataStorage != null) {
            gazeTrackerDataStorage.setGazeTracker(ctx, storeLayout, viewpoint);
        }


        //-------------------------------------------------------------------------------------
        //start-gaze-tracking
//        constraintLayout = findViewById(R.id.storelistLayout);
//
//        gazeTracker = GazeTrackerManager.makeNewInstance(this);
//
//        initSpeedDial();
//        initTrackerView();
//        initHandler();
//        initTouchHandler();
//
//        modelInfo();
//
//        runGazeTracker();
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
                storeList=response.body();

                Log.d("StorelistActivity", storeList.toString());
                storeInfo=storeList.response;

                storeAdapter = new StoreAdapter(getApplicationContext(), storeInfo);
                recyclerView.setAdapter(storeAdapter);
            }

            @Override
            public void onFailure(Call<StoreItem> call, Throwable t) {
                Log.d("StorelistActivity", t.toString());
            }
        });

    }

    //-------------------------------------------------------------------------------------------
    private void runGazeTracker() {
        new Thread(() -> {
            initGazeTracker();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            gazeTracker.startGazeTracking();
        }).start();
    }
    private void initSpeedDial(){

        Log.d("StorelistActivity", "initSpeedDial success!");
        show("start-gaze-tracking");
        gazeTracker.startGazeTracking();

    }
    private void initTrackerView() {
        viewPoint = findViewById(R.id.view_point_storelist);
//        viewCalibration = findViewById(R.id.view_calibration);
    }

    private void initHandler() {

        Log.d("StorelistActivity", "initHandler success!");
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

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

        Log.d("StorelistActivity", "initTouchHandler success!");
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
        gestureDetector = new GestureDetector(this, handler);
        constraintLayout.setOnTouchListener((view, event) -> gestureDetector.onTouchEvent(event));
    }


    private void releaseGaze() {
        if (gazeTracker != null) {
            gazeTracker.deinitGazeTracker();
        }
    }

    //
    // callbacks - gaze
    //

    private final GazeCallback gazeCallback = gazeInfo -> {
        Log.d("StorelistActivity", "gazeCallback");
        if (gazeInfo.trackingState != TrackingState.SUCCESS) {
            //화면에서 벗어나면 FAIL
            Log.d("StorelistActivity", "gazeCallback FAIL");
            return;
        }
        if (gazeTracker.isCalibrating()) {
            return;
        }

        float[] filtered_gaze = filterGaze(gazeInfo);
        float gx = filtered_gaze[0];
        float gy = filtered_gaze[1];
        Log.d("StorelistActivity", "gazeInfo_gx: "+gx);
        Log.d("StorelistActivity", "gazeInfo_gy: "+gy);
//        showGazePoint(gx, gy, gazeInfo.screenState);

        runOnUiThread(() -> {
            onGazeEvent(gx, gy);
            Log.v("gaze", "x=" + gx + ", y=" + gy + "scroll=" + scroll);
        });

        //save gazeInfo in arraylist -----
        Log.d("StorelistActivity", "list_gazeInfo: "+list_gazeInfo);
        list_gazeInfo.add(gazeInfo);
        list_scroll.add(scroll);
    };


    private float[] filterGaze(GazeInfo gazeInfo) {
        if (oneEuroFilter.filterValues(gazeInfo.timestamp, gazeInfo.x, gazeInfo.y)) {
            return oneEuroFilter.getFilteredValues();
        }
        return new float[]{gazeInfo.x, gazeInfo.y};
    }

//    private void showGazePoint(final float x, final float y, final ScreenState type) {
//        runOnUiThread(() -> {
//            viewPoint.setType(type == ScreenState.INSIDE_OF_SCREEN ? PointView.TYPE_DEFAULT : PointView.TYPE_OUT_OF_SCREEN);
//            // viewPoint.setPosition(x, y);
//        });
//    }

    //
    // callbacks - calibration
    //
    private final CalibrationCallback calibrationCallback = new CalibrationCallback() {
        @Override
        public void onCalibrationProgress(float progress) {
            runOnUiThread(() -> viewCalibration.setPointAnimationPower(progress));
        }

        @Override
        public void onCalibrationNextPoint(final float x, final float y) {
            runOnUiThread(() -> {
//                viewCalibration.setVisibility(View.VISIBLE);
                viewCalibration.changeDraw(true, null);
                viewCalibration.setPointPosition(x, y);
                viewCalibration.setPointAnimationPower(0);
            });

            // Give time to eyes find calibration coordinates, then collect data samples
            backgroundHandler.postDelayed(() -> startCollectSamples(), 1000);
        }

        @Override
        public void onCalibrationFinished(double[] calibrationData) {
            // When calibration is finished, calibration data is stored to SharedPreference
//            runOnUiThread(() -> viewCalibration.setVisibility(View.INVISIBLE));
//            runOnUiThread(() -> webView.setVisibility(View.VISIBLE));
//            showNavigationBar();
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

//    @Override
//    protected void onStart() {
//        super.onStart();
//        Log.d("StorelistActivity", "onStart");
//        gazeTracker.setGazeTrackerCallbacks(
//                gazeCallback, calibrationCallback, statusCallback, userStatusCallback);
//    }
//
//    @Override
//    protected void onStop() {
//        super.onStop();
//        gazeTracker.removeCallbacks(
//                gazeCallback, calibrationCallback, statusCallback, userStatusCallback);
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        backgroundThread.quitSafely();
//    }

    //
    // Miscellaneous
    //

    private void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    //
    // js
    //
    private void executeJs(String cmd) {
        webView.evaluateJavascript(cmd, s -> Log.e(TAG, ">>> " + s));
    }

    private void setUserId() {
        String cmd = "setUserId('" + userId + "')";
        executeJs(cmd);
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
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp / density);
    }

    /// for save file ----------------------------------------------------------------------
    private void gazeInfoToJson() {   //Gazeinfo arraylist -> json
        jsonObject = new JSONObject();
        try{
            JSONArray jArray = new JSONArray();
            for (int j = 0; j < list_gazeInfo.size(); j++){
                JSONObject sObject = new JSONObject();
                sObject.put("timestamp", list_gazeInfo.get(j).timestamp);
                sObject.put("x", list_gazeInfo.get(j).x);
                sObject.put("y", list_gazeInfo.get(j).y);
                sObject.put("trackingState", list_gazeInfo.get(j).trackingState);
                sObject.put("eyeMovementState", list_gazeInfo.get(j).eyeMovementState);
                sObject.put("screenState", list_gazeInfo.get(j).screenState);
                sObject.put("gazePoint", list_gazeInfo.get(j).gazePoint);
                sObject.put("movementPoint", list_gazeInfo.get(j).movementPoint);

                sObject.put("scroll", list_scroll.get(j));
                jArray.put(sObject);

                if (j >= list_gazeInfo.size()-1){
                    jsonObject.put("gazeinfo", jArray);
                }
            }
//            Log.i("json", jsonObject.toString());
        } catch (JSONException e){
            e.printStackTrace();
            Log.i("json", "jsonobject fail");
        }
    }

    private void setFolderName(){
        Date currentTime = Calendar.getInstance().getTime();
        String[] strList = currentTime.toString().split(" ");
        // strList = [MON, MM, DD, hh:mm:ss, KST, YYYY]

//        curTime = strList[3]; // "hh:mm:ss"
        curDateTime = strList[1]+strList[2] + "_" + strList[3]; // "YYYYMMDD_hh:mm:ss"

    }


//    private void writeFile(String fileTitle) {
//
//        fileTitle = fileTitle + "_" + curDateTime + ".json";
//
////        File dir = new File(Environment.getExternalStorageDirectory() + userId + File.pathSeparator + curDate);
//        File dir = new File(this.getFilesDir() + "/" + userId);
//        if(!dir.exists()) {
//            dir.mkdirs();
//            Log.i("mkdirs", dir.getAbsolutePath());
//        }
////        File file = new File(this.getFilesDir(), fileTitle);
//        File file = new File(dir, fileTitle);
//
//        try {
//            //파일 생성
//            if (!file.exists()) {
//                file.createNewFile();
//                Log.i("File", "create file");
//            }
//
//            BufferedWriter bw = new BufferedWriter(new FileWriter(file,true));
//            bw.write(jsonObject.toString());
//
//            bw.newLine();
//            bw.close();
//            show("save success");
//        } catch (IOException e) {
//            Log.i("저장오류", e.getMessage());
//            show("save fail");
//        }
//    }

    private void saveGazeInfo(String task) {
//        setFolderName(); // curDate, curTime 초기화
        gazeInfoToJson(); // 지금까지 기록된 gazeInfo를 jsonObject로 저장 ++ scroll
//        writeFile(task); // jsonObject 내보내기
        list_gazeInfo = new ArrayList<GazeInfo>(); // list 초기화

    }

    private void task1min() {
        list_gazeInfo = new ArrayList<GazeInfo>(); // list 초기화
        list_scroll = new ArrayList();
        setTimerSec(30); //60sec
        show("Start");
        timer.start();

    }

    private void setTimerSec(int totalSec) {
        int totalMillis = totalSec*1000;
        timer = new CountDownTimer(totalMillis,1000){
            @Override
            public void onTick(long l) {
                Log.i("task", "timer is running");
            }

            @Override
            public void onFinish() {
                Log.i("task", "timer is over");
                saveGazeInfo("task"+totalSec);
                Log.i("task", "saveGazeInfo");
            }
        };
    }

    private void modelInfo(){

        Log.d("StorelistActivity", "modelInfo success!");
        String modelName = Build.MODEL;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int screen_height = displayMetrics.heightPixels;
        int screen_width = displayMetrics.widthPixels;
        float screen_xdpi = displayMetrics.xdpi;
        float screen_ydpi = displayMetrics.ydpi;
        float screen_density_dpi = displayMetrics.densityDpi;
        float screen_dpi_ration = displayMetrics.densityDpi / displayMetrics.xdpi;
//        int contentHeight = linearLayout.getContentHeight();

        Log.i("model", "modelName:" + modelName);
        Log.i("model", "screen_size(h,w):[" + screen_height + "," + screen_width + "]");
        Log.i("model", "screen_dpi(x,y):[" + screen_xdpi + "," + screen_ydpi + "]");
        Log.i("model", "screen_density_dpi:" + screen_density_dpi);
        Log.i("model", "screen_dpi_ration:" + screen_dpi_ration);
    }
}