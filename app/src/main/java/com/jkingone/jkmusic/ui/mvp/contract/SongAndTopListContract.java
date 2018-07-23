package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.ui.mvp.base.BaseContract;

import java.util.List;

import retrofit2.Call;

public interface SongAndTopListContract {

    interface ViewCallback extends BaseContract.BaseView {
        void showView(List<SongInfo> songInfos);
    }

    interface Model extends BaseContract.BaseModel {
        Call<List<Song>> getSongFromSongList(String id);
        Call<List<Song>> getSongFromTopList(int type);
    }
}
