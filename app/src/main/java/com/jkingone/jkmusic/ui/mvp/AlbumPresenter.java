package com.jkingone.jkmusic.ui.mvp;

import com.jkingone.jkmusic.data.remote.RemoteData;
import com.jkingone.jkmusic.entity.AlbumList;
import com.jkingone.jkmusic.ui.mvp.contract.AlbumContract;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AlbumPresenter extends BasePresenter<AlbumContract.ViewCallback, AlbumContract.Model> {

    public AlbumPresenter(AlbumContract.ViewCallback viewCallback) {
        super();
        attachView(viewCallback);
    }

    public void getAlbumList(int offset, int limit) {
        new RemoteData().getAlbumList(offset, limit).enqueue(new Callback<List<AlbumList>>() {
            @Override
            public void onResponse(Call<List<AlbumList>> call, Response<List<AlbumList>> response) {
                getView().getAlbumList(response.body());
            }

            @Override
            public void onFailure(Call<List<AlbumList>> call, Throwable t) {

            }
        });
    }

    @Override
    public AlbumContract.Model createModel() {
        return null;
    }
}
