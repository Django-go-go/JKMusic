package com.jkingone.jkmusic.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.api.AlbumApi;
import com.jkingone.jkmusic.entity.Album;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by jiangbo03 at 2018/8/31
 */
public class AlbumViewModel extends AndroidViewModel {
    private MutableLiveData<Album> mAlbumLiveData;

    public AlbumViewModel(@NonNull Application application) {
        super(application);
        mAlbumLiveData = new MutableLiveData<>();
    }

    public LiveData<Album> getAlbumLiveData() {
        return mAlbumLiveData;
    }

    private Retrofit createRetrofit(Converter.Factory factory) {
        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(factory)
                .build();
    }

    public void getAlbum(String albumId) {
        Retrofit retrofit = createRetrofit(convertFactoryForAlbum());
        AlbumApi albumApi = retrofit.create(AlbumApi.class);
        albumApi.getAlbumInfo(albumId).enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                mAlbumLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {
                mAlbumLiveData.postValue(null);
            }
        });
    }

    private Converter.Factory convertFactoryForAlbum() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, Album>() {
                    @Override
                    public Album convert(@NonNull ResponseBody value) throws IOException {
                        Gson gson = new Gson();

                        String s = value.string();

                        JsonObject jsonObject = new JsonParser().parse(s).getAsJsonObject();

                        return gson.fromJson(jsonObject, Album.class);
                    }
                };
            }
        };
    }
}
