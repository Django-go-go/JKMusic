package com.jkingone.jkmusic.ui.mvp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.api.ArtistApi;
import com.jkingone.jkmusic.entity.ArtistList;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.mvp.contract.ArtistListContract;

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

public class ArtistListPresenter extends BasePresenter<ArtistListContract.ViewCallback, ArtistListContract.Model> {

    public ArtistListPresenter(ArtistListContract.ViewCallback viewCallback) {
        super();
        attachView(viewCallback);
    }

    public void getArtistList(int offset, int limit, int area, int sex, int order) {
        mModel.getArtistList(offset, limit, area, sex, order, "").enqueue(new Callback<List<ArtistList>>() {
            @Override
            public void onResponse(Call<List<ArtistList>> call, Response<List<ArtistList>> response) {
                getView().showArtistList(response.body());
            }

            @Override
            public void onFailure(Call<List<ArtistList>> call, Throwable t) {
                getView().showArtistList(null);
            }
        });
    }

    public void getArtistList(int offset, int limit, int area, int sex, int order, String abc) {
        mModel.getArtistList(offset, limit, area, sex, order, abc).enqueue(new Callback<List<ArtistList>>() {
            @Override
            public void onResponse(Call<List<ArtistList>> call, Response<List<ArtistList>> response) {
                getView().showArtistList(response.body());
            }

            @Override
            public void onFailure(Call<List<ArtistList>> call, Throwable t) {

            }
        });
    }

    @Override
    public ArtistListContract.Model createModel() {
        return new ArtistListContract.Model() {

            @Override
            public Call<List<ArtistList>> getArtistList(int offset, int limit, int area, int sex, int order, String abc) {
                Retrofit retrofit = createRetrofit(convertFactoryForArtistList());
                ArtistApi api = retrofit.create(ArtistApi.class);
                return api.getArtistList(offset, limit, area, sex, order, abc);
            }

            private Converter.Factory convertFactoryForArtistList() {
                return new Converter.Factory() {
                    @Nullable @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                        return new Converter<ResponseBody, List<ArtistList>>() {
                            @Override
                            public List<ArtistList> convert(@NonNull ResponseBody value) throws IOException {
                                Gson gson = new Gson();
                                String s = value.string();

                                JsonArray array = new JsonParser().parse(s)
                                        .getAsJsonObject()
                                        .getAsJsonArray("artist");

                                List<ArtistList> artistLists = new ArrayList<>();
                                for (JsonElement song : array) {
                                    if (song != null) {
                                        artistLists.add(gson.fromJson(song, ArtistList.class));
                                    }
                                }
                                return artistLists;
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
