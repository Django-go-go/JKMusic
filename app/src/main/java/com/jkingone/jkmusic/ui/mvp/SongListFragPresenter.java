package com.jkingone.jkmusic.ui.mvp;

import android.util.Log;

import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.data.remote.RemoteData;
import com.jkingone.jkmusic.ui.fragment.SongListFragment;
import com.jkingone.jkmusic.ui.mvp.contract.SongListFragContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/6.
 */

public class SongListFragPresenter extends BasePresenter<SongListFragContract.ViewCallback, SongListFragContract.Model> {

    public SongListFragPresenter(SongListFragContract.ViewCallback view) {
        super();
        attachView(view);
    }

    public void getSongList(int size, int no){

        new RemoteData().getSongList(size, no).enqueue(new Callback<List<SongList>>() {
            @Override
            public void onResponse(Call<List<SongList>> call, Response<List<SongList>> response) {
                getView().showView(response.body());
            }

            @Override
            public void onFailure(Call<List<SongList>> call, Throwable t) {
                Log.e(SongListFragment.TAG, "onFailure: ", t);
            }
        });

    }

    @Override
    public SongListFragContract.Model createModel() {
        return null;
    }
}
