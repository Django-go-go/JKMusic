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
import com.jkingone.jkmusic.api.AlbumApi;
import com.jkingone.jkmusic.entity.AlbumList;

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
public class AlbumListViewModel extends AndroidViewModel {
    private MutableLiveData<List<AlbumList>> mAlbumListLiveData;

    public AlbumListViewModel(@NonNull Application application) {
        super(application);
        mAlbumListLiveData = new MutableLiveData<>();
    }

    public LiveData<List<AlbumList>> getAlbumListLiveData() {
        return mAlbumListLiveData;
    }

    public void getAlbumList(int offset, int limit) {
        Retrofit retrofit = createRetrofit(convertFactoryForAlbumList());
        AlbumApi api = retrofit.create(AlbumApi.class);
        api.getAlbumList(offset, limit).enqueue(new Callback<List<AlbumList>>() {
            @Override
            public void onResponse(Call<List<AlbumList>> call, Response<List<AlbumList>> response) {
                mAlbumListLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<AlbumList>> call, Throwable t) {
                mAlbumListLiveData.postValue(null);
            }
        });
    }

    private Retrofit createRetrofit(Converter.Factory factory) {
        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(factory)
                .build();
    }

    private Converter.Factory convertFactoryForAlbumList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<AlbumList>>() {
                    @Override
                    public List<AlbumList> convert(@NonNull ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String s = value.string();
                        JsonArray array = new JsonParser().parse(s)
                                .getAsJsonObject()
                                .getAsJsonObject("plaze_album_list")
                                .getAsJsonObject("RM").getAsJsonObject("album_list")
                                .getAsJsonArray("list");

                        List<AlbumList> albumLists = new ArrayList<>();
                        for (JsonElement song : array) {
                            if (song != null) {
                                albumLists.add(gson.fromJson(song, AlbumList.class));
                            }
                        }
                        return albumLists;
                    }
                };
            }
        };
    }
}
