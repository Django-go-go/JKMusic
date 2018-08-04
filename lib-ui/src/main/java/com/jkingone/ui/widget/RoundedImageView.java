package com.jkingone.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;

import com.jkingone.common.utils.DensityUtils;
import com.jkingone.ui.R;

public class RoundedImageView extends android.support.v7.widget.AppCompatImageView {

    private static final int MODE_CIRCLE = 1;
    private static final int MODE_NONE = 0;
    private static final int MODE_ROUND = 2;

    private Paint mPaint;

    private int mMode;
    private int mRadius;
    private int mRound;

    private RectF mRoundRectF;

    private BitmapShader mBitmapShader;

    public RoundedImageView(Context context) {
        this(context, null, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        mRound = DensityUtils.dp2px(getContext(), 6);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundedImageView, defStyleAttr, 0);
        mMode = a.getInt(R.styleable.RoundedImageView_type, MODE_ROUND);
        mRound = a.getDimensionPixelSize(R.styleable.RoundedImageView_radius, mRound);
        a.recycle();

        mRoundRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (mMode == MODE_CIRCLE) {
            int result = Math.min(getMeasuredHeight(), getMeasuredWidth());
            mRadius = result / 2;
            setMeasuredDimension(result, result);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable mDrawable = getDrawable();
        Matrix mDrawMatrix = getImageMatrix();

        if (mDrawable == null) {
            return;
        }

        if (mDrawable.getIntrinsicWidth() == 0 || mDrawable.getIntrinsicHeight() == 0) {
            return;
        }

        if (mDrawMatrix == null && getPaddingTop() == 0 && getPaddingLeft() == 0) {
            mDrawable.draw(canvas);
        } else {
            final int saveCount = canvas.getSaveCount();
            canvas.save();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                if (getCropToPadding()) {
                    final int scrollX = getScrollX();
                    final int scrollY = getScrollY();
                    canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                            scrollX + getRight() - getLeft() - getPaddingRight(),
                            scrollY + getBottom() - getTop() - getPaddingBottom());
                }
            }

            canvas.translate(getPaddingLeft(), getPaddingTop());

            if (mDrawMatrix != null) {
                canvas.concat(mDrawMatrix);
            }

            if (mBitmapShader == null) {
                Bitmap bitmap = drawableToBitmap(mDrawable);
                mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            }

            mPaint.setShader(mBitmapShader);

            if (mMode == MODE_CIRCLE) {
                canvas.drawCircle(getMeasuredWidth() / 2, getMeasuredHeight() / 2, mRadius, mPaint);
            } else if (mMode == MODE_ROUND) {
                mRoundRectF.set(getPaddingLeft(), getPaddingTop(), getMeasuredWidth() - getPaddingRight(),
                        getMeasuredHeight() - getPaddingBottom());
                canvas.drawRoundRect(mRoundRectF, mRound, mRound, mPaint);
            } else {
                mDrawable.draw(canvas);
            }
            canvas.restoreToCount(saveCount);
        }
    }

    private Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }
}
