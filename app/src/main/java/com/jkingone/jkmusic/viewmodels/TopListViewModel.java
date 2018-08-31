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
import com.jkingone.jkmusic.api.TopListApi;
import com.jkingone.jkmusic.entity.TopList;

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
public class TopListViewModel extends AndroidViewModel {
    private MutableLiveData<List<TopList>> mTopListLiveData;

    public TopListViewModel(@NonNull Application application) {
        super(application);
        mTopListLiveData = new MutableLiveData<>();
    }

    public LiveData<List<TopList>> getTopListLiveData() {
        return mTopListLiveData;
    }

    public void getTopList() {
        Retrofit retrofit = createRetrofit(convertFactoryForTopList());
        TopListApi api = retrofit.create(TopListApi.class);
        api.getTopList().enqueue(new Callback<List<TopList>>() {
            @Override
            public void onResponse(Call<List<TopList>> call, Response<List<TopList>> response) {
                mTopListLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<TopList>> call, Throwable t) {
                mTopListLiveData.postValue(null);
            }
        });
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
}
