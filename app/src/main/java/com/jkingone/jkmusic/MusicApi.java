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

    //==============================================================================================
    // 歌单
    //==============================================================================================

    /**
     * 热门歌单
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.diy.getHotGeDanAndOfficial
     */
    @GET("ting?method=baidu.ting.diy.getHotGeDanAndOfficial")
    Call<List<SongList>> getHotSongList();

    /**
     * 歌单
     * @param page_no   页码
     * @param page_size 每页数量
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.diy.gedan&page_size=10&page_no=1
     */
    @GET("ting?method=baidu.ting.diy.gedan")
    Call<List<SongList>> getSongList(@Query("page_size") int page_size, @Query("page_no") int page_no);

    /**
     * 包含标签的歌单
     * @param tag      标签名
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.diy.search&page_size=10&page_no=1&query=华语
     */
    @GET("ting?&method=baidu.ting.diy.search&page_size=100")
    Call<List<SongList>> getTagSongList(@Query("query") String tag);

    /**
     * 歌单信息和歌曲
     * @param id 歌单id
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.diy.gedanInfo&listid=
     */
    @GET("ting?method=baidu.ting.diy.gedanInfo")
    Call<List<Song>> getSongFromSongList(@Query("listid") String id);

    /**
     * 歌单分类
     */
    @GET("ting?method=baidu.ting.diy.gedanCategory")
    Call<List<String>> geSongListCategory();

    //==============================================================================================
    // 榜单
    //==============================================================================================


}
