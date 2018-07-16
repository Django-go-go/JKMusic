package com.jkingone.jkmusic.api;

import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.entity.SongList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SongListApi {

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
}
