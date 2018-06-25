package com.jkingone.jkmusic.ui.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.data.entity.SongInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PlayFragment extends LazyFragment {

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.iv_play)
    ImageView mImageViewPlay;
    @BindView(R.id.iv_menu)
    ImageView mImageViewMenu;
    @BindView(R.id.liner_root)
    View mViewRoot;

    private Unbinder mUnbinder;

    private SongInfo mCurSongInfo;
    private List<SongInfo> mSongInfos;

    public static PlayFragment newInstance(String... params) {
        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @OnClick(R.id.iv_menu)
    public void onMenuClick() {

    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {

    }

    @OnClick(R.id.liner_root)
    public void onRootClick() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    public interface GetSongListener {
    }
}
