package com.jkingone.customviewlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {

    private static final String TAG = "CircleImageView";

    private Paint mPaint;
    private RectF mRectF;
    private Path mPath;

    public CircleImageView(Context context) {
        this(context, null, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStyle(Paint.Style.FILL);
        mRectF = new RectF();
        mPath = new Path();
    }


    @Override
    public void draw(Canvas canvas) {

        mRectF.set(getPaddingLeft(), getPaddingTop(),
                getMeasuredWidth() - getPaddingRight(), getMeasuredHeight() - getPaddingBottom());
        final int saveCount = canvas.save();

        mPath.reset();
        mPath.addRoundRect(mRectF, 12, 12, Path.Direction.CW);
        canvas.clipPath(mPath);
        super.draw(canvas);

        canvas.restoreToCount(saveCount);
    }
}
