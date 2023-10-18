package com.example.foodineye_app.gaze;

import java.io.Serializable;

public class RouletteData implements Serializable {
    private int s_num;
    private int f_num;
    private int recentGazeCountListValue; //recenGazeCount[s_num-1][f_num-1]

    public RouletteData(int s_num, int f_num, int recentGazeCountListValue) {
        this.s_num = s_num;
        this.f_num = f_num;
        this.recentGazeCountListValue = recentGazeCountListValue;
    }

    public int getS_num() {
        return s_num;
    }

    public int getF_num() {
        return f_num;
    }

    public int getRecentGazeCountListValue() {
        return recentGazeCountListValue;
    }

    @Override
    public String toString() {
        return "RouletteData{" +
                "s_num=" + s_num +
                ", f_num=" + f_num +
                ", recentGazeCountListValue=" + recentGazeCountListValue +
                '}';
    }
}

