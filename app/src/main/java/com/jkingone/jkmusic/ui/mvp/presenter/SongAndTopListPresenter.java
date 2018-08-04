package com.jkingone.jkmusic.ui.mvp.presenter;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.api.SongListApi;
import com.jkingone.jkmusic.api.TopListApi;
import com.jkingone.jkmusic.entity.Song;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.mvp.contract.SongAndTopListContract;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by Administrator on 2017/8/8.
 */

public class SongAndTopListPresenter extends BasePresenter<SongAndTopListContract.ViewCallback, SongAndTopListContract.Model> {

    public SongAndTopListPresenter(SongAndTopListContract.ViewCallback view) {
        super();
        attachView(view);
    }

    public void getSongFromSongList(String id){
        mModel.getSongFromSongList(id).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                getView().showView(Utils.songToSongInfo(response.body()));
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                getView().showView(null);
            }
        });
    }

    public void getSongFromTopList(int type){
        mModel.getSongFromTopList(type).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                getView().showView(Utils.songToSongInfo(response.body()));
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                getView().showView(null);
            }
        });
    }

    @Override
    public SongAndTopListContract.Model createModel() {
        return new SongAndTopListContract.Model() {

            @Override
            public Call<List<Song>> getSongFromSongList(String id) {
                Retrofit retrofit = createRetrofit(convertFactoryForSongFromSongList());
                SongListApi api = retrofit.create(SongListApi.class);
                return api.getSongFromSongList(id);
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
                                for (JsonElement song : array) {
                                    if (song != null) {
                                        songs.add(gson.fromJson(song, Song.class));
                                    }
                                }
                                return songs;
                            }
                        };
                    }
                };
            }

            @Override
            public Call<List<Song>> getSongFromTopList(int type) {
                Retrofit retrofit = createRetrofit(convertFactoryForSongFromTopList());
                TopListApi api = retrofit.create(TopListApi.class);
                return api.getSongFromTopList(type);
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
                                for (JsonElement song : array) {
                                    if (song != null) {
                                        songs.add(gson.fromJson(song, Song.class));
                                    }
                                }
                                return songs;
                            }
                        };
                    }
                };
            }

            private Retrofit createRetrofit(Converter.Factory factory) {
                return new Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .addConverterFactory(factory)
                        .build();
            }
        };
    }
}
