package com.jkingone.jkmusic.data.API;

import com.jkingone.jkmusic.data.entity.SongList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/9/10.
 */

public interface SongListApi {
    @GET("ting?method=baidu.ting.diy.gedan")
    Call<List<SongList>> getSongList(@Query("page_size") int page_size, @Query("page_no") int page_no);

}
