package com.jkingone.jkmusic.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.common.Utils.DensityUtils;
import com.jkingone.common.Utils.ScreenUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.adapter.HeadAndFootRecycleAdapter;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.entity.SongList;
import com.jkingone.jkmusic.entity.TopList;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.jkmusic.ui.mvp.contract.SongAndTopListContract;
import com.jkingone.jkmusic.ui.mvp.SongAndTopListPresenter;
import com.jkingone.ui.customview.ContentLoadView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SongAndTopListActivity extends BaseActivity<SongAndTopListPresenter> implements SongAndTopListContract.ViewCallback {

    private static final String TAG = "SongAndTopListActivity";

    public static final String TYPE_SONG_LIST = "song_list";
    public static final String TYPE_TOP_LIST = "top_list";

    private List<SongInfo> mMp3Infos = new ArrayList<>();

    private TopList mTopList;
    private SongList mSongList;

    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_coverimage)
    ImageView mImageViewCover;
    @BindView(R.id.tv_title)
    TextView mTextViewTitle;
    @BindView(R.id.tv_name)
    TextView mTextViewName;
    @BindView(R.id.tv_comment)
    TextView mTextViewComment;

    @BindView(R.id.recycle_universal)
    RecyclerView mRecyclerView;
    @BindView(R.id.content_universal)
    ContentLoadView mContentLoadView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlist_or_toplist);
        ScreenUtils.setTranslucent(this);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                Log.i(TAG, "onOffsetChanged: " + verticalOffset);
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            mSongList = intent.getParcelableExtra(TYPE_SONG_LIST);
            if (mSongList != null) {
                initSongList();
            }

            mTopList = intent.getParcelableExtra(TYPE_TOP_LIST);
            if (mTopList != null){
                initTopList();
            }
        }

        mContentLoadView.setLoadRetryListener(new ContentLoadView.LoadRetryListener() {
            @Override
            public void onRetry() {
                if (mSongList != null) {
                    mPresenter.getSongFromSongList(mSongList.getListId());
                }
                if (mTopList != null) {
                    mPresenter.getSongFromTopList(Integer.parseInt(mTopList.getType()));
                }
            }
        });
    }

    @Override
    public SongAndTopListPresenter createPresenter() {
        return new SongAndTopListPresenter(this);
    }

    private void initTopList() {
        mPresenter.getSongFromTopList(Integer.parseInt(mTopList.getType()));
        if (Utils.checkStringNotNull(mTopList.getPicS192())) {
            Picasso.get()
                    .load(mTopList.getPicS192())
                    .resize(DensityUtils.dp2px(this, 128), DensityUtils.dp2px(this, 128))
                    .centerCrop()
                    .into(mImageViewCover);
            Picasso.get()
                    .load(mTopList.getPicS192())
                    .resize(DensityUtils.dp2px(this, 128), DensityUtils.dp2px(this, 128))
                    .centerCrop()
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
        }

        mTextViewName.setText(mTopList.getName());
        mTextViewComment.setText(mTopList.getComment());
    }

    private void initSongList() {
        mPresenter.getSongFromSongList(mSongList.getListId());
        if (Utils.checkStringNotNull(mSongList.getPic300())) {
            Picasso.get()
                    .load(mSongList.getPic300())
                    .resize(DensityUtils.dp2px(this, 128), DensityUtils.dp2px(this, 128))
                    .centerCrop()
                    .into(mImageViewCover);
        }

        mTextViewName.setText(mSongList.getTitle());
        mTextViewTitle.setText(mSongList.getTag());
        mTextViewComment.setText(mSongList.getDesc());
    }

    @Override
    public void showView(List<SongInfo> songInfos) {
        if (songInfos == null) {
            mContentLoadView.postLoadFail();
            return;
        }
        if (songInfos.size() == 0) {
            mContentLoadView.postLoadNoData();
            return;
        }
        mContentLoadView.postLoadComplete();
        mRecyclerView.setAdapter(new SongAdapter(this, songInfos));
    }

    class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

        private Context mContext;
        private List<SongInfo> mSongs;

        SongAdapter(Context context, List<SongInfo> songs) {
            mContext = context;
            mSongs = songs;
        }

        @NonNull
        @Override
        public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SongViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_songinfo, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
            SongInfo songInfo = mSongs.get(position);
            holder.tv_singer.setText(songInfo.getArtist());
            holder.tv_songName.setText(songInfo.getTitle());
            holder.tv_position.setText(String.valueOf(position + 1));
            holder.iv_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return mSongs.size();
        }

        class SongViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tv_songName)
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
