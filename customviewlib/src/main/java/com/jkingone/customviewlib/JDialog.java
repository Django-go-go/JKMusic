package com.jkingone.customviewlib;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.jkingone.commonlib.Utils.ScreenUtils;


public class JDialog extends FrameLayout {

    private static final String TAG = "JDialog";

    private Activity mActivity;

    private ViewGroup mDecorView;

    private View mShadowView;
    private FrameLayout mContainerView;
    private View mContentView;

    private FrameLayout.LayoutParams mContainerLayoutParams;
    private int mContainerWidth;
    private int mContainerHeight;

    private Animation mEnterAnimation;
    private Animation mExitAnimation;

    private Animation mShadowEnterAnimation;
    private Animation mShadowExitAnimation;

    private boolean isShow = false;

    private int mLocation = Gravity.BOTTOM;

    private Scroller mScroller;

    public JDialog(Context context) {
        this(context, null, 0);
    }

    public JDialog(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JDialog(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        if (context instanceof Activity) {
            mActivity = (Activity) context;
        } else {
            Log.i(TAG, "init: Activity is null");
            return;
        }

        mContainerWidth = ScreenUtils.getScreenWidth(context) / 5 * 4;

        mDecorView = mActivity.getWindow().getDecorView().findViewById(android.R.id.content);

        mShadowView = new View(context);
        mShadowView.setBackgroundColor(Color.parseColor("#85403b3b"));
        mShadowView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, Gravity.CENTER));
        mShadowView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    hide();
                    return true;
                }
                return false;
            }
        });

        mContainerView = new FrameLayout(context);
        mContainerView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //do nothing...
            }
        });

        mShadowEnterAnimation = new AlphaAnimation(0, 1);
        mShadowEnterAnimation.setDuration(200);
        mShadowEnterAnimation.setInterpolator(new DecelerateInterpolator());
        mShadowEnterAnimation.setFillAfter(true);

        mShadowExitAnimation = new AlphaAnimation(1, 0);
        mShadowExitAnimation.setDuration(200);
        mShadowExitAnimation.setInterpolator(new DecelerateInterpolator());
        mShadowExitAnimation.setFillAfter(true);

        mScroller = new Scroller(context, new DecelerateInterpolator());

    }

    public void setContentView(int resId) {
        if (resId == NO_ID) {
            Log.i(TAG, "setContentView: resId is NO_ID");
            throw new NullPointerException("no id");
        }
        setContentView(LayoutInflater.from(mActivity).inflate(resId, mContainerView, false));
    }

    public void setContentView(int resId, int gravity) {
        if (resId == NO_ID) {
            Log.i(TAG, "setContentView: resId is NO_ID");
            throw new NullPointerException("no id");
        }

        setContentView(LayoutInflater.from(mActivity).inflate(resId, mContainerView, false), gravity);
    }

    public void setContentView(View view) {
        setContentView(view, mLocation);
    }

    public void setContentView(View view, int gravity) {
        if (view == null) {
            Log.i(TAG, "setContentView: view is null");
            throw new NullPointerException("null");
        }

        if (Gravity.BOTTOM == gravity || Gravity.TOP == gravity) {
            mContainerHeight = ScreenUtils.getScreenHeight(mActivity) / 3 * 2;
            mContainerLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mContainerHeight, gravity);
            mContainerView.setBackgroundResource(R.drawable.shape_rect_half_white);
        } else {
            mContainerHeight = ScreenUtils.getScreenHeight(mActivity) / 3;
            mContainerLayoutParams = new FrameLayout.LayoutParams(mContainerWidth, mContainerHeight, gravity);
            mContainerView.setBackgroundResource(R.drawable.shape_rect_white);
        }

        mLocation = gravity;
        mContentView = view;
    }

    public void show() {

        if (mContentView == null) {
            Log.i(TAG, "show: contentView is null");
            return;
        }

        if (mDecorView.getChildAt(mDecorView.getChildCount() - 1) != this) {
            mDecorView.addView(this);
        }

        if (getChildCount() == 0) {
            addView(mShadowView);
            addView(mContainerView, mContainerLayoutParams);
            mShadowView.setVisibility(INVISIBLE);
            mContainerView.setVisibility(INVISIBLE);
        }
        if (mContainerView.getChildCount() == 0) {
            mContainerView.addView(mContentView, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        }

        post(new Runnable() {
            @Override
            public void run() {
                if (mEnterAnimation == null) {
                    if (Gravity.BOTTOM == mLocation || Gravity.TOP == mLocation) {
                        mEnterAnimation = new TranslateAnimation(0, 0, mContainerView.getHeight(), 0);
                    } else {
                        mEnterAnimation = new AlphaAnimation(0, 1);
                    }

                    mEnterAnimation.setInterpolator(new AccelerateInterpolator());
                    mEnterAnimation.setDuration(200);
                    mEnterAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                            mShadowView.setVisibility(VISIBLE);
                            mContainerView.setVisibility(VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            isShow = true;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                }
                mShadowView.startAnimation(mShadowEnterAnimation);
                mContainerView.startAnimation(mEnterAnimation);
            }
        });

    }

    public void hide() {

        post(new Runnable() {
            @Override
            public void run() {
                if (mExitAnimation == null) {
                    if (Gravity.BOTTOM == mLocation || Gravity.TOP == mLocation) {
                        mExitAnimation = new TranslateAnimation(0, 0, 0, mContainerView.getHeight());
                    } else {
                        mExitAnimation = new AlphaAnimation(1, 0);
                    }
                    mExitAnimation.setInterpolator(new AccelerateInterpolator());
                    mExitAnimation.setDuration(200);
                    mExitAnimation.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            mContainerView.setVisibility(INVISIBLE);
                            mShadowView.setVisibility(INVISIBLE);
                            mDecorView.removeView(JDialog.this);
                            isShow = false;
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });
                }

                mShadowView.startAnimation(mShadowExitAnimation);
                mContainerView.startAnimation(mExitAnimation);
            }
        });

    }

    public boolean isShow() {
        return isShow;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            top = mContainerView.getTop();
            Log.i(TAG, "onInterceptTouchEvent: " + top);
            lastY = ev.getY();
//            downY = ev.getY();
            return false;
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            if (mContainerView.getTop() < top) {
                return false;
            } else {
                return true;
            }
        }

        return true;
    }

    private float lastY = 0;
