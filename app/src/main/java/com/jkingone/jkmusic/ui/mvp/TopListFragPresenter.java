package com.jkingone.jkmusic.ui.mvp;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.api.TopListApi;
import com.jkingone.jkmusic.entity.TopList;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.mvp.contract.TopListFragContract;

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
 * Created by Administrator on 2017/8/3.
 */

public class TopListFragPresenter extends BasePresenter<TopListFragContract.ViewCallback, TopListFragContract.Model> {

    public TopListFragPresenter(TopListFragContract.ViewCallback view) {
        super();
        attachView(view);
    }

    public void loadData(){
        mModel.getTopList().enqueue(new Callback<List<TopList>>() {
            @Override
            public void onResponse(Call<List<TopList>> call, Response<List<TopList>> response) {
                getView().showView(response.body());
            }

            @Override
            public void onFailure(Call<List<TopList>> call, Throwable t) {
                getView().showView(null);
            }
        });
    }


    @Override
    public TopListFragContract.Model createModel() {
        return new TopListFragContract.Model() {
            @Override
            public Call<List<TopList>> getTopList() {
                Retrofit retrofit = createRetrofit(convertFactoryForTopList());
                TopListApi api = retrofit.create(TopListApi.class);
                return api.getTopList();
            }

            private Retrofit createRetrofit(Converter.Factory factory) {
                return new Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .addConverterFactory(factory)
                        .build();
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
                                for (JsonElement song : array) {
                                    if (song != null) {
                                        songs.add(gson.fromJson(song, TopList.class));
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
