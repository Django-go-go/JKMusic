package com.jkingone.jkmusic.data.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.data.API.BannerApi;
import com.jkingone.jkmusic.data.API.HotSongApi;
import com.jkingone.jkmusic.data.API.HotSongListApi;
import com.jkingone.jkmusic.data.API.HotWordApi;
import com.jkingone.jkmusic.data.API.SearchSongApi;
import com.jkingone.jkmusic.data.API.SongApi;
import com.jkingone.jkmusic.data.API.SongFromSongListApi;
import com.jkingone.jkmusic.data.API.SongFromTopListApi;
import com.jkingone.jkmusic.data.API.SongListApi;
import com.jkingone.jkmusic.data.API.TagSongListApi;
import com.jkingone.jkmusic.data.API.TopListApi;
import com.jkingone.jkmusic.data.entity.NetSong;
import com.jkingone.jkmusic.data.entity.SearchSong;
import com.jkingone.jkmusic.data.entity.Song;
import com.jkingone.jkmusic.data.entity.SongList;
import com.jkingone.jkmusic.data.entity.TopList;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/7/22.
 */

public final class RemoteData {
    public static final String TAG = "RemoteData";

    public Call<List<TopList>> getTopList() {
//        OkHttpClient c = new OkHttpClient.Builder().addInterceptor(new RequestInterceptor()).build();
//        Request r = new Request.Builder().get()
//                .url("http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=json&method=baidu.ting.billboard.billCategory&&kflag=1")
//                .header("User-Agent", "Mozilla")
//                .build();
//        c.newCall(r).enqueue(new okhttp3.Callback() {
//            @Override
//            public void onFailure(okhttp3.Call call, IOException e) {
//                Log.e(TAG, "onFailure: ", e);
//            }
//
//            @Override
//            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
//                Gson gson = new Gson();
//                String s = response.body().string();
//                JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("content");
//                List<TopList> songs = new ArrayList<>();
//                for(JsonElement song : array) {
//                    if (song != null) {
//                        songs.add(gson.fromJson(song, TopList.class));
//                        Log.i(TAG, "convert: " + song);
//                    } else {
//                        Log.i(TAG, "convert: convertFactoryForTopList null");
//                    }
//                }
//            }
//        });
        Retrofit retrofit = createRetrofit(convertFactoryForTopList());
        TopListApi api = retrofit.create(TopListApi.class);
        return api.getTopList();
    }

    public Call<List<SongList>> getSongList(int size, int no) {
        Retrofit retrofit = createRetrofit(convertFactoryForSongList());
        SongListApi api = retrofit.create(SongListApi.class);
        return api.getSongList(size, no);
    }

    public Call<List<Song>> getSongFromSongList(String id) {
        Retrofit retrofit = createRetrofit(convertFactoryForSongFromSongList());
        SongFromSongListApi api = retrofit.create(SongFromSongListApi.class);
        return api.getSongFromSongList(id);
    }

    public Call<List<Song>> getSongFromTopList(int type) {
        Retrofit retrofit = createRetrofit(convertFactoryForSongFromTopList());
        SongFromTopListApi api = retrofit.create(SongFromTopListApi.class);
        return api.getSongFromTopList(type);
    }

    public Call<Song> getSong(String id) {
        Retrofit retrofit = createRetrofit(convertFactoryForSong());
        SongApi api = retrofit.create(SongApi.class);
        return api.getSong(id);
    }

    public Call<List<SongList>> getHotSongList(int num) {
        Retrofit retrofit = createRetrofit(convertFactoryForHotSongList());
        HotSongListApi api = retrofit.create(HotSongListApi.class);
        return api.getHotSongList(num);
    }

    public Call<List<SongList>> getTagSongList(String tag) {
        Retrofit retrofit = createRetrofit(convertFactoryForTagSongList());
        TagSongListApi api = retrofit.create(TagSongListApi.class);
        return api.getTagSongList(tag);
    }


    public Call<List<SearchSong>> getSearchSong(String query) {
        Retrofit retrofit = createRetrofit(convertFactoryForSearchSong());
        SearchSongApi api = retrofit.create(SearchSongApi.class);
        return api.getSearchSong(query);
    }

    public Call<List<String>> getHotWord() {
        Retrofit retrofit = createRetrofit(convertFactoryForHotWord());
        HotWordApi api = retrofit.create(HotWordApi.class);
        return api.getHotWord();
    }