//    private float downY = 0;

    private int top;
    private int distance;

    private static final int DIS = 50;
    private static final int MSG_START = 1;
    private static final int MSG_RUN = 2;
    private static final int MSG_END = 3;
    private static final int DELAY = 10;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = event.getY();
//                downY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                distance = Math.abs(mContainerView.getTop() - top);
                scrollToTop();
                break;
            case MotionEvent.ACTION_MOVE:
                if (mContainerView.getTop() >= top) {
                    float curY = event.getY();
                    float dif = curY - lastY;
                    mContainerView.offsetTopAndBottom((int) dif);
                    lastY = curY;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void scrollToTop() {
        new ScrollHandler().sendEmptyMessage(MSG_START);
    }

    private class ScrollHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_START:
                    if (distance < DIS) {
                        mContainerView.offsetTopAndBottom(-distance);
                        distance = 0;
                    } else {
                        sendEmptyMessageDelayed(MSG_RUN, DELAY);
                    }
                    break;
                case MSG_RUN:
                    if (distance - DIS > 0) {
                        distance -= DIS;
                        mContainerView.offsetTopAndBottom(-DIS);
                        sendEmptyMessageDelayed(MSG_RUN, DELAY);
                    } else {
                        sendEmptyMessageDelayed(MSG_END, DELAY);
                    }
                    break;
                case MSG_END:
                    mContainerView.offsetTopAndBottom(-distance);
                    distance = 0;
                    break;
            }
        }
    }
}
