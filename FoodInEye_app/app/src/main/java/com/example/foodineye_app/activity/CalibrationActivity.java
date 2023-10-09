package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodineye_app.GazeTrackerManager;
import com.example.foodineye_app.R;

import camp.visual.gazetracker.GazeTracker;
import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.callback.InitializationCallback;
import camp.visual.gazetracker.callback.StatusCallback;
import camp.visual.gazetracker.constant.AccuracyCriteria;
import camp.visual.gazetracker.constant.CalibrationModeType;
import camp.visual.gazetracker.constant.InitializationErrorType;
import camp.visual.gazetracker.constant.StatusErrorType;
import camp.visual.gazetracker.constant.UserStatusOption;
import camp.visual.gazetracker.util.ViewLayoutChecker;
import visual.camp.sample.view.CalibrationViewer;
import visual.camp.sample.view.PointView;

public class CalibrationActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences;
    String name;
    TextView nameTxt;
    Toolbar toolbar, toolbarfinish;
    ImageView backBtn, homeBtn;
    ImageView backBtnf, homeBtnf;

    //-----------------------------------------------------------------------------------------
    //gazetracker
    PointView viewPoint;

    GazeTrackerManager gazeTrackerManager;
    GazeTracker gazeTracker;

    CalibrationViewer viewCalibration;
    Handler backgroundHandler;
    HandlerThread backgroundThread = new HandlerThread("background");
    Context context;
    //-----------------------------------------------------------------------------------------
    //calibration
    Button calibrationBtn, readyBtn, goStoreBtn;
    LinearLayout calibration, calibrationFinish;
    ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        context = getApplicationContext();

        sharedPreferences = getSharedPreferences("test_token1", MODE_PRIVATE);
        name = sharedPreferences.getString("name", null);

        nameTxt = (TextView) findViewById(R.id.calibration_name);
        nameTxt.setText(name);

        toolbar = (Toolbar) findViewById(R.id.calibration_toolbar);
        backBtn = (ImageView) findViewById(R.id.calibration_back);
        homeBtn = (ImageView) findViewById(R.id.calibration_home);

        setToolBar(toolbar, backBtn, homeBtn);

        //-----------------------------------------------------------------------------------------
        //calibration

        gazeTrackerManager = GazeTrackerManager.makeNewInstance(this);

        calibrationBtn = (Button) findViewById(R.id.calibraionBtn);
        readyBtn = (Button) findViewById(R.id.readycalibraionBtn);

        readyBtn.setVisibility(View.VISIBLE);
        calibrationBtn.setVisibility(View.INVISIBLE);

        calibration = (LinearLayout) findViewById(R.id.calibration);
        calibrationFinish = (LinearLayout) findViewById(R.id.calibration_to_store);
        goStoreBtn = (Button) findViewById(R.id.to_storeBtn);

        final Data data = (Data) getApplication();
        data.setViewCalibration(viewCalibration);

        initTrackerView();
        initHandler();

        runGazeTracker();
        setOffsetOfView();

        calibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(()->calibration.setVisibility(View.INVISIBLE));
                // 타이틀바 숨기기
                hideNavigationBar();
                gazeTrackerManager.startCalibration(CalibrationModeType.DEFAULT, AccuracyCriteria.DEFAULT);
            }
        });

        goStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStorelistActivity();
            }
        });

    }

    //-----------------------------------------------------------------------------------------

