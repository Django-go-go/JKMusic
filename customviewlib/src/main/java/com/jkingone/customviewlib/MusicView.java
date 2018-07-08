package com.jkingone.customviewlib;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jkingone.commonlib.Utils.ScreenUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

/**
 * Created by Administrator on 2017/8/16.
 */

public class MusicView extends FrameLayout {

    private static final String TAG = "MusicView";

    private Paint paint = new Paint();
    private int radius = 400;
    private int strokeWidth = 120;
    private Shader shader;
    private ImageView mImageView;
    private FrameLayout.LayoutParams mLayoutParams;

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setStrokeWidth(int strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public MusicView(Context context) {
        this(context, null);
    }

    public MusicView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MusicView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mLayoutParams = new FrameLayout.LayoutParams(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        int width = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();

        radius = Math.min(height, width) / 2;
        double innerRadius = (double)radius * Math.sqrt(2) / 2;
        strokeWidth = radius - (int)innerRadius;
        mLayoutParams.gravity = Gravity.CENTER;
        mLayoutParams.width = (int) innerRadius * 2;
        mLayoutParams.height = (int) innerRadius * 2;
        mImageView.setLayoutParams(mLayoutParams);
        Log.i(TAG, "onMeasure1: " + getMeasuredWidth() + " " + getMeasuredHeight() + " " + radius);
    }

    @Override
    protected void onFinishInflate() {
        mImageView = (ImageView) getChildAt(0);
        Log.i(TAG, "onFinishInflate: ");
        super.onFinishInflate();
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);
        int saveCount = canvas.save();
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;

        paint.reset();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth);
        shader = new LinearGradient(0, 0, getMeasuredWidth() / 4, getMeasuredHeight() / 4,
                Color.parseColor("#757171"), Color.BLACK, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        canvas.drawCircle(centerX, centerY, radius - strokeWidth / 2, paint);

        paint.reset();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strokeWidth / 20 / 4 * 3);
        paint.setColor(Color.BLACK);
        for (int i = 0; i <= strokeWidth; i += (strokeWidth / 20)) {
            canvas.drawCircle(centerX, centerY, radius - i, paint);
        }
        canvas.restoreToCount(saveCount);
        Log.i(TAG, "dispatchDraw: ");
    }
}

