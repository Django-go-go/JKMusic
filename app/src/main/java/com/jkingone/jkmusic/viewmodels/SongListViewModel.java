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
import com.jkingone.jkmusic.api.SongListApi;
import com.jkingone.jkmusic.entity.SongList;

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
public class SongListViewModel extends AndroidViewModel {
    private MutableLiveData<List<SongList>> mSongListLiveData;
    private MutableLiveData<List<SongList>> mTagSongListLiveData;

    public SongListViewModel(@NonNull Application application) {
        super(application);
        mSongListLiveData = new MutableLiveData<>();
        mTagSongListLiveData = new MutableLiveData<>();
    }

    public LiveData<List<SongList>> getSongListLiveData() {
        return mSongListLiveData;
    }

    public LiveData<List<SongList>> getTagSongListLiveData() {
        return mTagSongListLiveData;
    }

    public void getSongList(int size, int no) {
        Retrofit retrofit = createRetrofit(convertFactoryForSongList());
        SongListApi api = retrofit.create(SongListApi.class);
        api.getSongList(size, no).enqueue(new Callback<List<SongList>>() {
            @Override
            public void onResponse(Call<List<SongList>> call, Response<List<SongList>> response) {
                mSongListLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<SongList>> call, Throwable t) {
                mSongListLiveData.postValue(null);
            }
        });
    }

    private Retrofit createRetrofit(Converter.Factory factory) {
        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
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
                        for (JsonElement song : array) {
                            if (song != null) {
                                songs.add(gson.fromJson(song, SongList.class));
                            }
                        }
                        return songs;
                    }
                };
            }
        };
    }

    public void getTagSongList(String tag) {
        Retrofit retrofit = createRetrofit(convertFactoryForTagSongList());
        SongListApi api = retrofit.create(SongListApi.class);
        api.getTagSongList(tag).enqueue(new Callback<List<SongList>>() {
            @Override
            public void onResponse(Call<List<SongList>> call, Response<List<SongList>> response) {
                mTagSongListLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<SongList>> call, Throwable t) {
                mTagSongListLiveData.postValue(null);
            }
        });
    }

}
