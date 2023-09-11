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

import camp.visual.gazetracker.callback.CalibrationCallback;
import camp.visual.gazetracker.constant.AccuracyCriteria;
import camp.visual.gazetracker.constant.CalibrationModeType;
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
    GazeTrackerManager gazeTracker;

    CalibrationViewer viewCalibration;
    Handler backgroundHandler;
    HandlerThread backgroundThread = new HandlerThread("background");
    Context context;
    //-----------------------------------------------------------------------------------------
    //calibration
    Button calibrationBtn, goStoreBtn;
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

        gazeTracker = GazeTrackerManager.makeNewInstance(this);

        calibrationBtn = (Button) findViewById(R.id.calibraionBtn);
        calibration = (LinearLayout) findViewById(R.id.calibration);
        calibrationFinish = (LinearLayout) findViewById(R.id.calibration_to_store);
        goStoreBtn = (Button) findViewById(R.id.to_storeBtn);

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
//                show("calibration");
                runOnUiThread(()->calibration.setVisibility(View.INVISIBLE));
                // 타이틀바 숨기기
                hideNavigationBar();
                gazeTracker.startCalibration(CalibrationModeType.DEFAULT, AccuracyCriteria.DEFAULT);
                Log.d("Calibration", "startCalibration: "+ gazeTracker.startCalibration(CalibrationModeType.DEFAULT, AccuracyCriteria.DEFAULT));
//                gazeTracker.startCalibration(CalibrationModeType.DEFAULT, AccuracyCriteria.DEFAULT, 20.000001, 100.00001, 1050.01, 2000.01);
            }
        });

        goStoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startStorelistActivity();
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

            // UI 스레드에서 실행하도록 변경
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    calibrationBtn.setEnabled(true);
                }
            });

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
//                show("햄버거 생성중 🍞 🍅 🥬 🥓");
//                calibrationBtn.setBackgroundColor(Color.LTGRAY);
                calibrationBtn.setEnabled(false);

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
            runOnUiThread(() -> calibrationFinish.setVisibility(View.VISIBLE));
            runOnUiThread(() -> calibration.setVisibility(View.INVISIBLE));
//            showNavigationBar();

            toolbarfinish = findViewById(R.id.calibrationfinish_toolbar);
            backBtnf = (ImageView) findViewById(R.id.calibrationfinish_back);
            homeBtnf = (ImageView) findViewById(R.id.calibrationfinish_home);
            setToolBar(toolbarfinish, backBtnf, homeBtnf);


            gazeTracker.setCalibrationData(calibrationData);
//            show("calibrationFinished");
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