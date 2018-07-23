package com.jkingone.jkmusic.api;

import com.jkingone.jkmusic.entity.Album;
import com.jkingone.jkmusic.entity.AlbumList;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface AlbumApi {

    /**
     * 唱片专辑
     *
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.plaza.getRecommendAlbum&offset=&limit=
     * http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.album.getAlbumInfo&album_id=
     */

    @GET("ting?method=baidu.ting.plaza.getRecommendAlbum")
    @Headers("User-Agent:Mozilla")
    Call<List<AlbumList>> getAlbumList(@Query("offset") int offset, @Query("limit") int limit);


    @GET("ting?method=baidu.ting.album.getAlbumInfo")
    @Headers("User-Agent:Mozilla")
    Call<Album> getAlbumInfo(@Query("album_id") String albumId);

}
