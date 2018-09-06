package com.jkingone.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.jkingone.ui.utils.DensityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jiangbo03 at 2018/7/31
 */
public class VolumeView extends View {

    public static final int NUMBER = 4;

    private int mRectWidth; // 音量柱宽
    private int mRectHeight; // 音量柱高
    private int mInterval = 6;

    private Paint mPaint;

    private List<Float> mPointers = new ArrayList<>(NUMBER);

    private float rate;

    private ObjectAnimator mObjectAnimator;

    public VolumeView(Context context) {
        super(context);
        init();
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VolumeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void setRate(float rate) {
        this.rate = rate;
        for (int j = 0; j < NUMBER; j++) {
            float res = (float) Math.abs(Math.sin(rate + j));
            mPointers.set(j, mRectHeight * res);
        }
        invalidate();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mObjectAnimator = ObjectAnimator.ofFloat(this, "rate", 0, 32);
        mObjectAnimator.setDuration(10000);
        mObjectAnimator.setInterpolator(new LinearInterpolator());
        mObjectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ValueAnimator.REVERSE);

        mRectWidth = DensityUtils.dp2px(getContext(), 5);

        for (int i = 0; i < NUMBER; i++) {
            mPointers.add(0f);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mRectHeight = getMeasuredHeight();
        mPointers.clear();
        for (int i = 0; i < NUMBER; i++) {
            mPointers.add((float) (Math.random() * mRectHeight));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i < NUMBER; i++) {
            int dif = mInterval * i;
            float left = (float) (mRectWidth * i + dif);
            float right = (float) (mRectWidth * (i + 1) + dif);
            canvas.drawRect(left, mRectHeight - mPointers.get(i), right, mRectHeight, mPaint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mObjectAnimator != null) {
            mObjectAnimator.end();
        }
    }

    public void start() {
        if (mObjectAnimator != null) {
            mObjectAnimator.start();
        }
    }

    public void stop() {
        if (mObjectAnimator != null) {
            mObjectAnimator.end();
        }
    }
}
