package com.jkingone.jkmusic.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
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
import com.jkingone.jkmusic.entity.SongInfo;

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
 * Created by jiangbo03 at 2018/8/31
 */
public class SongInfoViewModel extends AndroidViewModel {
    private MutableLiveData<List<SongInfo>> mSongLiveData;

    public SongInfoViewModel(@NonNull Application application) {
        super(application);
        mSongLiveData = new MutableLiveData<>();
    }

    public LiveData<List<SongInfo>> getSongLiveData() {
        return mSongLiveData;
    }

    public void getSongFromSongList(String id) {
        Retrofit retrofit = createRetrofit(convertFactoryForSongFromSongList());
        SongListApi api = retrofit.create(SongListApi.class);
        api.getSongFromSongList(id).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                mSongLiveData.postValue(Utils.songToSongInfo(response.body()));
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                mSongLiveData.postValue(null);
            }
        });
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

    public void getSongFromTopList(int type) {
        Retrofit retrofit = createRetrofit(convertFactoryForSongFromTopList());
        TopListApi api = retrofit.create(TopListApi.class);
        api.getSongFromTopList(type).enqueue(new Callback<List<Song>>() {
            @Override
            public void onResponse(Call<List<Song>> call, Response<List<Song>> response) {
                mSongLiveData.postValue(Utils.songToSongInfo(response.body()));
            }

            @Override
            public void onFailure(Call<List<Song>> call, Throwable t) {
                mSongLiveData.postValue(null);
            }
        });
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
}
