package com.jkingone.jkmusic.ui.fragment;

import android.content.Context;

import com.jkingone.jkmusic.ui.mvp.BasePresenter;

public abstract class BaseFragment<P extends BasePresenter> extends LazyFragment {

    protected P mPresenter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = createPresenter();
    }

    public abstract P createPresenter();
}
