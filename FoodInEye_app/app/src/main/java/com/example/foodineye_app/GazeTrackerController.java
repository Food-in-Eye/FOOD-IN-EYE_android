package com.example.foodineye_app;

import android.content.Context;

public class GazeTrackerController {

    private GazeTrackerManager gazeTracker;

    public void startTraking(Context context){
        gazeTracker = GazeTrackerManager.makeNewInstance(context);
        gazeTracker.startGazeTracking();
    }
}
