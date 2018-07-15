package com.jkingone.jkmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkingone.jkmusic.ui.mvp.LocalFragPresenter;
import com.jkingone.jkmusic.ui.mvp.contract.LocalFragContract;

public class LocalFragment extends BaseFragment<LocalFragPresenter> implements LocalFragContract.ViewCallback {

    public static LocalFragment newInstance(String... params) {
        LocalFragment fragment = new LocalFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    public LocalFragPresenter createPresenter() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
