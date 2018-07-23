package com.jkingone.jkmusic.api;

import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.entity.TopList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface TopListApi {

    //==============================================================================================
    // 榜单
    //==============================================================================================

    @GET("ting?method=baidu.ting.billboard.billList")
    @Headers("User-Agent:Mozilla")
    Call<List<Song>> getSongFromTopList(@Query("type") int type);

    @GET("ting?method=baidu.ting.billboard.billCategory")
    @Headers("User-Agent:Mozilla")
    Call<List<TopList>> getTopList();
}
