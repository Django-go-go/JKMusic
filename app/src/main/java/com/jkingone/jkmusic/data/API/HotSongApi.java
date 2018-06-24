package com.jkingone.jkmusic.data.API;

import com.jkingone.jkmusic.data.entity.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2017/9/13.
 */

public interface HotSongApi {
    @GET("ting?method=baidu.ting.song.getEditorRecommend&num=20")
    Call<List<Song>> getHotSong();
}
