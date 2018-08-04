package com.jkingone.jkmusic.ui.mvp.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.api.SongListApi;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.mvp.contract.SongListFragContract;

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
 * Created by Administrator on 2017/8/6.
 */

public class SongListFragPresenter extends BasePresenter<SongListFragContract.ViewCallback, SongListFragContract.Model> {

    public SongListFragPresenter(SongListFragContract.ViewCallback view) {
        super();
        attachView(view);
    }

    public void getSongList(int size, int no){

        mModel.getSongList(size, no).enqueue(new Callback<List<SongList>>() {
            @Override
            public void onResponse(Call<List<SongList>> call, Response<List<SongList>> response) {
                getView().showView(response.body());
            }

            @Override
            public void onFailure(Call<List<SongList>> call, Throwable t) {
                getView().showView(null);
            }
        });

    }

    @Override
    public SongListFragContract.Model createModel() {
        return new SongListFragContract.Model() {
            @Override
            public Call<List<SongList>> getSongList(int size, int no) {
                Retrofit retrofit = createRetrofit(convertFactoryForSongList());
                SongListApi api = retrofit.create(SongListApi.class);
                return api.getSongList(size, no);
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

        };
    }
}