    public void getNetSong(String id, final HttpCallBack callback) {
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = UrlString.Song.songInfo(id);
        Log.i(TAG, "url: " + url);
        Request request = new Request.Builder()
                .url(url)
                .header("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:55.0) Gecko/20100101 Firefox/55.0")
                .header("accept-encoding", "gzip, deflate")
                .header("accept-language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .header("connection", "Keep-Alive")
                .header("accept", "text/html,application/json,application/xml;q=0.9,*/*;q=0.8")
                .header("cookie", "BAIDUID=F7A79F9D5675ADFF8FAB6C533BB6AEE3:FG=1; BIDUPSID=993C0ADCD1F62B1EC7DA0FBB107AB73A; PSTM=1491932224; __cfduid=d9ca8f30118bd2c533a960acf916c71611495113898; BDUSS=WtmMEh6UkNHZHI5ejBBU29QWXg0bGczNWlwU1VWNXlKRnd-VTBva0tyWHdScEpaSUFBQUFBJCQAAAAAAAAAAAEAAAB5WqKLyMjH6bXEamluZ2ppMTIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAPC5alnwuWpZY; BDORZ=FFFB88E999055A3F8A630C64834BD6D0; BDRCVFR[Fc9oatPmwxn]=srT4swvGNE6uzdhUL68mv3; PSINO=3; H_PS_PSSID=1436_21078_19897_22160; app_vip=show; UM_distinctid=15e78adb978452-003ee4a7517fd3-17387440-104040-15e78adb97947c")
                .header("Host", "tingapi.ting.baidu.com")
                .header("Upgrade-Insecure-Requests", "1")
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e(TAG, "onFailure: ", e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    String s = response.body().string();
                    Log.i(TAG, "onResponse: ================> " + s);
                    if (s != null && s.length() > 0) {
                        Gson gson = new Gson();
                        JsonArray array1 = new JsonParser().parse(s).getAsJsonObject().getAsJsonObject("songurl").getAsJsonArray("url");
                        NetSong netSong1 = gson.fromJson(array1.get(0), NetSong.class);
                        JsonObject array2 = new JsonParser().parse(s).getAsJsonObject().getAsJsonObject("songinfo");
                        NetSong netSong = gson.fromJson(array2, NetSong.class);
                        netSong.setFile_link(netSong1.getFile_link());
                        if (callback != null) {
                            callback.netSong(netSong);
                        }
                    } else {
                        if (callback != null) {
                            callback.netSong(null);
                        }
                    }
                }

            }
        });
    }

    public Call<List<Song>> getHotSong() {
        Retrofit retrofit = createRetrofit(convertFactoryForHotSong());
        HotSongApi api = retrofit.create(HotSongApi.class);
        return api.getHotSong();
    }

    public Call<List<String>> getBanner() {
        Retrofit retrofit = createRetrofit(convertFactoryForBanner());
        BannerApi api = retrofit.create(BannerApi.class);
        return api.getBanner(9);
    }

    private class RequestInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .header("User-Agent", "Mozilla")
                    .build();
            return chain.proceed(request);
        }
    }

    private OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(new RequestInterceptor());
        return httpClientBuilder.build();
    }

    private Retrofit createRetrofit(Converter.Factory factory) {
        return new Retrofit.Builder()
                .baseUrl("http://tingapi.ting.baidu.com/v1/restserver/")
                .client(getOkHttpClient())
                .addConverterFactory(factory)
                .build();
    }

    private Converter.Factory convertFactoryForSongList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, List<SongList>> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<SongList>>() {
                    @Override
                    public List<SongList> convert(@NonNull ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("content");
                        List<SongList> songs = new ArrayList<>();
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, SongList.class));
                                Log.i(TAG, "convert: " + song);
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForTopList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<TopList>>() {
                    @Override
                    public List<TopList> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("content");
                        List<TopList> songs = new ArrayList<>();
                        Log.i(TAG, "convert: " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, TopList.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForSongFromSongList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, List<Song>> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<Song>>() {
                    @Override
                    public List<Song> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("content");
                        List<Song> songs = new ArrayList<>();
                        Log.i(TAG, "convert: " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, Song.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForSongFromTopList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<Song>>() {
                    @Override
                    public List<Song> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("song_list");
                        List<Song> songs = new ArrayList<>();
                        Log.i(TAG, "convert: " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, Song.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForSong() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, Song>() {
                    @Override
                    public Song convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonObject("result").getAsJsonArray("items");
                        List<Song> songs = new ArrayList<>();
                        Log.i(TAG, "convert: " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, Song.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs.get(0);
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForHotSongList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<SongList>>() {
                    @Override
                    public List<SongList> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonObject("content").getAsJsonArray("list");
                        List<SongList> songs = new ArrayList<>();
                        Log.i(TAG, "convert: convertFactoryForHotSongList " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, SongList.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForTagSongList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<SongList>>() {
                    @Override
                    public List<SongList> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("content");
                        List<SongList> songs = new ArrayList<>();
                        Log.i(TAG, "convert: convertFactoryForHotSongList " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, SongList.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForSearchSong() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<SearchSong>>() {
                    @Override
                    public List<SearchSong> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("song");
                        List<SearchSong> songs = new ArrayList<>();
                        Log.i(TAG, "convert: convertFactoryForHotSongList " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, SearchSong.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForHotWord() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<String>>() {
                    @Override
                    public List<String> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("result");
                        List<String> songs = new ArrayList<>();
                        Log.i(TAG, "convert: convertFactoryForHotSongList " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(song.getAsJsonObject().get("word").getAsString());
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForHotSong() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<Song>>() {
                    @Override
                    public List<Song> convert(ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().
                                getAsJsonArray("content").get(0).getAsJsonObject().getAsJsonArray("song_list");
                        List<Song> songs = new ArrayList<>();
                        Log.i(TAG, "convert: " + s);
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, Song.class));
                                Log.i(TAG, "convert: " + song);
                            } else {
                                Log.i(TAG, "convert: convertFactoryForTopList null");
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryForBanner() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<String>>() {
                    @Override
                    public List<String> convert(ResponseBody value) throws IOException {
                        Log.i(TAG, "convert: " + value);
                        String s = value.string();
//                        JsonArray array = new JsonParser().parse(s).getAsJsonObject().getAsJsonArray("pic");
                        List<String> banners = new ArrayList<>();
//                        for (JsonElement song : array) {
//                            if (song != null) {
//                                banners.add(song.getAsJsonObject().get("randpic").getAsString());
//                            }
//                        }
                        banners.add(s);
                        return banners;
                    }
                };
            }
        };
    }

}
