package com.jkingone.jkmusic.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.jkingone.commonlib.PicassoTransform;
import com.jkingone.commonlib.Utils.ScreenUtils;
import com.jkingone.commonlib.Utils.TimeUtils;
import com.jkingone.customviewlib.PicassoBackground;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.MusicBroadcastReceiver;
import com.jkingone.jkmusic.MusicManagerService;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.data.entity.SongInfo;
import com.jkingone.jkmusic.service.MusicService;
import com.jkingone.jkmusic.ui.fragment.PlayFragment;
import com.jkingone.jkmusic.ui.mvp.BasePresenter;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayActivity extends BaseActivity {

    private static final String TAG = "PlayActivity";

    @BindView(R.id.root)
    PicassoBackground mViewRoot;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.pager)
    ViewPager mViewPager;

    @BindView(R.id.tv_start)
    TextView mTextViewStart;
    @BindView(R.id.tv_total)
    TextView mTextViewTotal;
    @BindView(R.id.seekbar)
    SeekBar mSeekBar;

    private List<View> mPagerViews = new ArrayList<>(6);
    private ImageView mImageViewAlbum;

    private List<SongInfo> mSongInfos;
    private SongInfo mCurSongInfo;
    private int mCurIndex;

    private ScheduledExecutorService mSeekBarExecutorService;
    private ScheduledFuture<?> mScheduledFuture;

    private boolean isComplete;
    private boolean isNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ScreenUtils.setTranslucent(this);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        mCurSongInfo = intent.getParcelableExtra(PlayFragment.CUR_SONG);
        mCurIndex = intent.getIntExtra(PlayFragment.CUR_INDEX, Integer.MIN_VALUE);

        initToolbar();

        initViewPager();

        mMusicManagerService.setBindServiceCallback(new MusicManagerService.BindServiceCallback() {
            @Override
            public void updateFirst() {
                if (mMusicManagerService.isPlaying()) {
                    scheduleSeekBarUpdate();
                } else {
                    updateSeekBar();
                }
                updateViewPager(mViewPager.getCurrentItem());
            }
        });

        mMusicManagerService.setPlayCallback(new MusicBroadcastReceiver.PlayCallback() {
            @Override
            public void playStateChange(boolean isPlaying) {

            }

            @Override
            public void mediaSourceChange(boolean indexChanged, int index, List<SongInfo> songInfos) {

            }

            @Override
            public void indexChanged(int index, boolean isComplete) {
                updateIndex(index);
                PlayActivity.this.isComplete = isComplete;
                if (!isComplete) {
                    updateViewPager(mViewPager.getCurrentItem());
                } else {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
            }
        });
    }

    private void initViewPager() {
        mViewPager.setAdapter(new MusicAdapter());
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int lastOffset = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isNext = positionOffsetPixels > lastOffset;
                lastOffset = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {
                if (!isComplete) {
                    if (isNext) {
                        mMusicManagerService.next();
                    } else {
                        mMusicManagerService.previous();
                    }
                } else {
                    updateViewPager(position);
                }
                isComplete = false;
                if (position == 0) {
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isComplete = true;
                            mViewPager.setCurrentItem(Constant.PAGER_SIZE - 2, false);
                        }
                    }, 500);
                }
                if (position == Constant.PAGER_SIZE - 1) {
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isComplete = true;
                            mViewPager.setCurrentItem(1, false);
                        }
                    }, 500);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    private void initToolbar() {
        mToolbar.setTitle(mCurSongInfo.getTitle());
        mToolbar.setSubtitle(mCurSongInfo.getArtist());
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        exeBindService();
        getScheduledService();
    }

    @Override
    protected void onStop() {
        super.onStop();
        exeUnbindService();
        releaseScheduledService();
    }

    @Override
    public BasePresenter createPresenter() {
        return null;
    }

    private void updateSeekBar() {
        if (checkMusicManagerService() != null) {
            long progress = mMusicManagerService.getCurrentPosition();
            long total = mMusicManagerService.getDuration();
            long buf = mMusicManagerService.getBufferedPosition();

            if (progress == C.TIME_UNSET) {
                progress = 0;
            }
            if (total == C.TIME_UNSET) {
                total = 0;
            }
            if (buf == C.TIME_UNSET) {
                buf = 0;
            }

            mTextViewStart.setText(TimeUtils.formatTime(progress));
            mTextViewTotal.setText(TimeUtils.formatTime(total));

            mSeekBar.setMax((int) total);
            mSeekBar.setProgress((int) progress);
            mSeekBar.setSecondaryProgress((int) buf);
        }
    }

    private void updateViewPager(int pos) {
        View view = instantiateView(mViewPager, pos);
        mImageViewAlbum = view.findViewById(R.id.iv_pager_album);
        Picasso.get()
                .load(mCurSongInfo.getPicUrl())
                .transform(new PicassoTransform(this, PicassoTransform.MAX_RADIUS, 10))
                .into(mViewRoot);
        Picasso.get()
                .load(mCurSongInfo.getPicUrl())
                .centerCrop()
                .resize(ScreenUtils.getScreenWidth(this) / 2, ScreenUtils.getScreenWidth(this) / 2)
                .into(mImageViewAlbum);
        mToolbar.setTitle(mCurSongInfo.getTitle());
        mToolbar.setSubtitle(mCurSongInfo.getArtist());
    }

    private void updateIndex(int index) {
        mCurIndex = index;
        if (mSongInfos == null) {
            mSongInfos = mMusicManagerService.getMediaSources();
        }
        mCurSongInfo = mSongInfos.get(mCurIndex);
    }

    private void scheduleSeekBarUpdate() {
        mScheduledFuture = mSeekBarExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                new Handler(getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        updateSeekBar();
                    }
                });
            }
        }, 100, 1000, TimeUnit.MILLISECONDS);
    }

    private void stopSeekBarUpdate() {
        if (mScheduledFuture != null) {
            mScheduledFuture.cancel(false);
        }
    }

    private void getScheduledService() {
        if (mSeekBarExecutorService == null || mSeekBarExecutorService.isShutdown()) {
            mSeekBarExecutorService = Executors.newSingleThreadScheduledExecutor();
        }
    }

    private void releaseScheduledService() {
        if (!mSeekBarExecutorService.isShutdown()) {
            mSeekBarExecutorService.shutdown();
            mSeekBarExecutorService = null;
            stopSeekBarUpdate();
        }
    }

    private class MusicAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return Constant.PAGER_SIZE;
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = instantiateView(container, position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (position < mPagerViews.size()) {
                container.removeView(mPagerViews.get(position));
                mPagerViews.set(position, null);
            }
        }

    }

    private synchronized View instantiateView(ViewGroup container, int position) {
        int size = mPagerViews.size();
        if (position >= size) {
            for (int i = size; i <= position; i++) {
                mPagerViews.add(null);
            }
        }
        if (mPagerViews.get(position) == null) {
            View view = LayoutInflater.from(this).inflate(R.layout.pager_album, container, false);
            mPagerViews.set(position, view);
        }
        return mPagerViews.get(position);
    }
}
