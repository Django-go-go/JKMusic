package com.jkingone.jkmusic.ui.fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.commonlib.Utils.DensityUtils;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.data.entity.SongInfo;
import com.jkingone.jkmusic.data.local.ContentHelper;
import com.jkingone.jkmusic.service.MusicService;
import com.jkingone.jkmusic.ui.activity.BaseActivity;
import com.jkingone.jkmusic.ui.activity.PlayActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PlayFragment extends LazyFragment {

    private static final String TAG = "PlayFragment";

    public static final String CUR_SONG = "cur_song";
    public static final String CUR_INDEX = "cur_index";
    public static final String SONG_SIZE = "song_size";

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.iv_play)
    ImageView mImageViewPlay;
    @BindView(R.id.iv_menu)
    ImageView mImageViewMenu;

    private List<View> mRootViews = new ArrayList<>();
    private ImageView mImageViewCover;
    private TextView mTextViewSinger;
    private TextView mTextViewSongName;
    private List<SongInfo> mSongInfos = new ArrayList<>();

    private Unbinder mUnbinder;

    private SongInfo mCurSongInfo;
    private int mCurIndex = Integer.MIN_VALUE;

    private boolean isSelfMove;
    private boolean isNext;

    private BaseActivity mBaseActivity;

    public static PlayFragment newInstance(String... params) {
        PlayFragment fragment = new PlayFragment();
        fragment.setArguments(setParams(params));
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            mBaseActivity = (BaseActivity) context;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        mSongInfos = new ContentHelper(getContext()).getMusic();

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            int lastOffset = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isNext = positionOffsetPixels > lastOffset;
                lastOffset = positionOffsetPixels;
            }

            @Override
            public void onPageSelected(int position) {

                if (!isSelfMove) {
                    if (mBaseActivity.getIMusicInterface() != null) {
                        try {
                            if (isNext) {
                                mBaseActivity.getIMusicInterface().next();
                            } else {
                                mBaseActivity.getIMusicInterface().previous();
                            }
                            isSelfMove = true;
                        } catch (RemoteException e) {
                            //do nothing
                        }
                    }
                } else {
                    isSelfMove = false;
                    updateViewPager(position);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
//                Log.i(TAG, "onPageScrollStateChanged: ");
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.iv_menu)
    public void onMenuClick() {
        try {
            if (mBaseActivity.getIMusicInterface() != null) {
                mBaseActivity.getIMusicInterface().prepareMediaSource(mSongInfos);
                mBaseActivity.getIMusicInterface().playForIndex(3);

                mCurIndex = 3;
                mCurSongInfo = mSongInfos.get(mCurIndex);

                mViewPager.setAdapter(new MusicAdapter());
                isSelfMove  = true;
                mViewPager.setCurrentItem(3);
            }
        } catch (RemoteException e) {
            //do nothing
        }
//        try {
//            if (mBaseActivity.getIMusicInterface() != null) {
//                if (mSongInfos != null) {
//                    mSongInfos.clear();
//                }
//                mSongInfos.addAll(mBaseActivity.getIMusicInterface().getMediaSource());
//            }
//        } catch (RemoteException e) {
//            //do nothing
//        }
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        try {
            if (mBaseActivity.getIMusicInterface() != null) {
                if (mBaseActivity.getIMusicInterface().isPlaying()) {
                    mBaseActivity.getIMusicInterface().pause();
                } else {
                    mBaseActivity.getIMusicInterface().play();
                }
            }
        } catch (RemoteException e) {
            //do nothing
        }
    }

    @OnClick(R.id.pager)
    public void onPagerClick() {
        Intent intent = new Intent(mBaseActivity, PlayActivity.class);
        intent.putExtra(CUR_SONG, mCurSongInfo);
        intent.putExtra(CUR_INDEX, mCurIndex);
        intent.putExtra(SONG_SIZE, mSongInfos.size());
        startActivity(intent);
    }

    private void updateForFirstConnect() {
        if (mBaseActivity.getIMusicInterface() != null) {
            try {
                if (mBaseActivity.getIMusicInterface().isPlaying()) {
                    mImageViewPlay.setImageResource(R.drawable.music_xxh_yellow);
                } else {
                    mImageViewPlay.setImageResource(R.drawable.music);
                }
                int curIndex = mBaseActivity.getIMusicInterface().getCurrentWindowIndex();
                if (curIndex != mCurIndex) {

                }
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    private class MusicAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mSongInfos.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Log.i(TAG, "instantiateItem: " + position);
            View view = instantiateView(container, position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (position < mRootViews.size()) {
                container.removeView(mRootViews.get(position));
                mRootViews.set(position, null);
            }
        }


    }

    private synchronized View instantiateView(ViewGroup container, int position) {
        int size = mRootViews.size();
        if (position >= size) {
            for (int i = size; i <= position; i++) {
                mRootViews.add(null);
            }
        }
        Log.i(TAG, "instantiateView: " + mRootViews.size());
        if (mRootViews.get(position) == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.pager_bottom_music, container, false);
            mRootViews.set(position, view);
        }
        return mRootViews.get(position);
    }

    private void updateIndex() {
        if (mBaseActivity.getIMusicInterface() != null) {
            try {
                mCurIndex = mBaseActivity.getIMusicInterface().getCurrentWindowIndex();
                mCurSongInfo = mSongInfos.get(mCurIndex);
                Log.i(TAG, "updateIndex: " + mCurIndex);
            } catch (RemoteException e) {
                //do nothing
            }

        }
    }

    private void updateViewPager(int pos) {
        View view = instantiateView(mViewPager, pos);
        mImageViewCover = view.findViewById(R.id.iv_cover);
        mTextViewSinger = view.findViewById(R.id.tv_singer);
        mTextViewSongName = view.findViewById(R.id.tv_songName);
        Picasso.get()
                .load(mCurSongInfo.getPicUrl())
                .centerCrop()
                .resize(DensityUtils.dp2px(mBaseActivity, 48), DensityUtils.dp2px(mBaseActivity, 48))
                .placeholder(R.drawable.music)
                .into(mImageViewCover);
        mTextViewSinger.setText(mCurSongInfo.getArtist());
        mTextViewSongName.setText(mCurSongInfo.getTitle());
    }

    public void updateUI(Intent intent) {
        if (getContext() != null) {
            if (intent.getStringExtra(MusicService.EXTRA_PLAY) != null) {
                mImageViewPlay.setImageResource(R.drawable.music_xxh_yellow);
            }
            if (intent.getStringExtra(MusicService.EXTRA_PAUSE) != null) {
                mImageViewPlay.setImageResource(R.drawable.music);
            }
            if (intent.getStringExtra(MusicService.EXTRA_RELEASE) != null) {
                updateIndex();
                if (isSelfMove) {
                    updateViewPager(mViewPager.getCurrentItem());
                    isSelfMove = false;
                } else {
                    isSelfMove = true;
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
                }
            }
            if (intent.getStringExtra(MusicService.EXTRA_DATA_LIST) != null) {

            }

        }

    }
}
