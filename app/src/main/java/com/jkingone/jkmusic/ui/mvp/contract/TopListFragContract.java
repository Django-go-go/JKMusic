package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.TopList;
import com.jkingone.jkmusic.ui.mvp.base.BaseContract;

import java.util.List;

import retrofit2.Call;

public interface TopListFragContract {

    interface ViewCallback extends BaseContract.BaseView {
        void showView(List<TopList> topLists);
    }

    interface Model extends BaseContract.BaseModel {
        Call<List<TopList>> getTopList();
    }
}
