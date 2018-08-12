package com.jkingone.jkmusic.ui.mvp.presenter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.jkingone.utils.LogUtils;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.api.AlbumApi;
import com.jkingone.jkmusic.api.ArtistApi;
import com.jkingone.jkmusic.entity.Album;
import com.jkingone.jkmusic.entity.Artist;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.mvp.contract.AlbumAndArtistContract;

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

public class AlbumAndArtistPresenter extends BasePresenter<AlbumAndArtistContract.ViewCallback, AlbumAndArtistContract.Model> {

    public AlbumAndArtistPresenter(AlbumAndArtistContract.ViewCallback viewCallback) {
        super();
        attachView(viewCallback);
    }

    public void loadAlbum(String albumId) {
        mModel.getAlbum(albumId).enqueue(new Callback<Album>() {
            @Override
            public void onResponse(Call<Album> call, Response<Album> response) {
                getView().showAlbum(response.body());
            }

            @Override
            public void onFailure(Call<Album> call, Throwable t) {
                LogUtils.e(t);
                getView().showAlbum(null);
            }
        });
    }

    public void loadArtistInfo(String tingUid, String artistId) {
        mModel.getArtistInfo(tingUid, artistId).enqueue(new Callback<Artist.ArtistInfo>() {
            @Override
            public void onResponse(Call<Artist.ArtistInfo> call, Response<Artist.ArtistInfo> response) {
                getView().showArtistInfo(response.body());
            }

            @Override
            public void onFailure(Call<Artist.ArtistInfo> call, Throwable t) {
                getView().showArtistInfo(null);
            }
        });
    }

    public void loadArtistSong(String tingUid, String artistId, int offset, int limit) {
        mModel.getArtistSong(tingUid, artistId, offset, limit).enqueue(new Callback<List<Artist.Song>>() {
            @Override
            public void onResponse(Call<List<Artist.Song>> call, Response<List<Artist.Song>> response) {
                getView().showArtistSong(response.body());
            }

            @Override
            public void onFailure(Call<List<Artist.Song>> call, Throwable t) {
                getView().showArtistSong(null);
            }
        });
    }

    @Override
    protected AlbumAndArtistContract.Model createModel() {
        return new AlbumAndArtistContract.Model() {
            @Override
            public Call<Album> getAlbum(String albumId) {
                Retrofit retrofit = createRetrofit(convertFactoryForAlbum());
                AlbumApi albumApi = retrofit.create(AlbumApi.class);
                return albumApi.getAlbumInfo(albumId);
            }

            @Override
            public Call<Artist.ArtistInfo> getArtistInfo(String tingUid, String artistId) {
                Retrofit retrofit = createRetrofit(convertFactoryArtistInfo());
                ArtistApi artistApi = retrofit.create(ArtistApi.class);
                return artistApi.getArtistInfo(tingUid, artistId);
            }

            @Override
            public Call<List<Artist.Song>> getArtistSong(String tingUid, String artistId, int offset, int limit) {
                Retrofit retrofit = createRetrofit(convertFactoryArtistSong());
                ArtistApi artistApi = retrofit.create(ArtistApi.class);
                return artistApi.getArtistSongList(tingUid, artistId, offset, limit);
            }

            private Retrofit createRetrofit(Converter.Factory factory) {
                return new Retrofit.Builder()
                        .baseUrl(Constant.BASE_URL)
                        .addConverterFactory(factory)
                        .build();
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
        };
    }
}
