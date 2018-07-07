package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.data.entity.SongList;

import java.util.List;

public interface SongListFragContract {
    interface ViewCallback extends BaseContract.BaseView {
        void showView(List<SongList> songLists);
    }

    interface Model extends BaseContract.BaseModel {

    }
}
