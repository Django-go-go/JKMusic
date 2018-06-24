package com.jkingone.jkmusic.data.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Administrator on 2017/9/20.
 */

public interface BannerApi {
    //http://tingapi.ting.baidu.com/v1/restserver/ting?&method=baidu.ting.plaza.getFocusPic&num=9
    @GET("ting?&method=baidu.ting.plaza.getFocusPic")
    Call<List<String>> getBanner(@Query("num") int num);
}
