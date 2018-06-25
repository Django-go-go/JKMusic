package com.jkingone.jkmusic.ui.activity.presenter;

import android.content.Context;
import android.util.Log;

import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.data.entity.Song;
import com.jkingone.jkmusic.data.entity.SongInfo;
import com.jkingone.jkmusic.data.remote.RemoteData;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SongListPresenter extends BasePresenter<SongListPresenter.ViewCallback> {
    public static final String TAG = "SongListActivity";

    public interface ViewCallback {
        void showView(List<SongInfo> songInfos);
    }

    private ViewCallback mView;
    private Context mContext;

    public SongListPresenter(ViewCallback view, Context context) {
        mView = view;
        mContext = context;
    }

    public void getSongFromSongList(String id){
        new RemoteData().getSongFromSongList(id).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                mView.showView(Utils.songToSongInfo(response.body()));
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
                mView.showView(Utils.songToSongInfo(response.body()));
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {

            }
        });
    }
}
