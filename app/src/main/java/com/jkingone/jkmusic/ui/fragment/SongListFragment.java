package com.jkingone.jkmusic.ui.fragment;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jkingone.jkmusic.ui.activity.DetailActivity;
import com.jkingone.jkmusic.ui.base.LazyFragment;
import com.jkingone.jkmusic.viewmodels.SongListViewModel;
import com.jkingone.utils.DensityUtils;
import com.jkingone.utils.ScreenUtils;
import com.jkingone.jkmusic.GlideApp;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.adapter.LoadMoreRecycleAdapter;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.ui.activity.ClassifySongListActivity;
import com.jkingone.ui.widget.ContentLoadView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017/8/6.
 */

public class SongListFragment extends LazyFragment implements Observer<List<SongList>> {

    @BindView(R.id.recycle_common)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_common)
    ContentLoadView mContentLoadView;

    private SongListAdapter mRecycleAdapter;

    private List<SongList> mSongLists = new ArrayList<>();

    private int offset = 1;
    public static final int SIZE = 20;

    private Unbinder mUnbinder;

    public static SongListFragment newInstance(String... params) {
        SongListFragment fragment = new SongListFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    private SongListViewModel mSongListViewModel;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSongListViewModel = ViewModelProviders.of(this).get(SongListViewModel.class);
        mSongListViewModel.getSongListLiveData().observe(this, this);
    }

    @Override
    protected void onLazyLoadOnce() {
        mSongListViewModel.getSongList(SIZE, offset);
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.common_root_none, container, false);
        view.setBackgroundColor(Color.WHITE);

        mUnbinder = ButterKnife.bind(this, view);

        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                GridLayoutManager manager = (GridLayoutManager) recyclerView.getLayoutManager();
                if (newState == RecyclerView.SCROLL_STATE_IDLE && manager.getChildCount() > 0) {
                    int y = manager.findLastCompletelyVisibleItemPosition();
                    if (y == mSongLists.size() + 1) {
                        mRecycleAdapter.mFootLoadView.postLoading();
                        mSongListViewModel.getSongList(SIZE, offset);
                    }
                }
            }
        });

        mContentLoadView.setLoadRetryListener(() -> mSongListViewModel.getSongList(SIZE, offset));

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSongListViewModel.getSongListLiveData().removeObserver(this);
    }

    @Override
    public void onChanged(List<SongList> songLists) {
        if (mSongLists.size() == 0) {
            if (songLists == null) {
                mContentLoadView.postLoadFail();
                return;
            }
            if (songLists.size() == 0) {
                mContentLoadView.postLoadNoData();
                return;
            }
            mContentLoadView.postLoadComplete();
        } else {
            if (songLists == null) {
                mRecycleAdapter.mFootLoadView.postLoadFail();
                return;
            }
            if (songLists.size() == 0) {
                mRecycleAdapter.mFootLoadView.postLoadNoData();
                return;
            }
        }

        mSongLists.addAll(songLists);

        if (offset == 1) {
            mRecycleAdapter = new SongListAdapter(getContext());
            mRecyclerView.setAdapter(mRecycleAdapter);
        } else {
            mRecycleAdapter.notifyDataSetChanged();
        }

        mRecycleAdapter.mFootLoadView.postLoadComplete();

        offset++;
    }

    class SongListAdapter extends LoadMoreRecycleAdapter {

        private static final int TYPE_CONTENT = 2;
        private static final int TYPE_HEAD = 1;

        private int col = 2;
        private int w;
        private int h;

        SongListAdapter(Context context) {
            super(context);
            w = ScreenUtils.getScreenWidth(mContext);
            h = DensityUtils.dp2px(mContext, 8) + DensityUtils.sp2px(mContext, 42);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {

            switch (viewType) {
                case TYPE_HEAD: {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.item_grid_songlist_head, parent, false);
                    view.setLayoutParams(
                            new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, DensityUtils.dp2px(mContext, 150)));
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(getContext(), ClassifySongListActivity.class));
                        }
                    });
                    return new HeadViewHolder(view);
                }

                case TYPE_CONTENT: {
                    View convertView = LayoutInflater.from(mContext).inflate(R.layout.item_universal, parent, false);
                    return new ContentViewHolder(convertView);
                }

                default:
                    throw new IllegalArgumentException("no Type");
            }
        }

        @Override
        public int getItemContentCount() {
            return mSongLists.size() + 1;
        }

        @Override
        public int getItemContentViewType(int position) {
            if (position == 0) {
                return TYPE_HEAD;
            }
            return TYPE_CONTENT;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

            if (holder instanceof ContentViewHolder) {

                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;
                final SongList songList = mSongLists.get(position - 1);

                contentViewHolder.mTextView.setText(songList.getTitle());



                if (Utils.checkStringNotNull(songList.getPic300())) {
                    GlideApp.with(SongListFragment.this)
                            .asBitmap()
                            .override(w / col, w / col)
                            .load(songList.getPic300())
                            .into(contentViewHolder.mImageView);
                } else if (Utils.checkStringNotNull(songList.getPic())) {
                    GlideApp.with(SongListFragment.this)
                            .asBitmap()
                            .override(w / col, w / col)
                            .load(songList.getPic())
                            .into(contentViewHolder.mImageView);
                }

                contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), DetailActivity.class);
                        intent.putExtra(DetailActivity.TYPE_SONG_LIST, songList);
                        startActivity(intent);
                    }
                });

            }
        }

        @Override
        public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
            RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
            if (manager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager) manager;
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        if (getItemViewType(position) != TYPE_CONTENT) {
                            return 2;
                        }
                        return 1;
                    }
                });
            }
        }

        class HeadViewHolder extends RecyclerView.ViewHolder {

            HeadViewHolder(View itemView) {
                super(itemView);
            }
        }

        class ContentViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_item)
            ImageView mImageView;
            @BindView(R.id.tv_item)
            TextView mTextView;

            ContentViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, h));
                itemView.setLayoutParams(new RecyclerView.LayoutParams(w / 2, w / 2 + h));
            }
        }
    }
}
