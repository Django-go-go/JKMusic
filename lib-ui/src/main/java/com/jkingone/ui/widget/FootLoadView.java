package com.jkingone.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.TextView;

public class FootLoadView extends FrameLayout {

    private TextView mTextView;
    private FrameLayout.LayoutParams mLpTextView;

    public FootLoadView(@NonNull Context context) {
        this(context, null, 0);
    }

    public FootLoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FootLoadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mTextView = new TextView(context);
        mTextView.setGravity(Gravity.CENTER);
        mLpTextView = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mLpTextView.gravity = Gravity.CENTER;
        mTextView.setLayoutParams(mLpTextView);
        mTextView.setPadding(12, 12, 12, 12);
        mTextView.setVisibility(INVISIBLE);
        addView(mTextView);
    }

    public void postLoading() {
        mTextView.setVisibility(VISIBLE);
        mTextView.setText("正在努力加载...");
    }

    public void postLoadComplete() {
        mTextView.setVisibility(VISIBLE);
        mTextView.setText("加载完成");
    }

    public void postLoadFail() {
        mTextView.setVisibility(VISIBLE);
        mTextView.setText("加载失败！");
    }

    public void postLoadNoData() {
        mTextView.setVisibility(VISIBLE);
        mTextView.setText("已经到底了！");
    }
}
