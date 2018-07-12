package com.jkingone.jkmusic.ui.mvp;

import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.data.remote.RemoteData;
import com.jkingone.jkmusic.ui.mvp.contract.ClassifySongListContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2018/6/24.
 */

public class ClassifySongListPresenter extends BasePresenter<ClassifySongListContract.ViewCallBack, ClassifySongListContract.Model> {

    public ClassifySongListPresenter(ClassifySongListContract.ViewCallBack view) {
        super();
        attachView(view);
    }

    @Override
    public ClassifySongListContract.Model createModel() {
        return null;
    }



    public void loadData(String tag){
        new RemoteData().getTagSongList(tag).enqueue(new Callback<List<SongList>>() {
            @Override
            public void onResponse(Call<List<SongList>> call, Response<List<SongList>> response) {
                getView().showView(response.body());
            }

            @Override
            public void onFailure(Call<List<SongList>> call, Throwable t) {

            }
        });
    }
}
