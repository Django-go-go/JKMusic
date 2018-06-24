package com.jkingone.jkmusic.data.API;

import com.jkingone.jkmusic.data.entity.Song;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/9/11.
 */

public interface SongFromTopListApi {
    @GET("ting?method=baidu.ting.billboard.billList")
    Call<List<Song>> getSongFromTopList(@Query("type") int type);
}
