package com.jkingone.jkmusic.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jkingone.common.utils.DensityUtils;
import com.jkingone.jkmusic.ui.base.LazyFragment;
import com.jkingone.ui.widget.JDialog;
import com.jkingone.jkmusic.Constant;
import com.jkingone.jkmusic.MusicBroadcastReceiver;
import com.jkingone.jkmusic.MusicManagerService;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.service.MusicService;
import com.jkingone.jkmusic.ui.base.BaseActivity;
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

    @BindView(R.id.pager)
    ViewPager mViewPager;
    @BindView(R.id.iv_play)
    ImageView mImageViewPlay;
    @BindView(R.id.iv_menu)
    ImageView mImageViewMenu;

    private JDialog mJDialog;

    private List<View> mRootViews = new ArrayList<>(6);
    private ImageView mImageViewCover;
    private TextView mTextViewSinger;
    private TextView mTextViewSongName;
    private List<SongInfo> mSongInfos = new ArrayList<>();

    private Unbinder mUnbinder;

    private SongInfo mCurSongInfo;
    private int mCurIndex = Integer.MIN_VALUE;

    private boolean isComplete;
    private boolean isNext;

    private BaseActivity mBaseActivity;

    private MusicAdapter mMusicAdapter;

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
    public View onCreateView(@NonNull LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        initViewPager();

        initCallback();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    private void initCallback() {
        mBaseActivity.getMusicManagerService().setBindServiceCallback(new MusicManagerService.BindServiceCallback() {
            @Override
            public void updateFirst() {
                mSongInfos.clear();
                mSongInfos.addAll(mBaseActivity.getMusicManagerService().getMediaSources());
            }
        });
        mBaseActivity.getMusicManagerService().setPlayCallback(new MusicBroadcastReceiver.PlayCallback() {
            @Override
            public void playStateChange(boolean isPlaying) {
                if (isPlaying) {
                    mImageViewPlay.setImageResource(R.drawable.music_xxh_yellow);
                } else {
                    mImageViewPlay.setImageResource(R.drawable.music);
                }
            }

            @Override
            public void mediaSourceChange(boolean indexChanged, int index, List<SongInfo> songInfos) {
                if (songInfos.size() == 0) {
                    Toast.makeText(mBaseActivity, "clear", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mBaseActivity, "" + indexChanged + " " + index, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void indexChanged(int index, boolean isComplete) {
                updateIndex(index);
                PlayFragment.this.isComplete = isComplete;
                if (isComplete) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                } else {
                    updateViewPager(mViewPager.getCurrentItem());
                }
            }
        });
    }

    private void initViewPager() {
        if (mMusicAdapter == null) {
            mMusicAdapter = new MusicAdapter();
        }
        mViewPager.setAdapter(mMusicAdapter);
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
                    }, 200);
                } else if (position == Constant.PAGER_SIZE - 1) {
                    mViewPager.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            isComplete = false;
                            isNext = true;
                            mViewPager.setCurrentItem(1, false);
                        }
                    }, 200);
                } else {
                    if (!isComplete) {
                        if (isNext) {
                            mBaseActivity.getMusicManagerService().next();
                        } else {
                            mBaseActivity.getMusicManagerService().previous();
                        }
                    } else {
                        updateViewPager(position);
                    }
                    isComplete = false;
                }
            }
        });
    }

    private View dialogView;
    private RecyclerView mRecyclerView;
    private PlayListAdapter mPlayListAdapter;
    @OnClick(R.id.iv_menu)
    public void onMenuClick() {
        if (mJDialog == null) {
            mJDialog = new JDialog(mBaseActivity);

            List<String> strings = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                strings.add("index : " + i);
            }
            final ListView listView = new ListView(mBaseActivity);
            listView.setAdapter(new ArrayAdapter<>(mBaseActivity, android.R.layout.simple_list_item_1, strings));
            mJDialog.setContentView(listView);
            mJDialog.setCheckScroll(new JDialog.CheckScroll() {
                @Override
                public boolean canScrollVertically() {
                    return listView.canScrollVertically(-1);
                }
            });

//            createDialogView();
//            mJDialog.setContentView(dialogView);
//            mJDialog.setCheckScroll(new JDialog.CheckScroll() {
//                @Override
//                public boolean canScrollVertically() {
//                    return mRecyclerView.canScrollVertically(-1);
//                }
//            });
        }
