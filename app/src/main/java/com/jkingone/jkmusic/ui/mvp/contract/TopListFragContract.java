package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.TopList;

import java.util.List;

public interface TopListFragContract {

    interface ViewCallback extends BaseContract.BaseView {
        void showView(List<TopList> topLists);
    }

    interface Model extends BaseContract.BaseModel {

    }
}
