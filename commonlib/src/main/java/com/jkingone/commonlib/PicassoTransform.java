package com.jkingone.commonlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.renderscript.RSRuntimeException;

import com.jkingone.commonlib.Utils.ImageUtils;
import com.squareup.picasso.Transformation;

/**
 * Created by Administrator on 2017/8/17.
 */

public class PicassoTransform implements Transformation {

    public static int MAX_RADIUS = 25;
    public static int DEFAULT_DOWN_SAMPLING = 1;

    private Context mContext;

    private int mRadius;
    private int mSampling;

    public PicassoTransform(Context context) {
        this(context, MAX_RADIUS, DEFAULT_DOWN_SAMPLING);
    }

    public PicassoTransform(Context context, int radius, int sampling) {
        mContext = context.getApplicationContext();
        mRadius = radius;
        mSampling = sampling;
    }

    @Override public Bitmap transform(Bitmap source) {

        if (source == null){
            source = BitmapFactory.decodeResource(mContext.getResources(), R.mipmap.ic_launcher_round);
        }
        int scaledWidth = source.getWidth() / mSampling;
        int scaledHeight = source.getHeight() / mSampling;

        Bitmap bitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.scale(1 / (float) mSampling, 1 / (float) mSampling);
        Paint paint = new Paint();
        paint.setFlags(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(source, 0, 0, paint);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            try {
                bitmap = ImageUtils.blur(mContext, bitmap, mRadius);
            } catch (RSRuntimeException e) {
                bitmap = ImageUtils.blur(bitmap, mRadius, true);
            }
        } else {
            bitmap = ImageUtils.blur(bitmap, mRadius, true);
        }

        source.recycle();

        return bitmap;
    }

    @Override public String key() {
        return "BlurTransformation(radius=" + mRadius + ", sampling=" + mSampling + ")";
    }
}
