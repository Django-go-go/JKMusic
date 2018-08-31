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
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.api.ArtistApi;
import com.jkingone.jkmusic.entity.Album;
import com.jkingone.jkmusic.entity.Artist;

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
public class ArtistVieModel extends AndroidViewModel {
    private MutableLiveData<Artist.ArtistInfo> mArtistInfoLiveData;
    private MutableLiveData<List<Artist.Song>> mArtistSongsLiveData;

    public ArtistVieModel(@NonNull Application application) {
        super(application);
        mArtistInfoLiveData = new MutableLiveData<>();
        mArtistSongsLiveData = new MutableLiveData<>();
    }

    public LiveData<Artist.ArtistInfo> getArtistInfoLiveData() {
        return mArtistInfoLiveData;
    }

    public LiveData<List<Artist.Song>> getArtistSongsLiveData() {
        return mArtistSongsLiveData;
    }

    public void getArtistInfo(String tingUid, String artistId) {
        Retrofit retrofit = createRetrofit(convertFactoryArtistInfo());
        ArtistApi artistApi = retrofit.create(ArtistApi.class);
        artistApi.getArtistInfo(tingUid, artistId).enqueue(new Callback<Artist.ArtistInfo>() {
            @Override
            public void onResponse(Call<Artist.ArtistInfo> call, Response<Artist.ArtistInfo> response) {
                mArtistInfoLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<Artist.ArtistInfo> call, Throwable t) {
                mArtistInfoLiveData.postValue(null);
            }
        });
    }

    public void getArtistSong(String tingUid, String artistId, int offset, int limit) {
        Retrofit retrofit = createRetrofit(convertFactoryArtistSong());
        ArtistApi artistApi = retrofit.create(ArtistApi.class);
        artistApi.getArtistSongList(tingUid, artistId, offset, limit).enqueue(new Callback<List<Artist.Song>>() {
            @Override
            public void onResponse(Call<List<Artist.Song>> call, Response<List<Artist.Song>> response) {
                mArtistSongsLiveData.postValue(response.body());
            }

            @Override
            public void onFailure(Call<List<Artist.Song>> call, Throwable t) {
                mArtistSongsLiveData.postValue(null);
            }
        });
    }

    private Retrofit createRetrofit(Converter.Factory factory) {
        return new Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(factory)
                .build();
    }

    private Converter.Factory convertFactoryArtistInfo() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, Object>() {
                    @Override
                    public Object convert(@NonNull ResponseBody value) throws IOException {

                        return new Gson().fromJson(value.toString(), Artist.ArtistInfo.class);

                    }
                };
            }
        };
    }

    private Converter.Factory convertFactoryArtistSong() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                return new Converter<ResponseBody, List<Artist.Song>>() {
                    @Override
                    public List<Artist.Song> convert(@NonNull ResponseBody value) throws IOException {
                        Gson gson = new Gson();
                        String res = value.string();
                        JsonArray array = new JsonParser().parse(res).getAsJsonObject().getAsJsonArray("songlist");
                        List<Artist.Song> songList = new ArrayList<>();
                        for (JsonElement jsonElement : array) {
                            songList.add(gson.fromJson(jsonElement, Artist.Song.class));
                        }
                        return songList;
                    }
                };
            }
        };
    }
}
