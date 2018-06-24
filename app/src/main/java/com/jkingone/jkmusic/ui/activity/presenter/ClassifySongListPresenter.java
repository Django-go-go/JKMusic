package com.jkingone.jkmusic.ui.activity.presenter;

import com.jkingone.jkmusic.data.entity.SongList;
import com.jkingone.jkmusic.data.remote.RemoteData;
import com.jkingone.jkmusic.ui.activity.BaseActivity;
import com.jkingone.jkmusic.ui.fragment.presenter.SongListFragPresenter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/6/24.
 */

public class ClassifySongListPresenter extends BasePresenter<ClassifySongListPresenter.ViewCallBack> {

    private ViewCallBack mViewCallBack;

    public ClassifySongListPresenter(ViewCallBack viewCallBack) {
        mViewCallBack = viewCallBack;
    }

    public interface ViewCallBack {
        void showView(List<SongList> classifySongLists);
    }

    public void loadData(String tag){
        new RemoteData().getTagSongList(tag).enqueue(new Callback<List<SongList>>() {
            @Override
            public void onResponse(Call<List<SongList>> call, Response<List<SongList>> response) {
                mViewCallBack.showView(response.body());
            }

            @Override
            public void onFailure(Call<List<SongList>> call, Throwable t) {

            }
        });
    }
}
