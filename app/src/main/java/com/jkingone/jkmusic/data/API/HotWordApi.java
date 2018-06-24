package com.jkingone.jkmusic.data.API;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Administrator on 2017/9/11.
 */

public interface HotWordApi {
    @GET("ting?method=baidu.ting.search.hot")
    Call<List<String>> getHotWord();
}
