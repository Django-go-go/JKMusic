package com.jkingone.jkmusic.data.API;

import com.jkingone.jkmusic.data.entity.Song;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/9/11.
 */

public interface SongApi {
    @GET("ting?method=baidu.ting.song.baseInfos")
    Call<Song> getSong(@Query("song_id") String id);
}