//    private void runGazeTracker() {
//        Log.d("CalibrationActivity", "runGazeTracker");
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                initGaze();
//                gazeTrackerManager.startGazeTracking();
//
//                // UI 스레드에서 실행하도록 변경
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        readyBtn.setVisibility(View.INVISIBLE);
//                        calibrationBtn.setVisibility(View.VISIBLE);
//                        calibrationBtn.setEnabled(true);
//                    }
//                });
//            }
//        }).start();
//
//    }

    private void runGazeTracker() {
        Log.d("Calibration", "startGazeTracking");
        new Thread(() -> {
            initGaze();

//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            gazeTrackerManager.startGazeTracking();

            // UI 스레드에서 실행하도록 변경
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    readyBtn.setVisibility(View.INVISIBLE);
                    calibrationBtn.setVisibility(View.VISIBLE);
                    calibrationBtn.setEnabled(true);
                }
            });

        }).start();
    }


    private void initGaze(){

        //initGazeTracker
        Log.d("!CalibrationActivity", "initGaze");
        UserStatusOption userStatusOption = new UserStatusOption();
        gazeTrackerManager.initGazeTracker(initializationCallback, userStatusOption);

    }

    private final InitializationCallback initializationCallback = new InitializationCallback() {
        @Override
        public void onInitialized(GazeTracker gazeTracker, InitializationErrorType error) {
            if (gazeTracker != null) {
                //gazeTrakcer init success
                Log.d("!CalibrationActivity", "CalibrationActivity initSuccess");

                //set StatusCallback
                gazeTrackerManager.setGazeTrackerCallbacks(statusCallback);

                //startTracking
                gazeTrackerManager.stopGazeTracking();

            }
            else {
                //gazeTrakcer init fail -> 재시도
                Log.d("!CalibrationActivity", "CalibrationActivity initFail");

            }
        }
    };

    private StatusCallback statusCallback = new StatusCallback() {
        @Override
        public void onStarted() {
            // gazeTracker.startTracking() Success
            Log.d("!CalibrationActivity", "calibrationCallback");
            gazeTrackerManager.setGazeTrackerCallbacks(calibrationCallback);
        }

        @Override
        public void onStopped(final StatusErrorType error) {
            // gazeTracker.startTracking() Fail
        }
    };

    private void initTrackerView() {
        viewPoint = findViewById(R.id.view_point);
        viewCalibration = findViewById(R.id.view_calibration);
    }

    private void initHandler() {
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    //
    // callbacks - calibration
    //
    private final CalibrationCallback calibrationCallback = new CalibrationCallback() {

        @Override
        public void onCalibrationProgress(float progress) {
            Log.d("!CalibrationActivity", "calibraionProgress");
            runOnUiThread(() -> viewCalibration.setPointAnimationPower(progress));
        }

        @Override
        public void onCalibrationNextPoint(final float x, final float y) {
            runOnUiThread(() -> {
                viewCalibration.setVisibility(View.VISIBLE);
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
            runOnUiThread(() -> viewCalibration.setVisibility(View.INVISIBLE));
            runOnUiThread(() -> calibrationFinish.setVisibility(View.VISIBLE));
            runOnUiThread(() -> calibration.setVisibility(View.INVISIBLE));

            toolbarfinish = findViewById(R.id.calibrationfinish_toolbar);
            backBtnf = (ImageView) findViewById(R.id.calibrationfinish_back);
            homeBtnf = (ImageView) findViewById(R.id.calibrationfinish_home);
            setToolBar(toolbarfinish, backBtnf, homeBtnf);


            gazeTrackerManager.setCalibrationData(calibrationData);
        }
    };

    private boolean startCollectSamples() {
        boolean isSuccess = gazeTrackerManager.startCollectingCalibrationSamples();
        return isSuccess;
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void show(String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    private void setOffsetOfView() {
        viewLayoutChecker.setOverlayView(viewPoint, new ViewLayoutChecker.ViewLayoutListener() {
            @Override
            public void getOffset(int x, int y) {
                viewPoint.setOffset(x, y);
                viewCalibration.setOffset(x, y);
            }
        });
    }

    public void startStorelistActivity(){
        //calibraion -> storelist
        Intent intent = new Intent(getApplicationContext(), StorelistActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    //toolbar
    public void setToolBar(androidx.appcompat.widget.Toolbar toolbar, ImageView backBtn, ImageView homeBtn){

        // 툴바를 액션바로 설정
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle(""); // 툴바의 타이틀을 직접 설정
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 뒤로 가기 버튼 동작을 처리
//                onBackPressed();
                //home -> calibration
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

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

}