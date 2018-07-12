package com.jkingone.jkmusic;

import com.jkingone.jkmusic.entity.SearchSong;
import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.entity.TopList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MusicApi {
    @GET("ting?&method=baidu.ting.plaza.getFocusPic")
    Call<List<String>> getBanner(@Query("num") int num);

    @GET("ting?method=baidu.ting.song.getEditorRecommend&num=20")
    Call<List<Song>> getHotSong();

    @GET("ting?method=baidu.ting.diy.getHotGeDanAndOfficial")
    Call<List<SongList>> getHotSongList(@Query("num") int num);

    @GET("ting?method=baidu.ting.search.hot")
    Call<List<String>> getHotWord();

    @GET("ting?method=baidu.ting.search.catalogSug")
    Call<List<SearchSong>> getSearchSong(@Query("query") String query);

    @GET("ting?method=baidu.ting.song.baseInfos")
    Call<Song> getSong(@Query("song_id") String id);

    @GET("ting?method=baidu.ting.diy.gedanInfo")
    Call<List<Song>> getSongFromSongList(@Query("listid") String id);

    @GET("ting?method=baidu.ting.billboard.billList")
    Call<List<Song>> getSongFromTopList(@Query("type") int type);

    @GET("ting?method=baidu.ting.diy.gedan")
    Call<List<SongList>> getSongList(@Query("page_size") int page_size, @Query("page_no") int page_no);

    @GET("ting?&method=baidu.ting.diy.search&page_size=100")
    Call<List<SongList>> getTagSongList(@Query("query") String tag);

    @GET("ting?method=baidu.ting.billboard.billCategory&kflag=1")
    Call<List<TopList>> getTopList();
}
