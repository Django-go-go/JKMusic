package com.jkingone.jkmusic.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
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

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.Artist;
import com.jkingone.jkmusic.entity.ArtistList;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.jkmusic.viewmodels.ArtistVieModel;
import com.jkingone.ui.ContentLoadView;
import com.jkingone.ui.PagerSlidingTabStrip;
import com.jkingone.utils.ScreenUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ArtistDetailActivity extends BaseActivity {

    public static final String ARTIST_INFO = "详细信息";
    public static final String ARTIST_SONG = "歌曲";
    public static final int LIMIT = 30;

    private ArtistList mArtistList;

    private int mOffset = 0;

    private ContentLoadView mContentLoadView;
    private RecyclerView mRecyclerView;
    private View mViewInfo;

    @BindView(R.id.pager_strip)
    PagerSlidingTabStrip mPagerSlidingTabStrip;
    @BindView(R.id.viewpager)
    ViewPager mViewPager;
    @BindView(R.id.iv_cover)
    ImageView mImageViewCover;

    private ArtistVieModel mArtistVieModel;
    private Observer<Artist.ArtistInfo> mArtistInfoObserver = new Observer<Artist.ArtistInfo>() {
        @Override
        public void onChanged(@Nullable Artist.ArtistInfo artistInfo) {

        }
    };
    private Observer<List<Artist.Song>> mArtistSongsObserver = new Observer<List<Artist.Song>>() {
        @Override
        public void onChanged(@Nullable List<Artist.Song> songs) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_album_and_artist);

        ScreenUtils.setTranslucent(this);

        ButterKnife.bind(this);

        mArtistVieModel = ViewModelProviders.of(this).get(ArtistVieModel.class);
        mArtistVieModel.getArtistInfoLiveData().observe(this, mArtistInfoObserver);
        mArtistVieModel.getArtistSongsLiveData().observe(this, mArtistSongsObserver);
        mArtistVieModel.getArtistSong(mArtistList.tingUid, mArtistList.artistId, mOffset, LIMIT);
        mArtistVieModel.getArtistInfo(mArtistList.tingUid, mArtistList.artistId);
        mViewPager.setAdapter(new ViewPagerAdapter());
        mPagerSlidingTabStrip.setViewPager(mViewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mArtistVieModel.getArtistSongsLiveData().removeObserver(mArtistSongsObserver);
        mArtistVieModel.getArtistInfoLiveData().removeObserver(mArtistInfoObserver);
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
            if (position == 0) {
                    return ARTIST_SONG;
                }
                if (position == 1) {
                    return ARTIST_INFO;
                }


            return super.getPageTitle(position);
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            if (position == 0) {
                mContentLoadView = (ContentLoadView) LayoutInflater.from(ArtistDetailActivity.this)
                        .inflate(R.layout.common_root_none, container, false);
                mRecyclerView = mContentLoadView.findViewById(R.id.recycle_common);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(ArtistDetailActivity.this));
                container.addView(mContentLoadView);
                return mContentLoadView;
            }
            if (position == 1) {
                mViewInfo = LayoutInflater.from(ArtistDetailActivity.this)
                        .inflate(R.layout.activity_album_and_artist_info, container, false);
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


}
