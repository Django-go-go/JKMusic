package com.jkingone.jkmusic.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PageKeyedDataSource;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.NetWorkState;
import com.jkingone.jkmusic.api.ArtistApi;
import com.jkingone.jkmusic.entity.ArtistList;

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
public class ArtistListViewModel extends AndroidViewModel {

    private static final int PAGE_SIZE = 10;

    private LiveData<PagedList<ArtistList>> mArtistListLiveData;
    private MutableLiveData<NetWorkState> mNetWorkStateLiveData;
    private MutableLiveData<NetWorkState> mFootLoadLiveData;

    public ArtistListViewModel(@NonNull Application application) {
        super(application);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(true)
                .setInitialLoadSizeHint(PAGE_SIZE)
                .setPageSize(PAGE_SIZE)
                .build();
        mNetWorkStateLiveData = new MutableLiveData<>();
        mFootLoadLiveData = new MutableLiveData<>();

        mArtistListLiveData = new LivePagedListBuilder<>(new ArtistFactory(), config)
                .setBoundaryCallback(new PagedList.BoundaryCallback<ArtistList>() {
                    @Override
                    public void onZeroItemsLoaded() {
                        Log.i(Constant.TAG, "onZeroItemsLoaded: ");
                    }

                    @Override
                    public void onItemAtFrontLoaded(@NonNull ArtistList itemAtFront) {
                        Log.i(Constant.TAG, "onItemAtFrontLoaded: ");
                    }

                    @Override
                    public void onItemAtEndLoaded(@NonNull ArtistList itemAtEnd) {
                        Log.i(Constant.TAG, "onItemAtEndLoaded: ");
                    }
                }).build();
    }

    private int area;
    private int sex;
    private int order;
    private String abc;

    public void setParams(int area, int sex, int order, String abc) {
        this.area = area;
        this.sex = sex;
        this.order = order;
        this.abc = abc;
    }

    class ArtistFactory extends DataSource.Factory<Integer, ArtistList> {

        @Override
        public DataSource<Integer, ArtistList> create() {
            return new PageKeyedDataSource<Integer, ArtistList>() {
                @Override
                public void loadInitial(@NonNull LoadInitialParams<Integer> params,
                                        @NonNull LoadInitialCallback<Integer, ArtistList> callback) {
                    mNetWorkStateLiveData.postValue(NetWorkState.LOADING);
                    getArtistListInner(0, PAGE_SIZE, area, sex, order, abc)
                            .enqueue(new Callback<List<ArtistList>>() {
                                @Override
                                public void onResponse(Call<List<ArtistList>> call,
                                                       Response<List<ArtistList>> response) {
                                    if (response.isSuccessful()) {
                                        List<ArtistList> body = response.body();
                                        if (body != null) {
                                            callback.onResult(body, null, 1);
                                            mNetWorkStateLiveData.postValue(NetWorkState.SUCCESS);
                                        } else {
                                            mNetWorkStateLiveData.postValue(NetWorkState.NO_DATA);
                                        }
                                    } else {
                                        mNetWorkStateLiveData.postValue(NetWorkState.FAIL);
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<ArtistList>> call, Throwable t) {
                                    mNetWorkStateLiveData.postValue(NetWorkState.FAIL);
                                }
                            });

                }

                @Override
                public void loadBefore(@NonNull LoadParams<Integer> params,
                                       @NonNull LoadCallback<Integer, ArtistList> callback) {
                }

                @Override
                public void loadAfter(@NonNull LoadParams<Integer> params,
                                      @NonNull LoadCallback<Integer, ArtistList> callback) {
                    getArtistListInner(params.key * PAGE_SIZE, PAGE_SIZE, area, sex, order, abc)
                            .enqueue(new Callback<List<ArtistList>>() {
                                @Override
                                public void onResponse(Call<List<ArtistList>> call,
                                                       Response<List<ArtistList>> response) {
                                    if (response.isSuccessful()) {
                                        List<ArtistList> body = response.body();
                                        if (body != null) {
                                            callback.onResult(body, params.key + 1);
                                            mFootLoadLiveData.postValue(NetWorkState.SUCCESS);
                                        } else {
                                            mFootLoadLiveData.postValue(NetWorkState.NO_DATA);
                                        }
                                    } else {
                                        mFootLoadLiveData.postValue(NetWorkState.FAIL);
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<ArtistList>> call, Throwable t) {
                                    mFootLoadLiveData.postValue(NetWorkState.FAIL);
                                }
                            });

                }
            };
        }
    }

    public LiveData<NetWorkState> getFootLoadLiveData() {
        return mFootLoadLiveData;
    }

    public LiveData<NetWorkState> getNetWorkStateLiveData() {
        return mNetWorkStateLiveData;
    }

    public LiveData<PagedList<ArtistList>> getArtistListLiveData() {
        return mArtistListLiveData;
    }

    private Call<List<ArtistList>> getArtistListInner(int offset, int limit, int area,
                                                      int sex, int order, String abc) {
        Retrofit retrofit = createRetrofit(convertFactoryForArtistList());
        ArtistApi api = retrofit.create(ArtistApi.class);
        return api.getArtistList(offset, limit, area, sex, order, abc);
    }

    private Converter.Factory convertFactoryForArtistList() {
        return new Converter.Factory() {
            @Nullable
            @Override
            public Converter<ResponseBody, ?> responseBodyConverter(Type type, final Annotation[] annotations, Retrofit retrofit) {
                return (Converter<ResponseBody, List<ArtistList>>) value -> {
                    Gson gson = new Gson();
                    String s = value.string();

                    JsonArray array = new JsonParser().parse(s)
                            .getAsJsonObject().getAsJsonArray("artist");

                    List<ArtistList> artistLists = new ArrayList<>();
                    for (JsonElement song : array) {
                        if (song != null) {
                            artistLists.add(gson.fromJson(song, ArtistList.class));
                        }
                    }
                    return artistLists;
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
}
