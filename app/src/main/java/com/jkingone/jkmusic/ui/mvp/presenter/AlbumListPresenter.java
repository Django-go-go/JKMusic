package com.jkingone.jkmusic.ui.mvp.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.api.AlbumApi;
import com.jkingone.jkmusic.entity.AlbumList;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.mvp.contract.AlbumListContract;

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

public class AlbumListPresenter extends BasePresenter<AlbumListContract.ViewCallback, AlbumListContract.Model> {

    public AlbumListPresenter(AlbumListContract.ViewCallback viewCallback) {
        super();
        attachView(viewCallback);
    }

    public void getAlbumList(int offset, int limit) {
        mModel.getAlbumList(offset, limit).enqueue(new Callback<List<AlbumList>>() {
            @Override
            public void onResponse(Call<List<AlbumList>> call, Response<List<AlbumList>> response) {
                getView().showAlbumList(response.body());
            }

            @Override
            public void onFailure(Call<List<AlbumList>> call, Throwable t) {
                getView().showAlbumList(null);
            }
        });
    }

    @Override
    public AlbumListContract.Model createModel() {
        return new AlbumListContract.Model() {
            @Override
            public Call<List<AlbumList>> getAlbumList(int offset, int limit) {
                Retrofit retrofit = createRetrofit(convertFactoryForAlbumList());
                AlbumApi api = retrofit.create(AlbumApi.class);
                return api.getAlbumList(offset, limit);
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
        };
    }
}
