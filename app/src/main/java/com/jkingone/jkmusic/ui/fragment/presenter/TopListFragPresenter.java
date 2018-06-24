package com.jkingone.jkmusic.ui.fragment.presenter;

import android.content.Context;
import android.util.Log;

import com.jkingone.jkmusic.data.entity.TopList;
import com.jkingone.jkmusic.data.remote.RemoteData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TopListFragPresenter {
    private TopListFragView mView;
    private Context mContext;

    public TopListFragPresenter(TopListFragView topListFragView, Context context) {
        mView = topListFragView;
        mContext = context;
    }

    public void loadData(){
        new RemoteData().getTopList().enqueue(new Callback<List<TopList>>() {
            @Override
            public void onResponse(Call<List<TopList>> call, Response<List<TopList>> response) {
                mView.showView(response.body());
            }

            @Override
            public void onFailure(Call<List<TopList>> call, Throwable t) {

            }
        });
    }

    public interface TopListFragView {
        void showView(List<TopList> topLists);
    }

}
