package com.jkingone.jkmusic.ui.fragment.presenter;

import android.content.Context;
import android.util.Log;

import com.jkingone.jkmusic.data.entity.SongList;
import com.jkingone.jkmusic.data.remote.RemoteData;
import com.jkingone.jkmusic.ui.fragment.SongListFragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/6.
 */

public class SongListFragPresenter {
    private Context mContext;
    private ViewCallback mView;

    public interface ViewCallback {
        void showView(List<SongList> songLists);
    }

    public SongListFragPresenter(Context context, ViewCallback view) {
        mContext = context;
        mView = view;
    }

    public void getSongList(int size, int no){

        new RemoteData().getSongList(size, no).enqueue(new Callback<List<SongList>>() {
            @Override
            public void onResponse(Call<List<SongList>> call, Response<List<SongList>> response) {
                mView.showView(response.body());
            }

            @Override
            public void onFailure(Call<List<SongList>> call, Throwable t) {
                Log.e(SongListFragment.TAG, "onFailure: ", t);
            }
        });

    }
}
