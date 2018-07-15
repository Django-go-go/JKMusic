package com.jkingone.jkmusic.api;

import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.entity.TopList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TopListApi {
    @GET("ting?method=baidu.ting.billboard.billList")
    Call<List<Song>> getSongFromTopList(@Query("type") int type);

    @GET("ting?method=baidu.ting.billboard.billCategory")
    Call<List<TopList>> getTopList();
}
