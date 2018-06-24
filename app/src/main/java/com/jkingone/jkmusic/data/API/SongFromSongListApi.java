package com.jkingone.jkmusic.data.API;

import com.jkingone.jkmusic.data.entity.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/9/11.
 */

public interface SongFromSongListApi {
    @GET("ting?method=baidu.ting.diy.gedanInfo")
    Call<List<Song>> getSongFromSongList(@Query("listid") String id);
}
