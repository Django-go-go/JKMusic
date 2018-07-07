package com.jkingone.customviewlib;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;

public class TouchViewPager extends ViewPager {

    public static final String TAG = "TouchViewPager";

    private boolean isClick = false;

    private float downX = 0;

    public TouchViewPager(Context context) {
        super(context);
    }

    public TouchViewPager(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean need = super.onTouchEvent(event);

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            isClick = true;
            if (getParent() != null) {
                View view = (View) getParent();
                view.setPressed(true);
            }
            downX = event.getX();
        }

        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float curX = event.getX();
            float dif = Math.abs(curX - downX);

            isClick = !(dif > 12);
            if (getParent() != null && !isClick) {
                View view = (View) getParent();
                view.setPressed(false);
            }
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (getParent() != null) {
                View view = (View) getParent();
                view.setPressed(false);
            }
            if (isClick) {
                performClick();
            }
            isClick = false;
        }

        return need;
    }
}
