package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.SongInfo;

import java.util.List;

public interface SongListContract {

    interface ViewCallback extends BaseContract.BaseView {
        void showView(List<SongInfo> songInfos);
    }

    interface Model extends BaseContract.BaseModel {

    }
}
