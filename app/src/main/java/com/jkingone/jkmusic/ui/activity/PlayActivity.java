package com.jkingone.jkmusic.ui.activity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
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
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.exoplayer2.C;
import com.jkingone.common.utils.ImageUtils;
import com.jkingone.common.utils.ScreenUtils;
import com.jkingone.common.utils.TimeUtils;
import com.jkingone.jkmusic.ui.base.BaseActivity;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.MusicBroadcastReceiver;
import com.jkingone.jkmusic.MusicManagerService;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.ui.fragment.PlayFragment;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PlayActivity extends BaseActivity {

    private static final String TAG = "PlayActivity";

    @BindView(R.id.root)
    LinearLayout mViewBackground;
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

    @BindView(R.id.iv_play)
    ImageView mImageViewPlay;

    private ObjectAnimator mViewPagerAnimator;
    private MusicAdapter mMusicAdapter;

    private List<ObjectAnimator> mViewPagerAnimators = new ArrayList<>(6);
    private List<View> mPagerViews = new ArrayList<>(6);
    private View mCurView;
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

        initCallback();
    }

    private void initCallback() {
        mMusicManagerService.setBindServiceCallback(new MusicManagerService.BindServiceCallback() {
            @Override
            public void updateFirst() {
                if (mMusicManagerService.isPlaying()) {
                    scheduleSeekBarUpdate();
                    mImageViewPlay.setImageResource(R.drawable.music_large_blue);
                } else {
                    mImageViewPlay.setImageResource(R.drawable.music_large);
                    updateSeekBar();
                }
                updateViewPager(mViewPager.getCurrentItem());
            }
        });

        mMusicManagerService.setPlayCallback(new MusicBroadcastReceiver.PlayCallback() {
            @Override
            public void playStateChange(boolean isPlaying) {
                if (isPlaying) {
                    mImageViewPlay.setImageResource(R.drawable.music_large_blue);
                } else {
                    mImageViewPlay.setImageResource(R.drawable.music_large);
                }
                postViewPagerAnimator(isPlaying);
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
        setViewPagerScroll(mViewPager);
        if (mMusicAdapter == null) {
            mMusicAdapter = new MusicAdapter();
        }
        mViewPager.setAdapter(mMusicAdapter);
        mViewPager.setCurrentItem(1);
        mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            int lastPos = mViewPager.getCurrentItem();

            @Override
            public void onPageSelected(int position) {

                isNext = (lastPos == Constant.PAGER_SIZE - 1 && position == 1) || (position > lastPos);
                if (lastPos == 0 && position == Constant.PAGER_SIZE - 2) {
                    isNext = false;
                }
                Log.i(TAG, "onPageSelected: " + position + " " + isComplete + " " + isNext);
                lastPos = position;

                if (position == 0) {
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isComplete = false;
                            isNext = false;
                            mViewPager.setCurrentItem(Constant.PAGER_SIZE - 2, false);
                        }
                    }, 500);
                } else if (position == Constant.PAGER_SIZE - 1) {
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isComplete = false;
                            isNext = true;
                            mViewPager.setCurrentItem(1, false);
                        }
                    }, 500);
                } else {
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
                }
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

    @OnClick({R.id.iv_mode, R.id.iv_last, R.id.iv_next, R.id.iv_play, R.id.iv_menu})
    public void onBottomClick(View view) {
        switch (view.getId()) {
            case R.id.iv_mode: {
                break;
            }
            case R.id.iv_last: {
                if (getMusicManagerService() != null) {
                    isComplete = false;
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1, true);
                }
                break;
            }
            case R.id.iv_play: {
                if (getMusicManagerService() != null) {
                    if (mMusicManagerService.isPlaying()) {
                        mMusicManagerService.pause();
                    } else {
                        mMusicManagerService.play();
                    }
                }
                break;
            }
            case R.id.iv_next: {
                if (getMusicManagerService() != null) {
                    isComplete = false;
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }
                break;
            }
            case R.id.iv_menu: {
                break;
            }
        }
    }

    private void updateSeekBar() {
        if (getMusicManagerService() != null) {
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
                .transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap source) {
                        Bitmap bitmap = ImageUtils.blurBitmap(source, 25, 8, PlayActivity.this, true);
                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), bitmap);
                        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#85403b3b"));
                        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{bitmapDrawable, colorDrawable});
                        return ImageUtils.drawableToBitmap(layerDrawable);
                    }

                    @Override
                    public String key() {
                        return mCurSongInfo.getPicUrl();
                    }
                })
                .into(new Target() {

                    private Handler mHandler = new Handler();
                    private Runnable mLastCallback =null;
                    @Override
                    public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                        if (mLastCallback != null) {
                            mHandler.removeCallbacks(mLastCallback);
                        }
                        Runnable curCallback = new Runnable() {
                            @Override
                            public void run() {
                                mViewBackground.setBackground(new BitmapDrawable(getResources(), bitmap));
                            }
                        };
                        mHandler.postDelayed(curCallback, 1000);
                        mLastCallback = curCallback;
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
        Picasso.get()
                .load(mCurSongInfo.getPicUrl())
                .placeholder(R.drawable.music)
                .centerCrop()
                .resize(ScreenUtils.getScreenWidth(this) / 2, ScreenUtils.getScreenWidth(this) / 2)
                .into(mImageViewAlbum);
        mToolbar.setTitle(mCurSongInfo.getTitle());
        mToolbar.setSubtitle(mCurSongInfo.getArtist());

        postViewPagerAnimator(mMusicManagerService.isPlaying());
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

    private void postViewPagerAnimator(final boolean start) {
        mViewPager.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator objectAnimator = instantiateAnimator(mViewPager.getCurrentItem());
                if (start) {
                    if (objectAnimator.isStarted()) {
                        objectAnimator.resume();
                    } else {
                        objectAnimator.start();
                    }
                } else {
                    objectAnimator.pause();
                }
            }
        }, 500);

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
                clearViewPager(mPagerViews.get(position));
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

    private synchronized ObjectAnimator instantiateAnimator(int position) {
        int size = mViewPagerAnimators.size();
        if (position >= size) {
            for (int i = size; i <= position; i++) {
                mViewPagerAnimators.add(null);
            }
        }
        if (mViewPagerAnimators.get(position) == null) {
            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(instantiateView(null, position), "rotation", 0, 360);
            objectAnimator.setDuration(9000);
            objectAnimator.setInterpolator(new LinearInterpolator());
            objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
            objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
            objectAnimator.setAutoCancel(true);
            mViewPagerAnimators.set(position, objectAnimator);
        }
        return mViewPagerAnimators.get(position);
    }

    public void setViewPagerScroll(ViewPager viewPager) {
        try {
            Field mScroller = ViewPager.class.getDeclaredField("mScroller");
            ViewPagerScroller pagerScroller = new ViewPagerScroller(this);
            mScroller.setAccessible(true);
            mScroller.set(viewPager, pagerScroller);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ViewPagerScroller extends Scroller {

        public ViewPagerScroller(Context context) {
            this(context, sInterpolator);
        }

        public ViewPagerScroller(Context context, Interpolator interpolator) {
            super(context, interpolator);
        }

        private static final Interpolator sInterpolator = new Interpolator() {
            @Override
            public float getInterpolation(float t) {
                t -= 1.0f;
                return t * t * t * t * t + 1.0f;
            }
        };


        @Override
        public void startScroll(int startX, int startY, int dx, int dy, int duration) {
            super.startScroll(startX, startY, dx, dy, duration * 2);
        }
    }

    private void clearViewPager(View view) {
        ImageView imageViewCover = view.findViewById(R.id.iv_pager_album);
        imageViewCover.setImageDrawable(null);
    }
}
