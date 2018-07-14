package com.jkingone.customviewlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.ViewUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.jkingone.commonlib.Utils.ScreenUtils;

import java.lang.ref.WeakReference;


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

    private AnimatorSet mAnimatorSet;
    private ObjectAnimator mContainerAnimator;
    private ObjectAnimator mShadowAnimator;

    private boolean isShow = false;

    private int mLocation = Gravity.BOTTOM;

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

        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();
        mMinYVelocity = configuration.getScaledMinimumFlingVelocity();
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

        if (Gravity.BOTTOM == gravity) {
            mContainerHeight = ScreenUtils.getScreenHeight(mActivity) / 3 * 2;
            mContainerLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mContainerHeight, gravity);
            mContainerView.setBackgroundResource(R.drawable.shape_rect_top_white);
        } else if (Gravity.TOP == gravity) {
            mContainerHeight = ScreenUtils.getScreenHeight(mActivity) / 3 * 2;
            mContainerLayoutParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, mContainerHeight, gravity);
            mContainerView.setBackgroundResource(R.drawable.shape_rect_bottom_white);
        } else {
            mContainerHeight = ScreenUtils.getScreenHeight(mActivity) / 3;
            mContainerLayoutParams = new FrameLayout.LayoutParams(mContainerWidth, mContainerHeight, gravity);
            mContainerView.setBackgroundResource(R.drawable.shape_rect_white);
        }

        mLocation = gravity;
        mContentView = view;
    }

    public void show() {

        addToDecorView();

        enterAnimator();
    }

    public void hide() {
        exitAnimator();
    }

    private void enterAnimator() {

        if (mLocation == Gravity.BOTTOM) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY", mContainerHeight, 0);
        } else if (mLocation == Gravity.TOP) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY", -mContainerHeight, 0);
        } else {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "alpha", 0, 1);
        }

        mShadowAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 0, 1);

        initAndStartAnimatorSet(DURATION, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isShow = true;
                isScroll = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                mShadowView.setVisibility(VISIBLE);
                mContainerView.setVisibility(VISIBLE);
                isScroll = true;
            }
        });

    }

    private void exitAnimator() {

        if (mLocation == Gravity.BOTTOM) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY", 0, mContainerHeight);
        } else if (mLocation == Gravity.TOP) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY", 0, -mContainerHeight);
        } else {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "alpha", 1, 0);
        }
        mShadowAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", 1, 0);

        initAndStartAnimatorSet(DURATION, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeFromDecorView();
                isScroll = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isScroll = true;
            }
        });

    }

    private void initAndStartAnimatorSet(int duration, Animator.AnimatorListener animatorListener) {
        if (mContainerAnimator == null || mShadowAnimator == null) {
            Log.i(TAG, "initAnimatorSet: animator is null");
            throw new NullPointerException("animator is null");
        }
        if (mAnimatorSet == null) {
            mAnimatorSet = new AnimatorSet();
            mAnimatorSet.setInterpolator(new DecelerateInterpolator());
        }
        mAnimatorSet.setDuration(duration);
        mAnimatorSet.playTogether(mContainerAnimator, mShadowAnimator);
        mAnimatorSet.removeAllListeners();
        mAnimatorSet.addListener(animatorListener);
        mAnimatorSet.start();
    }

    private void addToDecorView() {

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
    }

    private void removeFromDecorView() {
        mContainerView.setVisibility(INVISIBLE);
        mShadowView.setVisibility(INVISIBLE);
        mDecorView.removeView(JDialog.this);
        isScroll = false;
        mAlpha = 1;
        isShow = false;
    }

    public boolean isShow() {
        return isShow;
    }

    private float mLastY;
    private float mDownY;

    private float mAlpha = 1;

    private int mOriginTop;
    private int mOriginBottom;

    private int mScrollDistance;
    private boolean isScroll = false;

    private static final int SCROLL_DIS = 360;
    private static final int DURATION = 200;


    private int mTouchSlop;

    private VelocityTracker mVelocityTracker;
    private int mMinYVelocity;

    private CheckScroll mCheckScroll;
    public interface CheckScroll {
        boolean canScrollVertically();
    }

    public void setCheckScroll(CheckScroll checkScroll) {
        mCheckScroll = checkScroll;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (mLocation != Gravity.BOTTOM && mLocation != Gravity.TOP) {
            return false;
        }

        if (mCheckScroll != null && mCheckScroll.canScrollVertically()) {
            return false;
        }

        if (isScroll) {
            return false;
        }

        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mOriginTop = mContainerView.getTop();
            mOriginBottom = mContainerView.getBottom();
            mLastY = mDownY = ev.getY();
        }

        if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            float dis = Math.abs(ev.getY() - mDownY);
            boolean canScroll = (mDownY <= ev.getY() && mLocation == Gravity.BOTTOM)
                    || (mDownY >= ev.getY() && mLocation == Gravity.TOP);
            if (canScroll && dis >= mTouchSlop) {
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1000);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastY = mDownY = event.getY();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (mLocation == Gravity.BOTTOM) {
                    mScrollDistance = Math.abs(mContainerView.getTop() - mOriginTop);
                }
                if (mLocation == Gravity.TOP) {
                    mScrollDistance = Math.abs(mContainerView.getBottom() - mOriginBottom);
                }

                float velocity = mVelocityTracker.getYVelocity();

                boolean back = false;

                if (mLocation == Gravity.BOTTOM) {
                    back = velocity > 0 && Math.abs(velocity) > mMinYVelocity * 25;
                }

                if (mLocation == Gravity.TOP) {
                    back = velocity < 0 && Math.abs(velocity) > mMinYVelocity * 25;
                }

                back = back || mScrollDistance > SCROLL_DIS;

                if (back) {
                    scrollToBack();
                } else {
                    scrollToCorrect();
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.clear();
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float curY = event.getY();
                float touchDis = Math.abs(curY - mDownY);

                boolean canScroll = (mDownY <= curY && mLocation == Gravity.BOTTOM)
                        || (mDownY >= curY && mLocation == Gravity.TOP);

                if (canScroll && touchDis >= mTouchSlop) {
                    float dis = curY - mLastY;

                    if (mLocation == Gravity.TOP) {
                        mAlpha += dis / mContainerHeight;
                    }
                    if (mLocation == Gravity.BOTTOM) {
                        mAlpha -= dis / mContainerHeight;
                    }

                    mContainerView.offsetTopAndBottom((int) dis);
                    mShadowView.setAlpha(mAlpha);
                    mLastY = curY;
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void scrollToCorrect() {
        if (mLocation == Gravity.BOTTOM) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY",
                    mContainerView.getTranslationY(), mContainerView.getTranslationY() - mScrollDistance);
        }
        if (mLocation == Gravity.TOP) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY",
                    mContainerView.getTranslationY(), mContainerView.getTranslationY() + mScrollDistance);
        }

        mShadowAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", mAlpha, 1);

        initAndStartAnimatorSet(DURATION * mScrollDistance / mContainerHeight, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isShow = true;
                isScroll = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isScroll = true;
            }
        });
    }

    private void scrollToBack() {
        if (mLocation == Gravity.BOTTOM) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY",
                    mContainerView.getTranslationY(), mContainerView.getTranslationY() + mContainerHeight - mScrollDistance);
        }
        if (mLocation == Gravity.TOP) {
            mContainerAnimator = ObjectAnimator.ofFloat(mContainerView, "translationY",
                    mContainerView.getTranslationY(), mContainerView.getTranslationY() - mContainerHeight + mScrollDistance);
        }

        mShadowAnimator = ObjectAnimator.ofFloat(mShadowView, "alpha", mAlpha, 0);

        initAndStartAnimatorSet(DURATION * (mContainerHeight - mScrollDistance) / mContainerHeight, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeFromDecorView();
                isScroll = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isScroll = true;
            }
        });
    }

