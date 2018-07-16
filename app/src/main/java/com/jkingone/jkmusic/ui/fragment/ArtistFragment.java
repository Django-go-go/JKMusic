package com.jkingone.jkmusic.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.ui.mvp.ArtistPresenter;
import com.jkingone.jkmusic.ui.mvp.contract.ArtistContract;

import butterknife.OnClick;

public class ArtistFragment extends BaseFragment<ArtistPresenter> implements ArtistContract.ViewCallback {

    public static ArtistFragment newInstance(String... params) {
        ArtistFragment fragment = new ArtistFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    public ArtistPresenter createPresenter() {
        return null;
    }

    @Override
    protected void onLazyLoadOnce() {
        super.onLazyLoadOnce();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_artist, container, false);
        return view;
    }

    @OnClick({R.id.frame_china_man, R.id.frame_china_woman, R.id.frame_china_group,
            R.id.frame_un_man, R.id.frame_un_woman, R.id.frame_un_group,
            R.id.frame_japan_man, R.id.frame_japan_woman, R.id.frame_japan_group,
            R.id.frame_korea_man, R.id.frame_korea_woman, R.id.frame_korea_group,
            R.id.frame_other_man, R.id.frame_other_woman, R.id.frame_other_group})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.frame_china_man:
                break;
            case R.id.frame_china_woman:
                break;
            case R.id.frame_china_group:
                break;
            case R.id.frame_un_man:
                break;
            case R.id.frame_un_woman:
                break;
            case R.id.frame_un_group:
                break;
            case R.id.frame_japan_man:
                break;
            case R.id.frame_japan_woman:
                break;
            case R.id.frame_japan_group:
                break;
            case R.id.frame_korea_man:
                break;
            case R.id.frame_korea_woman:
                break;
            case R.id.frame_korea_group:
                break;
            case R.id.frame_other_man:
                break;
            case R.id.frame_other_woman:
                break;
            case R.id.frame_other_group:
                break;
        }
    }
}
