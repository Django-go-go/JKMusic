package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.AlbumList;
import com.jkingone.jkmusic.ui.mvp.base.BaseContract;

import java.util.List;

import retrofit2.Call;

public interface AlbumListContract {
    interface ViewCallback extends BaseContract.BaseView {
        void showAlbumList(List<AlbumList> albumLists);
    }

    interface Model extends BaseContract.BaseModel {
        Call<List<AlbumList>> getAlbumList(int offset, int limit);
    }
}
