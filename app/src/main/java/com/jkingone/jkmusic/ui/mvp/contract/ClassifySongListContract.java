package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.data.entity.SongList;

import java.util.List;

public interface ClassifySongListContract {

    interface ViewCallBack extends BaseContract.BaseView {
        void showView(List<SongList> classifySongLists);
    }

    interface Model extends BaseContract.BaseModel {

    }
}
