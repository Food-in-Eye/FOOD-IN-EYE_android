package com.example.foodineye_app;

import android.content.Context;
import android.os.Build;
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

import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.foodineye_app.activity.Data;
import com.example.foodineye_app.activity.StorelistActivity;
import com.example.foodineye_app.gaze.PostGaze;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.GazeCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.callback.UserStatusCallback;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.constant.UserStatusOption;
import camp.visual.gazetracker.filter.OneEuroFilterManager;
import camp.visual.gazetracker.gaze.GazeInfo;
import camp.visual.gazetracker.state.TrackingState;
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

    private boolean isWebViewRedirected;

    // for file save --------------------------------------------------------------------------
    private String curDateTime;

    private ArrayList<GazeInfo> list_gazeInfo = new ArrayList<GazeInfo>(); // list for gazeInfo
    private PostGaze postGaze; //layout 하나
    private PostGaze.Gaze gaze;
    private ArrayList<PostGaze.Gaze> gazeArrayList = new ArrayList<>();


    //scroll change
    private int scroll;
    private ArrayList list_scroll = new ArrayList();

    // task
    private CountDownTimer timer;
    //-----------------------------------------------------------------------------------------

    public void setGazeTracker(Context context, ConstraintLayout constraintLayout, PointView viewPoint){

        GazeTrackerManager gazeTrackerManager = new GazeTrackerManager(context);
        setGazeTracker(gazeTrackerManager);

        setconstraintLayout(constraintLayout);

        initSpeedDial();
        setViewPoint(viewPoint);
        initHandler();
        initTouchHandler();

        modelInfo();

        gazeTracker = GazeTrackerManager.makeNewInstance(context);
        gazeTracker.setGazeTrackerCallbacks(
                gazeCallback, calibrationCallback, statusCallback, userStatusCallback);

        runGazeTracker();

    }

    public void stopGazeTracker(String layout_name, int store_num, int food_num){
        Log.d("GazeTrackerDataStorage", "change layout, stopGazeTracking ");
        gazeTracker.stopGazeTracking();
        viewPoint.setPosition(0,0);
        gazeInfoToJson(layout_name, store_num, food_num); // 지금까지 기록된 gazeInfo를 jsonObject로 저장 ++ scroll
        list_gazeInfo = new ArrayList<GazeInfo>(); // list 초기화
//        saveGazeInfo("none");
        Log.d("GazeTrackerDataStorage", "list_gazeInfo_2: " +list_gazeInfo);
        list_gazeInfo = new ArrayList<>(); //list 초기화
    }


    private void runGazeTracker() {
        new Thread(() -> {
            initGazeTracker();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            synchronized (list_gazeInfo){
                gazeTracker.startGazeTracking();
            }
        }).start();
    }

    //-----------------------------------------------------------------------------------------
//    private void initLinearLayout(LinearLayout parentLayout, LinearLayout childLayout){
//
//        Log.d("GazeTrackerDataStorage", "initLinearLayout success!");
//    }

    private void initSpeedDial(){

        Log.d("GazeTrackerDataStorage", "initSpeedDial success!");
        show("start-gaze-tracking");
        releaseGaze();

    }
//    private void initTrackerView() {
////        viewPoint = findViewById(R.id.view_point);
////        viewCalibration = findViewById(R.id.view_calibration);
//    }

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
//        showGazePoint(gx, gy, gazeInfo.screenState);

//        context.runOnUiThread(() -> {
//            onGazeEvent(gx, gy);
//            Log.v("gaze", "x=" + gx + ", y=" + gy + "scroll=" + scroll);
//        });

        //save gazeInfo in arraylist -----
        Log.d("GazeTrackerDataStorage", "list_gazeInfo"+list_gazeInfo.toString());
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
//        context.runOnUiThread(() -> {
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

    //--------------------------------------------------------------------

    private void show(String message) {

        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
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
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp / density);
    }

    /// for save file ----------------------------------------------------------------------
    private void gazeInfoToJson(String layout_name, int store_num, int food_num) {

        String layoutName = layout_name;
        int s_num = store_num;
        int f_num = food_num;


        for(int j = 0; j<list_gazeInfo.size(); j++){
            long t = list_gazeInfo.get(j).timestamp;
            float x = list_gazeInfo.get(j).x;
            float y = (list_gazeInfo.get(j).y) + scroll;
            gaze = new PostGaze.Gaze(x, y, t);
            gazeArrayList.add(gaze);
        }

        postGaze = new PostGaze(layoutName, s_num, f_num, gazeArrayList);
        Log.d("Gaze", "gaze: "+postGaze);
        Data.setGazeList(postGaze);

//        try {
//            JSONObject gazeObject = new JSONObject();
//            // "page" 필드 추가
//            gazeObject.put("page", layoutName);
//
//            // "s_num" 필드 추가
//            gazeObject.put("s_num", s_num);
//
//            // "f_num" 필드 추가
//            gazeObject.put("f_num", f_num);
//
//            JSONArray gazeArray = new JSONArray();
//
//            for (int j = 0; j < list_gazeInfo.size(); j++) {
//                JSONObject gazeJson = new JSONObject();
//
//                // Gaze 정보의 x 좌표, y 좌표, timestamp 추가
//                gazeJson.put("x", list_gazeInfo.get(j).x);
//                gazeJson.put("y", (list_gazeInfo.get(j).y)+scroll);
//                gazeJson.put("t", list_gazeInfo.get(j).timestamp);
//
//                gazeArray.put(gazeJson);
//            }
//
//            // "gaze" 필드에 Gaze 정보 배열 추가
//            gazeObject.put("gaze", gazeArray);
//            Log.i("json", gazeObject.toString());
//
//            // 한 화면에서의 gazeObject -> 전체 jsonObject에 넣기
//
//            Data.addJsonObject(gazeObject);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.i("json", "gazeObject fail");
//        }


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

//    private void saveGazeInfo(String task) {
////        setFolderName(); // curDate, curTime 초기화
//        gazeInfoToJson(); // 지금까지 기록된 gazeInfo를 jsonObject로 저장 ++ scroll
////        writeFile(task); // jsonObject 내보내기
//        list_gazeInfo = new ArrayList<GazeInfo>(); // list 초기화
//
//    }

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
//                saveGazeInfo("task"+totalSec);
                Log.i("task", "saveGazeInfo");
            }
        };
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

    //setter

    public void setGazeTracker(GazeTrackerManager gazeTracker) {
        this.gazeTracker = gazeTracker;
    }

    public void setconstraintLayout(ConstraintLayout constraintLayout) {
        this.constraintLayout = constraintLayout;
    }

    public void setViewPoint(PointView viewPoint) {
        this.viewPoint = viewPoint;
    }

}