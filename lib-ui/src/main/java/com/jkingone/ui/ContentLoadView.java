package com.jkingone.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jkingone.ui.utils.DensityUtils;


public class ContentLoadView extends FrameLayout {

    private View mContentView;
    private ProgressBar mProgressBar;
    private TextView mTextView;

    private FrameLayout.LayoutParams mLpProgressBar;
    private FrameLayout.LayoutParams mLpTextView;
    private FrameLayout.LayoutParams mLpContentView;

    private int mHeightProgressBar;

    public ContentLoadView(@NonNull Context context) {
        this(context, null, 0);
    }

    public ContentLoadView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ContentLoadView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init(context);
    }

    private void init(Context context) {
        mHeightProgressBar = DensityUtils.dp2px(context, 48);

        initProgressbar(context);

        initTextView(context);

        initContentView(context);

        addView(mProgressBar);

        addView(mTextView);

        if (mContentView != null) {
            addView(mContentView);
        }
    }


    private void initProgressbar(Context context) {
        mProgressBar = new ProgressBar(context);
        mLpProgressBar = new FrameLayout.LayoutParams(mHeightProgressBar, mHeightProgressBar);
        mLpProgressBar.gravity = Gravity.CENTER;
        mProgressBar.setLayoutParams(mLpProgressBar);
    }

    private void initTextView(Context context) {
        mTextView = new TextView(context);
        mLpTextView = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mLpTextView.gravity = Gravity.CENTER;
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setLayoutParams(mLpTextView);
        mTextView.setVisibility(INVISIBLE);
        mTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLoadRetryListener != null) {
                    postLoading();
                    mLoadRetryListener.onRetry();
                }
            }
        });
    }

    private void initContentView(Context context) {

        if (getChildCount() == 3 && (mContentView = getChildAt(2)) != null) {
            mLpContentView = (LayoutParams) mContentView.getLayoutParams();
            if (mLpContentView == null) {
                mLpContentView = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                mLpContentView.gravity = Gravity.CENTER;
                mContentView.setLayoutParams(mLpContentView);
            }
            mContentView.setVisibility(INVISIBLE);
        }
    }

    public void postLoading() {

        post(new Runnable() {
            @Override
            public void run() {
                if (mContentView != null) {
                    mContentView.setVisibility(INVISIBLE);
                }

                mTextView.setVisibility(INVISIBLE);
                mProgressBar.setVisibility(GONE);
            }
        });
    }

    public void postLoadFail() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mContentView != null) {
                    mContentView.setVisibility(INVISIBLE);
                }

                mTextView.setVisibility(VISIBLE);
                mTextView.setText("加载失败！");
                mProgressBar.setVisibility(GONE);
            }
        });
    }

    public void postLoadNoData() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mContentView != null) {
                    mContentView.setVisibility(INVISIBLE);
                }

                mTextView.setVisibility(VISIBLE);
                mTextView.setText("抱歉，没有数据！");
                mProgressBar.setVisibility(GONE);
            }
        });
    }

    public void postLoadComplete() {
        post(new Runnable() {
            @Override
            public void run() {
                if (mContentView != null) {
                    mContentView.setVisibility(VISIBLE);
                }

                mTextView.setVisibility(INVISIBLE);
                mProgressBar.setVisibility(GONE);
            }
        });
    }

    private LoadRetryListener mLoadRetryListener;

    public void setLoadRetryListener(LoadRetryListener loadRetryListener) {
        mLoadRetryListener = loadRetryListener;
    }

    public interface LoadRetryListener {
        void onRetry();
    }
}
