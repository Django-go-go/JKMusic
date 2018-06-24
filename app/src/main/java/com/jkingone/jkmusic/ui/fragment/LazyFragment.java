package com.jkingone.jkmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by Administrator on 2018/6/3.
 */

public class LazyFragment extends Fragment {
    boolean mIsLoadedData = false;

    private static final String ARG_PARAM = "param";
    private static final String ARG_LEN = "len";

    String[] params;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isResumed()) {
            handleOnVisibilityChangedToUser(isVisibleToUser);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            int len = getArguments().getInt(ARG_LEN);
            params = new String[len];
            for (int i = 0; i < len; i++) {
                params[i] = getArguments().getString(ARG_PARAM + i);
            }
        }
    }

    static Bundle setParams(String... params) {
        Bundle args = new Bundle();
        if (params != null) {
            args.putInt(ARG_LEN, params.length);
            for (int i = 0; i < params.length; i++) {
                args.putString(ARG_PARAM + i, params[i]);
            }
        } else {
            args.putInt(ARG_LEN, 0);
        }
        return args;
    }

    /** 处理对用户是否可见 */
    private void handleOnVisibilityChangedToUser(boolean isVisibleToUser) {
        if (isVisibleToUser && isVisible()) {
            // 对用户可见
            if (!mIsLoadedData) {
                mIsLoadedData = true;
                onLazyLoadOnce();
            }
            onVisibleToUser();
        } else {
            // 对用户不可见
            onInvisibleToUser();
        }
    }

    /**  懒加载一次。如果只想在对用户可见时才加载数据，并且只加载一次数据，在子类中重写该方法 */
    protected void onLazyLoadOnce() {
        Log.i(PlaceholderFragment.TAG, "onLazyLoadOnce: " + params[0]);
    }

    /** 对用户可见时触发该方法。如果只想在对用户可见时才加载数据，在子类中重写该方法 */
    protected void onVisibleToUser() {
        Log.i(PlaceholderFragment.TAG, "onVisibleToUser: " + params[0]);
    }

    /** 对用户不可见时触发该方法 */
    protected void onInvisibleToUser() {
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getUserVisibleHint()) {
            handleOnVisibilityChangedToUser(true);
        }
    }
}
