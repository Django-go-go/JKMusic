package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.ui.mvp.base.BaseContract;

import java.util.List;

import retrofit2.Call;

public interface SongListFragContract {
    interface ViewCallback extends BaseContract.BaseView {
        void showView(List<SongList> songLists);
    }

    interface Model extends BaseContract.BaseModel {
        Call<List<SongList>> getSongList(int size, int no);
    }
}
