package com.jkingone.jkmusic.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.GlideApp;
import com.jkingone.jkmusic.NetWorkState;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.api.ArtistApi;
import com.jkingone.jkmusic.entity.ArtistList;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.jkmusic.ui.fragment.ArtistListFragment;
import com.jkingone.jkmusic.viewmodels.ArtistListViewModel;
import com.jkingone.ui.ContentLoadView;
import com.jkingone.utils.DensityUtils;
import com.jkingone.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistListActivity extends BaseActivity {

    @BindView(R.id.content_common)
    ContentLoadView mContentLoadView;
    @BindView(R.id.recycle_common)
    RecyclerView mRecyclerView;
    @BindView(R.id.toolbar_common)
    Toolbar mToolbar;

    private ArtistListAdapter mArtistAdapter;

    private ArtistListViewModel mArtistListViewModel;
    private Observer<PagedList<ArtistList>> mArtistListObserver = artistLists -> {
        mArtistAdapter.submitList(artistLists);
    };

    private Observer<NetWorkState> mNetWorkStateObserver = netWorkState -> {
        if (netWorkState == NetWorkState.FAIL) {
            mContentLoadView.postLoadFail();
        } else if (netWorkState == NetWorkState.NO_DATA) {
            mContentLoadView.postLoadNoData();
        } else if (netWorkState == NetWorkState.SUCCESS) {
            mContentLoadView.postLoadComplete();
        }

    };

    private Observer<NetWorkState> mFootLoadObserver = netWorkState -> {
        if (netWorkState == NetWorkState.FAIL) {
            Toast.makeText(ArtistListActivity.this,
                    "加载失败", Toast.LENGTH_SHORT).show();
        } else if (netWorkState == NetWorkState.NO_DATA) {
            Toast.makeText(ArtistListActivity.this,
                    "没有数据", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_root_normal);
        ScreenUtils.setTranslucent(this);
        ButterKnife.bind(this);

        int area = getIntent().getIntExtra(ArtistListFragment.ARTIST_AREA, ArtistApi.AREA_ALL);
        int sex = getIntent().getIntExtra(ArtistListFragment.ARTIST_SEX, ArtistApi.SEX_NONE);

        Log.i(Constant.TAG, "onCreate: " + area + " " + sex);

        mToolbar.setTitle("歌手");
        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mArtistAdapter = new ArtistListAdapter();
        mRecyclerView.setAdapter(mArtistAdapter);
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int px = DensityUtils.dp2px(ArtistListActivity.this, 4);
                outRect.set(px, px, px, px);
            }
        });

        mArtistListViewModel = ViewModelProviders.of(this).get(ArtistListViewModel.class);
        mArtistListViewModel.setParams(area, sex, ArtistApi.ORDER_HOT, null);

        mArtistListViewModel.getArtistListLiveData().observe(this, mArtistListObserver);
        mArtistListViewModel.getFootLoadLiveData().observe(this, mFootLoadObserver);
        mArtistListViewModel.getNetWorkStateLiveData().observe(this, mNetWorkStateObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mArtistListViewModel.getArtistListLiveData().removeObserver(mArtistListObserver);
        mArtistListViewModel.getFootLoadLiveData().removeObserver(mFootLoadObserver);
        mArtistListViewModel.getNetWorkStateLiveData().removeObserver(mNetWorkStateObserver);
    }


    class ArtistListAdapter extends PagedListAdapter<ArtistList, ArtistListAdapter.ArtistViewHolder> {

        ArtistListAdapter() {
            super(new DiffUtil.ItemCallback<ArtistList>() {
                @Override
                public boolean areItemsTheSame(@NonNull ArtistList artistList, @NonNull ArtistList newArtist) {
                    return artistList.artistId.equals(newArtist.artistId);
                }

                @Override
                public boolean areContentsTheSame(@NonNull ArtistList artistList, @NonNull ArtistList newArtist) {
                    return artistList.equals(newArtist);
                }
            });
        }

        @NonNull
        @Override
        public ArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ArtistViewHolder(LayoutInflater.from(ArtistListActivity.this)
                    .inflate(R.layout.item_list_artist_classify, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ArtistViewHolder holder, int position) {
            ArtistList artistList = getItem(position);
            if (artistList != null) {
                holder.mTextView.setText(artistList.name);
                GlideApp.with(ArtistListActivity.this)
                        .asBitmap()
                        .load(artistList.avatarBig)
                        .override(DensityUtils.dp2px(getApplicationContext(), 64))
                        .into(holder.mImageView);
            }
        }

        class ArtistViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_item)
            ImageView mImageView;
            @BindView(R.id.tv_item)
            TextView mTextView;

            ArtistViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
