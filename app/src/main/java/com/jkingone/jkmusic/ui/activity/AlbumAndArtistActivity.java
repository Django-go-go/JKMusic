package com.jkingone.jkmusic.ui.activity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.common.utils.LogUtils;
import com.jkingone.common.utils.ScreenUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.adapter.LoadMoreRecycleAdapter;
import com.jkingone.jkmusic.entity.Album;
import com.jkingone.jkmusic.entity.AlbumList;
import com.jkingone.jkmusic.entity.Artist;
import com.jkingone.jkmusic.entity.ArtistList;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.jkmusic.ui.mvp.presenter.AlbumAndArtistPresenter;
import com.jkingone.jkmusic.ui.mvp.contract.AlbumAndArtistContract;
import com.jkingone.ui.widget.ContentLoadView;
import com.jkingone.ui.widget.PagerSlidingTabStrip;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AlbumAndArtistActivity extends BaseActivity<AlbumAndArtistPresenter> implements AlbumAndArtistContract.ViewCallback {


    public static final String TYPE_ALBUM = "album";
    public static final String TYPE_ARTIST = "artist";
    public static final String ALBUM_INFO = "专辑信息";
    public static final String ALBUM_SONG = "专辑歌曲";
    public static final String ARTIST_INFO = "详细信息";
    public static final String ARTIST_SONG = "歌曲";
    public static final int LIMIT = 30;

    private AlbumList mAlbumList;
    private ArtistList mArtistList;

    private int mOffset = 0;
    private String mType;

    private List<SongInfo> mSongs = new ArrayList<>();

    private ContentLoadView mContentLoadView;
    private RecyclerView mRecyclerView;
    private View mViewInfo;

//    @BindView(R.id.app_bar)
//    AppBarLayout mAppBarLayout;
    @BindView(R.id.pager_strip)
    PagerSlidingTabStrip mPagerSlidingTabStrip;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.iv_cover)
    ImageView mImageViewCover;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_and_artist);

        ScreenUtils.setTranslucent(this);

        ButterKnife.bind(this);

        if (getIntent() != null) {
            if ((mAlbumList = getIntent().getParcelableExtra(TYPE_ALBUM)) != null) {
                mType = TYPE_ALBUM;
                mPresenter.loadAlbum(mAlbumList.getAlbumId());
                mViewPager.setAdapter(new ViewPagerAdapter());
                mPagerSlidingTabStrip.setViewPager(mViewPager);
                return;
            }

            if ((mArtistList = getIntent().getParcelableExtra(TYPE_ARTIST)) != null) {
                mType = TYPE_ARTIST;
                mPresenter.loadArtistSong(mArtistList.getTingUid(), mArtistList.getArtistId(), mOffset, LIMIT);
                mPresenter.loadArtistInfo(mArtistList.getTingUid(), mArtistList.getArtistId());
                mViewPager.setAdapter(new ViewPagerAdapter());
                mPagerSlidingTabStrip.setViewPager(mViewPager);
            }
        }

    }

    @Override
    public AlbumAndArtistPresenter createPresenter() {
        return new AlbumAndArtistPresenter(this);
    }

    @Override
    public void showAlbum(Album album) {
        if (album == null) {
            mContentLoadView.postLoadFail();
            return;
        }
        Album.AlbumInfo albumInfo = album.getAlbumInfo();
        if (Utils.checkStringNotNull(albumInfo.getPicS1000())) {
//            Picasso.get()
//                    .load(album.getAlbumInfo().getPicS1000())
//                    .resize(ScreenUtils.getScreenWidth(this), DensityUtils.dp2px(this, 400))
//                    .centerCrop()
//                    .into(mImageViewCover);
//                    .into(new Target() {
//                        @Override
//                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                            mAppBarLayout.setBackground(new BitmapDrawable(AlbumAndArtistActivity.this.getResources(), bitmap));
//                        }
//
//                        @Override
//                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//
//                        }
//
//                        @Override
//                        public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//                        }
//                    });
        }

        List<SongInfo> songs = Utils.AlbumSongToSongInfo(album.getSongList());

        if (songs == null || songs.size() == 0) {
            mContentLoadView.postLoadNoData();
            return;
        }

        mSongs.clear();
        mSongs.addAll(songs);

        mRecyclerView.setAdapter(new SongAdapter(this));
        mContentLoadView.postLoadComplete();
    }

    @Override
    public void showArtistInfo(Artist.ArtistInfo artistInfo) {
        LogUtils.i("showArtist" + artistInfo);

        if (Utils.checkStringNotNull(artistInfo.getAvatarS1000())) {
//            Picasso.get().load(artistInfo.getAvatarS1000())
//                    .resize(ScreenUtils.getScreenWidth(this), DensityUtils.dp2px(this, 400))
//                    .centerCrop()
//                    .into(mImageViewCover);
        }
    }

    @Override
    public void showArtistSong(List<Artist.Song> songlist) {
        LogUtils.i("showArtistSong" + songlist);
    }

    class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            if (TYPE_ALBUM.equals(mType)) {
                if (position == 0) {
                    return ALBUM_SONG;
                }
                if (position == 1) {
                    return ALBUM_INFO;
                }
            }
            if (TYPE_ARTIST.equals(mType)) {
                if (position == 0) {
                    return ARTIST_SONG;
                }
                if (position == 1) {
                    return ALBUM_INFO;
                }
            }

            return super.getPageTitle(position);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (position == 0) {
                mContentLoadView = (ContentLoadView) LayoutInflater.from(AlbumAndArtistActivity.this).inflate(R.layout.common_root_none, container, false);
                mRecyclerView = mContentLoadView.findViewById(R.id.recycle_common);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(AlbumAndArtistActivity.this));
                container.addView(mContentLoadView);
                return mContentLoadView;
            }
            if (position == 1) {
                mViewInfo = LayoutInflater.from(AlbumAndArtistActivity.this).inflate(R.layout.activity_album_and_artist_info, container, false);
                container.addView(mViewInfo);
                return mViewInfo;
            }
            throw new IndexOutOfBoundsException("position is more");
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
        }
    }

    class SongAdapter extends LoadMoreRecycleAdapter {

        private static final int TYPE_HEAD = 1;
        private static final int TYPE_CONTENT = 2;

        SongAdapter(Context context) {
            super(context);
        }

        @Override
        public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
            switch (viewType) {
                case TYPE_HEAD: {
                    View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_song_head, parent, false);
                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    view.findViewById(R.id.tv_collect).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    return new HeadViewHolder(view);
                }
                case TYPE_CONTENT: {
                    return new SongViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_list_song, parent, false));
                }

                default:
                    throw new IllegalArgumentException("no type");
            }
        }

        @Override
        public int getItemContentCount() {
            return mSongs.size() + 1;
        }

        @Override
        public int getItemContentViewType(int position) {
            if (position == 0) {
                return TYPE_HEAD;
            }
            return TYPE_CONTENT;
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof SongViewHolder) {
                position -= 1;
                SongViewHolder songViewHolder = (SongViewHolder) holder;
                SongInfo songInfo = mSongs.get(position);
                songViewHolder.tv_singer.setText(songInfo.getArtist());
                songViewHolder.tv_songName.setText(songInfo.getTitle());
                songViewHolder.tv_position.setText(String.valueOf(position + 1));
                songViewHolder.iv_action.setOnClickListener(new View.OnClickListener() {
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

        }

        class HeadViewHolder extends RecyclerView.ViewHolder {

            HeadViewHolder(View itemView) {
                super(itemView);
            }
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
