package com.jkingone.jkmusic.api;

import com.jkingone.jkmusic.entity.Artist;
import com.jkingone.jkmusic.entity.ArtistList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface ArtistApi {
    /**
     * 全部地区
     */
    int AREA_ALL = 0;
    /**
     * 华语
     */
    int AREA_CHINA = 6;
    /**
     * 欧美
     */
    int AREA_EU = 3;
    /**
     * 韩国
     */
    int AREA_KOREA = 7;
    /**
     * 日本
     */
    int AREA_JAPAN = 60;
    /**
     * 其他
     */
    int AREA_OTHER = 5;

    /**
     * 无选择
     */
    int SEX_NONE = 0;
    /**
     * 男性
     */
    int SEX_MALE = 1;
    /**
     * 女性
     */
    int SEX_FEMALE = 2;
    /**
     * 组合
     */
    int SEX_GROUP = 3;

    int ORDER_HOT = 1;

    int ORDER_ID = 2;

    /**
     * 获取艺术家列表
     *
     * @param offset 偏移
     * @param limit  数量
     * @param area   地区：0不分,6华语,3欧美,7韩国,60日本,5其他
     * @param sex    性别：0不分,1男,2女,3组合
     * @param order  排序：1按热门，2按艺术家id
     * @param abc    艺术家名首字母：a-z,other其他
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.artist.getList&offset=0&limit=100&area=0&sex=0&order=1&abc=
     */
    @GET("ting?method=baidu.ting.artist.getList")
    @Headers("User-Agent:Mozilla")
    Call<List<ArtistList>> getArtistList(@Query("offset")int offset, @Query("limit")int limit, @Query("area")int area,
                                         @Query("sex")int sex, @Query("order")int order, @Query("abc")String abc);


    /**
     * 艺术家歌曲
     *
     * @param tingUid  tinguid
     * @param artistId 艺术家id
     * @param offset   偏移量
     * @param limit    获取数量
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.artist.getSongList&tinguid=&artistid=&offset=&limits=
     */
    @GET("ting?method=baidu.ting.artist.getSongList")
    @Headers("User-Agent:Mozilla")
    Call<List<Artist.Song>> getArtistSongList(@Query("tinguid")String tingUid, @Query("artistid")String artistId,
                                              @Query("offset")int offset, @Query("limits")int limit);


    /**
     * 艺术家信息
     *
     * @param tingUid  tinguid
     * @param artistId 艺术家id
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.artist.getinfo&tinguid=2517&artistid=88
     */
    @GET("ting?method=baidu.ting.artist.getinfo")
    @Headers("User-Agent:Mozilla")
    Call<Artist.ArtistInfo> getArtistInfo(@Query("tinguid")String tingUid, @Query("artistid")String artistId);
}
