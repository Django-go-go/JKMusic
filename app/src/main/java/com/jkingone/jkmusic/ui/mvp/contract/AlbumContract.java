package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.AlbumList;

import java.util.List;

public interface AlbumContract {
    interface ViewCallback extends BaseContract.BaseView {
        void getAlbumList(List<AlbumList> albumLists);
    }

    interface Model extends BaseContract.BaseModel {

    }
}