//        mPlayListAdapter.setPlayPosition(mBaseActivity.getMusicManagerService().getCurrentWindowIndex());
//        mRecyclerView.scrollToPosition(mBaseActivity.getMusicManagerService().getCurrentWindowIndex());
        mJDialog.show();
    }

    private void createDialogView() {
        dialogView = LayoutInflater.from(mBaseActivity).inflate(R.layout.dialog_playlist, mJDialog, false);

        mRecyclerView = dialogView.findViewById(R.id.recycle_dialog);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        mPlayListAdapter = new PlayListAdapter(mBaseActivity, mSongInfos, mBaseActivity.getMusicManagerService());
        mRecyclerView.setAdapter(mPlayListAdapter);

        dialogView.findViewById(R.id.tv_repeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                int mode = Utils.getNextRepeatMode(mBaseActivity.getMusicManagerService().getPlayMode());
                if (mode == MusicService.PLAY_MODE_ALL) {
                    textView.setText("全部循环");
                } else if (mode == MusicService.PLAY_MODE_SHUFFLE) {
                    textView.setText("随机播放");
                } else {
                    textView.setText("单曲循环");
                }
                mBaseActivity.getMusicManagerService().setPlayMode(mode);
            }
        });
        dialogView.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBaseActivity.getMusicManagerService().clearMediaSources();
                mPlayListAdapter.clearData();
                mPlayListAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        if (mBaseActivity.getMusicManagerService() != null) {
            if (mBaseActivity.getMusicManagerService().isPlaying()) {
                mBaseActivity.getMusicManagerService().pause();
            } else {
                mBaseActivity.getMusicManagerService().play();
            }
        }
    }

    @OnClick(R.id.pager)
    public void onPagerClick() {
        Intent intent = new Intent(mBaseActivity, PlayActivity.class);
        intent.putExtra(CUR_SONG, mCurSongInfo);
        intent.putExtra(CUR_INDEX, mCurIndex);
        startActivity(intent);
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
            if (position < mRootViews.size()) {
                container.removeView(mRootViews.get(position));
                clearViewPager(mRootViews.get(position));
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
        if (mRootViews.get(position) == null) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.pager_bottom_music, container, false);
            mRootViews.set(position, view);
        }
        return mRootViews.get(position);
    }

    private void updateIndex(int index) {
        mCurIndex = index;
        mCurSongInfo = mSongInfos.get(mCurIndex);
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

    private void clearViewPager(View view) {
        ImageView imageViewCover = view.findViewById(R.id.iv_cover);
        TextView textViewSinger = view.findViewById(R.id.tv_singer);
        TextView textViewSongName = view.findViewById(R.id.tv_songName);
        textViewSinger.setText("");
        textViewSongName.setText("");
        imageViewCover.setImageDrawable(null);
    }

    static class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> {

        private Context mContext;
        private List<PlayList> mPlayLists = new ArrayList<>();
        private LayoutInflater mLayoutInflater;
        private int mPlayPosition = -1;

        private MusicManagerService mMusicManagerService;

        @NonNull
        @Override
        public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlayListViewHolder(mLayoutInflater.inflate(R.layout.item_list_playlist, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PlayListViewHolder holder, final int position) {

            final PlayList playList = mPlayLists.get(position);

            if (playList.isPlaying) {
                mPlayPosition = position;
                holder.mImageViewPic.setVisibility(View.VISIBLE);
                holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                holder.mImageViewPic.setVisibility(View.GONE);
                holder.mTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            }

            holder.mTextView.setText(playList.mSongInfo.getTitle() + " - " + playList.mSongInfo.getArtist());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mPlayPosition != position) {
                        setPlayPosition(position);

                        mMusicManagerService.seekToIndex(position, 0);
                    }
                }
            });
            holder.mImageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMusicManagerService.removeMediaSource(position);
                    mPlayLists.remove(playList);
                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPlayLists.size();
        }

        public void clearData() {
            mPlayLists.clear();
        }

        PlayListAdapter(Context context, List<SongInfo> songInfos, MusicManagerService musicManagerService) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
            for (int i = 0; i < songInfos.size(); i++) {
                PlayList playList = new PlayList();
                playList.mSongInfo = songInfos.get(i);
                playList.isPlaying = false;
                mPlayLists.add(playList);
            }
            mMusicManagerService = musicManagerService;
        }

        void setPlayPosition(int playPosition) {
            if (playPosition >= 0) {
                if (playPosition != mPlayPosition) {
                    if (mPlayPosition >= 0) {
                        mPlayLists.get(mPlayPosition).isPlaying = false;
                    }
                    PlayList playList = new PlayList();
                    playList.isPlaying = true;
                    playList.mSongInfo = mPlayLists.get(playPosition).mSongInfo;
                    mPlayLists.set(playPosition, playList);
                    mPlayPosition = playPosition;
                    notifyDataSetChanged();
                }
            }
        }

        class PlayList {
            SongInfo mSongInfo;
            boolean isPlaying;
        }

        class PlayListViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.iv_pic)
            ImageView mImageViewPic;
            @BindView(R.id.tv_song)
            TextView mTextView;
            @BindView(R.id.iv_delete)
            ImageView mImageViewDelete;

            PlayListViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }
}
