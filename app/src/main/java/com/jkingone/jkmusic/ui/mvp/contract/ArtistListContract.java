package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.ArtistList;
import com.jkingone.jkmusic.ui.mvp.base.BaseContract;

import java.util.List;

import retrofit2.Call;

public interface ArtistListContract {

    interface ViewCallback extends BaseContract.BaseView {
        void showArtistList(List<ArtistList> artistLists);
    }

    interface Model extends BaseContract.BaseModel {
        Call<List<ArtistList>> getArtistList(int offset, int limit, int area, int sex, int order, String abc);
    }
}
