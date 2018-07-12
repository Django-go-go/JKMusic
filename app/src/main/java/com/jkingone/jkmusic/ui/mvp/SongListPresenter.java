package com.jkingone.jkmusic.ui.mvp;

import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.data.remote.RemoteData;
import com.jkingone.jkmusic.ui.mvp.contract.SongListContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SongListPresenter extends BasePresenter<SongListContract.ViewCallback, SongListContract.Model> {
    public static final String TAG = "SongListActivity";

    public SongListPresenter(SongListContract.ViewCallback view) {
        super();
        attachView(view);
    }

    public void getSongFromSongList(String id){
        new RemoteData().getSongFromSongList(id).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                getView().showView(Utils.songToSongInfo(response.body()));
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }

    public void getSongFromTopList(int type){
        new RemoteData().getSongFromTopList(type).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                getView().showView(Utils.songToSongInfo(response.body()));
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }

    @Override
    public SongListContract.Model createModel() {
        return null;
    }
}
