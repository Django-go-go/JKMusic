package com.jkingone.jkmusic.ui.mvp;

import com.jkingone.jkmusic.data.entity.TopList;
import com.jkingone.jkmusic.data.remote.RemoteData;
import com.jkingone.jkmusic.ui.mvp.contract.TopListFragContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Administrator on 2017/8/3.
 */

public class TopListFragPresenter extends BasePresenter<TopListFragContract.ViewCallback, TopListFragContract.Model> {

    public TopListFragPresenter(TopListFragContract.ViewCallback view) {
        super();
        attachView(view);
    }

    public void loadData(){
        new RemoteData().getTopList().enqueue(new Callback<List<TopList>>() {
            @Override
            public void onResponse(Call<List<TopList>> call, Response<List<TopList>> response) {
                getView().showView(response.body());
            }

            @Override
            public void onFailure(Call<List<TopList>> call, Throwable t) {

            }
        });
    }


    @Override
    public TopListFragContract.Model createModel() {
        return null;
    }
}
