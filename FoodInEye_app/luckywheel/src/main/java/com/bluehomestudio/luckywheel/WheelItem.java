package com.bluehomestudio.luckywheel;

import android.graphics.Bitmap;

/**
 * Created by mohamed on 22/04/17.
 */

public class WheelItem {

    public int color;
    public Bitmap bitmap;
    public String text;

        public WheelItem(int color, Bitmap bitmap) {
        this.color = color;
        this.bitmap = bitmap;
    }

    public WheelItem(int color, String text) {
        this.color = color;
        this.text = text;
    }

    public void setColor(int parseColor) {
        color = parseColor;
    }

    public void setName(String foodName) {
        text = foodName;
    }
}
