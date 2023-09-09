package com.example.foodineye_app.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodineye_app.GazeTrackerManager;
import com.example.foodineye_app.R;

import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.constant.AccuracyCriteria;
import camp.visual.gazetracker.constant.CalibrationModeType;
import camp.visual.gazetracker.constant.UserStatusOption;
import camp.visual.gazetracker.util.ViewLayoutChecker;
import visual.camp.sample.view.CalibrationViewer;
import visual.camp.sample.view.PointView;

public class Calibration extends AppCompatActivity {

    //-----------------------------------------------------------------------------------------
    //gazetracker
    PointView viewPoint;
    GazeTrackerManager gazeTracker;

    CalibrationViewer viewCalibration;
    Handler backgroundHandler;
    HandlerThread backgroundThread = new HandlerThread("background");
    Context context;
    //-----------------------------------------------------------------------------------------
    //calibration
    LinearLayout calibrationBtn;
    LinearLayout calibration;
    ViewLayoutChecker viewLayoutChecker = new ViewLayoutChecker();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration);

        context = getApplicationContext();
        //-----------------------------------------------------------------------------------------
        //calibration

        gazeTracker = GazeTrackerManager.makeNewInstance(this);

        calibrationBtn = (LinearLayout) findViewById(R.id.calibrationBtn);
        calibration = (LinearLayout) findViewById(R.id.calibration);
        initTrackerView();
        initHandler();

        final Data data = (Data) getApplication();
        data.setViewCalibration(viewCalibration);

        // gazeTracker 객체 초기화 후 콜백 등록
        gazeTracker.setGazeTrackerCallbacks(calibrationCallback);
        runGazeTracker();

        setOffsetOfView();

        calibrationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show("start-calibration");
                runOnUiThread(()->calibration.setVisibility(View.INVISIBLE));
                // 타이틀바 숨기기
                hideNavigationBar();
                gazeTracker.startCalibration(CalibrationModeType.DEFAULT, AccuracyCriteria.DEFAULT);
                Log.d("Calibration", "startCalibration: "+ gazeTracker.startCalibration(CalibrationModeType.DEFAULT, AccuracyCriteria.DEFAULT));
//                gazeTracker.startCalibration(CalibrationModeType.DEFAULT, AccuracyCriteria.DEFAULT, 20.000001, 100.00001, 1050.01, 2000.01);
            }
        });

        LinearLayout orderBtn = (LinearLayout) findViewById(R.id.real_home_order);
        orderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //home -> storelist
                Intent intent = new Intent(getApplicationContext(), StorelistActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }
        });

        //-----------------------------------------------------------------------------------------

    }

    private void runGazeTracker() {
        Log.d("Calibration", "startGazeTracking");
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

    private void initTrackerView() {
        viewPoint = findViewById(R.id.view_point);
        viewCalibration = findViewById(R.id.view_calibration);
    }

    private void initHandler() {
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());
    }

    private void initGazeTracker() {
        UserStatusOption userStatusOption = new UserStatusOption();
        gazeTracker.initGazeTracker((tracker, error) -> {
            if (tracker == null) {
                Log.d("Calibration", "Gaze Tracker Is NULL NULL NULL");
            } else {
                Log.d("Calibration", "Gaze Tracker INIT SUCCESS");
                show("Gaze Tracker INIT SUCCESS");
            }
        }, userStatusOption);
    }

    @Override
    protected void onStart() {
        super.onStart();
        gazeTracker.setGazeTrackerCallbacks(calibrationCallback);
    }

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
            runOnUiThread(() -> calibration.setVisibility(View.VISIBLE));
            showNavigationBar();
            gazeTracker.setCalibrationData(calibrationData);
            show("calibrationFinished");
        }
    };

    private boolean startCollectSamples() {
        boolean isSuccess = gazeTracker.startCollectingCalibrationSamples();
        return isSuccess;
    }

    private void hideNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }

    private void showNavigationBar() {
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_FULLSCREEN;
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
}