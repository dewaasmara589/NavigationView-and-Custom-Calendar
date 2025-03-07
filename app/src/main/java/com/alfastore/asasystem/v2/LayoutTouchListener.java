package com.alfastore.asasystem.v2;

import android.app.Activity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

public class LayoutTouchListener implements View.OnTouchListener {
    private Activity activity;
    static final int MIN_DISTANCE = 100;// change this runtime based on screen resolution. for 1920x1080 is to small the 100 distance
    private float downX, upX;

    public LayoutTouchListener(Activity activity) {
        this.activity = activity;
    }

    public void onRightToLeftSwipe() {
        Log.d("TAG", "RightToLeftSwipe!");

    }

    public void onLeftToRightSwipe() {
        Log.d("TAG", "LeftToRightSwipe!");
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                downX = event.getX();
                return true;
            }
            case MotionEvent.ACTION_UP: {
                upX = event.getX();

                float deltaX = downX - upX;

                if (Math.abs(deltaX) > MIN_DISTANCE) {
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe();
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe();
                        return true;
                    }
                } else {
                    Log.i("TAG", "Swipe was only " + Math.abs(deltaX) + " long horizontally, need at least " + MIN_DISTANCE);
                }

                return false;
            }
        }
        return false;
    }
}
