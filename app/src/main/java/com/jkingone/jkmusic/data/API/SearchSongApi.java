package com.jkingone.jkmusic.data.API;

import com.jkingone.jkmusic.data.entity.SearchSong;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/9/11.
 */

public interface SearchSongApi {
    @GET("ting?method=baidu.ting.search.catalogSug")
    Call<List<SearchSong>> getSearchSong(@Query("query") String query);
}
