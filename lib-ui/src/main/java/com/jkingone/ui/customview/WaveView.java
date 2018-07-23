package com.jkingone.ui.customview;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;

import com.jkingone.common.Utils.ScreenUtils;

/**
 * Created by Administrator on 2018/6/24.
 */

public class WaveView extends LinearLayout {

    private int radius;
    private int rx = 0;
    private int ry = 0;

    ObjectAnimator animator;
    ObjectAnimator animatorReverse;

    private Paint mPaint;

    private WaveEndListener mWaveEndListener;

    public interface WaveEndListener {
        void onWaveEnd(Animator animation, boolean isReverse);
    }

    public void setRadius(int radius) {
        this.radius= radius;
        invalidate();
    }

    public void setCircleCenter(int rx, int ry) {
        this.rx = rx;
        this.ry = ry;
    }

    public void start() {
        animator.start();
    }

    public void startReverse() {
        animatorReverse.start();
    }

    public void stop() {
        animator.end();
        animatorReverse.end();
    }

    public void setEndListener(WaveEndListener listener) {
        mWaveEndListener = listener;
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mWaveEndListener != null) {
                    mWaveEndListener.onWaveEnd(animation, false);
                }
            }
        });

        animatorReverse.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mWaveEndListener != null) {
                    mWaveEndListener.onWaveEnd(animation, true);
                }
            }
        });
    }

    public WaveView(Context context) {
        this(context, null, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.BLACK);
        int h = ScreenUtils.getScreenHeight(context);
        int w = ScreenUtils.getScreenWidth(context);
        animator = ObjectAnimator.ofInt(this, "radius", 48, (int)Math.hypot(w, h));
        animator.setDuration(500);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setRepeatCount(0);

        animatorReverse = ObjectAnimator.ofInt(this, "radius", (int)Math.hypot(w, h), 48);
        animatorReverse.setDuration(500);
        animatorReverse.setInterpolator(new AccelerateDecelerateInterpolator());
        animatorReverse.setRepeatCount(0);
    }

    private Path mPath = new Path();

    @Override
    public void draw(Canvas canvas) {
        int saveCount = canvas.save();

        mPath.reset();
        mPath.addCircle(rx, ry, radius, Path.Direction.CW);
        canvas.clipPath(mPath);

        super.draw(canvas);

        canvas.restoreToCount(saveCount);
    }


}
