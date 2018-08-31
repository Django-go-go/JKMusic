package com.jkingone.jkmusic.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.jkingone.jkmusic.viewmodels.SongInfoViewModel;
import com.jkingone.utils.DensityUtils;
import com.jkingone.utils.ScreenUtils;
import com.jkingone.jkmusic.GlideApp;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.entity.TopList;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.ui.widget.ContentLoadView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends BaseActivity {

    public static final String TYPE_SONG_LIST = "song_list";
    public static final String TYPE_TOP_LIST = "top_list";

    private String mType;

    private List<SongInfo> mSongInfos = new ArrayList<>();

    private TopList mTopList;
    private SongList mSongList;

    private SongAdapter mSongAdapter;

    private Drawable mToolbarDrawable;

    private int mHeight = 0;

    @BindView(R.id.toolbar_common)
    Toolbar mToolbar;

    @BindView(R.id.recycle_common)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_common)
    ContentLoadView mContentLoadView;

    private SongInfoViewModel mSongInfoViewModel;
    private Observer<List<SongInfo>> mSongObserver = new Observer<List<SongInfo>>() {
        @Override
        public void onChanged(@Nullable List<SongInfo> songInfos) {
            if (songInfos == null) {
                mContentLoadView.postLoadFail();
                return;
            }
            if (songInfos.size() == 0) {
                mContentLoadView.postLoadNoData();
                return;
            }
            mSongInfos.clear();
            mSongInfos.addAll(songInfos);
            mContentLoadView.postLoadComplete();
            if (mSongAdapter != null) {
                mSongAdapter.notifyDataSetChanged();
            } else {
                mSongAdapter = new SongAdapter();
                mRecyclerView.setAdapter(mSongAdapter);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_root_overlay);
        ScreenUtils.setTranslucent(this);
        ButterKnife.bind(this);

        mSongInfoViewModel = ViewModelProviders.of(this).get(SongInfoViewModel.class);
        mSongInfoViewModel.getSongLiveData().observe(this, mSongObserver);

        mContentLoadView.setLoadRetryListener(new ContentLoadView.LoadRetryListener() {
            @Override
            public void onRetry() {
                if (TYPE_SONG_LIST.equals(mType)) {
                    mSongInfoViewModel.getSongFromSongList(mSongList.getListId());
                    return;
                }
                if (TYPE_TOP_LIST.equals(mType)) {
                    mSongInfoViewModel.getSongFromTopList(Integer.parseInt(mTopList.getType()));
                }
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            mSongList = intent.getParcelableExtra(TYPE_SONG_LIST);
            if (mSongList != null) {
                mType = TYPE_SONG_LIST;
                mSongInfoViewModel.getSongFromSongList(mSongList.getListId());
                mToolbar.setTitle("歌单");
            }

            mTopList = intent.getParcelableExtra(TYPE_TOP_LIST);
            if (mTopList != null){
                mType = TYPE_TOP_LIST;
                mSongInfoViewModel.getSongFromTopList(Integer.parseInt(mTopList.getType()));
                mToolbar.setTitle("榜单");
            }
        }

        setSupportActionBar(mToolbar);
        mToolbar.bringToFront();
        mToolbar.setBackground(null);

        mHeight = DensityUtils.dp2px(this, 160);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSongAdapter = new SongAdapter();
        mRecyclerView.setAdapter(mSongAdapter);

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int scrollY = 0;
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                scrollY += dy;
                if (mToolbarDrawable != null) {
                    if (scrollY >= 0 && scrollY <= mHeight) {
                        int alpha = scrollY * 255 / mHeight;
                        mToolbarDrawable.setAlpha(alpha);
                    } else {
                        mToolbarDrawable.setAlpha(255);
                    }
                }
            }

        });
    }

    class SongAdapter extends RecyclerView.Adapter {

        private static final int TYPE_HEAD = 1;
        private static final int TYPE_CONTENT = 2;

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            if (viewType == TYPE_HEAD) {
                return new HeadViewHolder(LayoutInflater.from(DetailActivity.this).inflate(R.layout.activity_songlist_or_toplist_head, parent, false));
            }
            if (viewType == TYPE_CONTENT) {
                return new SongViewHolder(LayoutInflater.from(DetailActivity.this).inflate(R.layout.item_list_song, parent, false));
            }
            throw new IllegalStateException("no type");
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof HeadViewHolder) {
                final HeadViewHolder headViewHolder = (HeadViewHolder) holder;
                if (TYPE_SONG_LIST.equals(mType)) {
                    bindSongList(headViewHolder);
                    return;
                }
                if (TYPE_TOP_LIST.equals(mType)) {
                    bindTopList(headViewHolder);
                }
                return;
            }

            if (holder instanceof SongViewHolder) {
                position -= 1;
                SongInfo songInfo = mSongInfos.get(position);
                SongViewHolder songViewHolder = (SongViewHolder) holder;
                songViewHolder.tv_singer.setText(songInfo.getArtist());
                songViewHolder.tv_songName.setText(songInfo.getTitle());
                songViewHolder.tv_position.setText(String.valueOf(position + 1));
                songViewHolder.iv_action.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                songViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                return TYPE_HEAD;
            }
            return TYPE_CONTENT;
        }

        @Override
        public int getItemCount() {
            return mSongInfos.size() + 1;
        }

        private void bindSongList(final HeadViewHolder headViewHolder) {

            GlideApp.with(DetailActivity.this)
                    .asBitmap()
                    .load(mSongList.getPic300())
                    .override(DensityUtils.dp2px(DetailActivity.this, 128))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            headViewHolder.mImageViewCover.setImageBitmap(bitmap);

                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@NonNull Palette palette) {
                                    int color = palette.getMutedColor(Color.LTGRAY);
                                    if (color == Color.LTGRAY) {
                                        color = palette.getVibrantColor(Color.LTGRAY);
                                    }
                                    headViewHolder.itemView.setBackgroundColor(color);
                                    mToolbarDrawable = new ColorDrawable(color);
                                    mToolbar.setBackground(mToolbarDrawable);
                                }
                            });
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            headViewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                            headViewHolder.mImageViewCover.setImageResource(R.drawable.music_large);
                            mToolbarDrawable = new ColorDrawable(Color.LTGRAY);
                            mToolbar.setBackground(mToolbarDrawable);
                        }
                    });

            headViewHolder.mTextViewName.setText(mSongList.getTitle());
            headViewHolder.mTextViewTitle.setText(mSongList.getTag());
            headViewHolder.mTextViewComment.setText(mSongList.getDesc());
        }

        private void bindTopList(final HeadViewHolder headViewHolder) {
            GlideApp.with(DetailActivity.this)
                    .asBitmap()
                    .load(mTopList.getPicS192())
                    .override(DensityUtils.dp2px(DetailActivity.this, 128))
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition) {
                            headViewHolder.mImageViewCover.setImageBitmap(bitmap);

                            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                @Override
                                public void onGenerated(@NonNull Palette palette) {
                                    int color = palette.getMutedColor(Color.LTGRAY);
                                    if (color == Color.LTGRAY) {
                                        color = palette.getVibrantColor(Color.LTGRAY);
                                    }
                                    headViewHolder.itemView.setBackgroundColor(color);
                                    mToolbarDrawable = new ColorDrawable(color);
                                    mToolbar.setBackground(mToolbarDrawable);
                                }
                            });
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            headViewHolder.itemView.setBackgroundColor(Color.LTGRAY);
                            headViewHolder.mImageViewCover.setImageResource(R.drawable.music_large);
                            mToolbarDrawable = new ColorDrawable(Color.LTGRAY);
                            mToolbar.setBackground(mToolbarDrawable);
                        }
                    });

            headViewHolder.mTextViewName.setText(mTopList.getName());
            headViewHolder.mTextViewComment.setText(mTopList.getComment());
        }

        class HeadViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_coverimage)
            ImageView mImageViewCover;
            @BindView(R.id.tv_title)
            TextView mTextViewTitle;
            @BindView(R.id.tv_name)
            TextView mTextViewName;
            @BindView(R.id.tv_comment)
            TextView mTextViewComment;

            HeadViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }

        class SongViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_song_name)
            TextView tv_songName;
            @BindView(R.id.tv_singer)
            TextView tv_singer;
            @BindView(R.id.iv_action)
            ImageView iv_action;
            @BindView(R.id.tv_pos)
            TextView tv_position;

            SongViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                tv_position.setVisibility(View.VISIBLE);
                tv_singer.setVisibility(View.VISIBLE);
                tv_songName.setVisibility(View.VISIBLE);
                iv_action.setVisibility(View.VISIBLE);
            }
        }
    }

}
