package com.jkingone.jkmusic.api;

import com.jkingone.jkmusic.entity.SearchSong;
import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.entity.TopList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicApi {

    public static final String BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting?";

    /**
     * 轮播音乐封面
     * @param num 数量
     */
    @GET("ting?&method=baidu.ting.plaza.getFocusPic")
    Call<List<String>> getBanner(@Query("num") int num);

    @GET("ting?method=baidu.ting.song.getEditorRecommend&num=20")
    Call<List<Song>> getHotSong();

    @GET("ting?method=baidu.ting.search.hot")
    Call<List<String>> getHotWord();

    @GET("ting?method=baidu.ting.search.catalogSug")
    Call<List<SearchSong>> getSearchSong(@Query("query") String query);

    @GET("ting?method=baidu.ting.song.baseInfos")
    Call<Song> getSong(@Query("song_id") String id);





}