//    private boolean isScrollUp = false;
//
//    private static final int DIS = 96;
//    private static final int MSG_START = 1;
//    private static final int MSG_RUN = 2;
//    private static final int MSG_END = 3;
//    private static final int DELAY = 10;
//
//    private static class ScrollHandler extends Handler {
//
//        private WeakReference<JDialog> mDialogWeakReference;
//
//        ScrollHandler(WeakReference<JDialog> dialogWeakReference) {
//            mDialogWeakReference = dialogWeakReference;
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            JDialog jDialog = mDialogWeakReference.get();
//            if (jDialog != null) {
//                switch (msg.what) {
//                    case MSG_START:
//                        jDialog.isScroll = true;
//                        sendEmptyMessage(MSG_RUN);
//                        break;
//                    case MSG_RUN:
//                        if (jDialog.mScrollDistance <= 0) {
//                            sendEmptyMessage(MSG_END);
//                        } else if (jDialog.mScrollDistance - DIS >= 0) {
//                            jDialog.mScrollDistance -= DIS;
//                            jDialog.isScroll = true;
//                            jDialog.mContainerView.offsetTopAndBottom(jDialog.isScrollUp ? -DIS : DIS);
//                            if (jDialog.isScrollUp) {
//                                jDialog.mAlpha += (float) DIS / jDialog.mContainerHeight;
//                            } else {
//                                jDialog.mAlpha -= (float) DIS / jDialog.mContainerHeight;
//                            }
//                            jDialog.mShadowView.setAlpha(jDialog.mAlpha);
//                            sendEmptyMessageDelayed(MSG_RUN, DELAY);
//                        } else {
//                            jDialog.isScroll = true;
//                            jDialog.mContainerView.offsetTopAndBottom(jDialog.isScrollUp ? -jDialog.mScrollDistance : jDialog.mScrollDistance);
//                            jDialog.mScrollDistance = 0;
//                            sendEmptyMessageDelayed(MSG_RUN, DELAY);
//                        }
//                        break;
//                    case MSG_END:
//                        jDialog.isScroll = false;
//                        if (!jDialog.isScrollUp) {
//                            jDialog.restoreContainerView();
//                        }
//                        break;
//                }
//            }
//
//        }
//    }
//
//    private void restoreContainerView() {
//        mContainerView.setVisibility(INVISIBLE);
//        mShadowView.setVisibility(INVISIBLE);
//        mDecorView.removeView(JDialog.this);
//        mAlpha = 1;
//        isShow = false;
//    }
}
