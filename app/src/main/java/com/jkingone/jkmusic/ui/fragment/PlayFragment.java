package com.jkingone.jkmusic.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.utils.LogUtils;
import com.jkingone.jkmusic.ContentHelper;
import com.jkingone.jkmusic.music.MusicManager;
import com.jkingone.jkmusic.ui.base.LazyFragment;
import com.jkingone.ui.widget.JDialog;
import com.jkingone.jkmusic.MusicBroadcastReceiver;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.ui.base.BaseActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class PlayFragment extends LazyFragment implements MusicManager.ServiceConnectionListener {

    public static final String CUR_SONG = "cur_song";
    public static final String CUR_INDEX = "cur_index";

    public static final int NEXT = 1;
    public static final int PREV = 2;

    @BindView(R.id.recycle_pager)
    RecyclerView mRecyclerViewPager;
    @BindView(R.id.iv_play)
    ImageView mImageViewPlay;
    @BindView(R.id.iv_menu)
    ImageView mImageViewMenu;

    private JDialog mJDialog;

    private Unbinder mUnbinder;

    private int mScrollDirection;

    private BaseActivity mBaseActivity;
    private MusicManager mMusicManager;

    private MusicAdapter mMusicAdapter;

    private PagerSnapHelper mPagerSnapHelper;

    private MusicBroadcastReceiver.MediaPlayerCallback
            mMediaPlayerCallback = new MusicBroadcastReceiver.SimpleMediaPlayerCallback() {

        @Override
        public void onCompletion() {
            mScrollDirection = NEXT;
            mRecyclerViewPager.smoothScrollToPosition(mMusicManager.getNextIndex() + 1);
        }

        @Override
        public void onError(int what) {
            mScrollDirection = NEXT;
            mRecyclerViewPager.smoothScrollToPosition(mMusicManager.getNextIndex() + 1);
        }

        @Override
        public void onPrepared(boolean isPlaying) {
            if (isPlaying) {
                mImageViewPlay.setImageResource(R.drawable.music_xxh_yellow);
            } else {
                mImageViewPlay.setImageResource(R.drawable.music);
            }
        }
    };

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

        mMusicManager = MusicManager.getInstance();

        mRecyclerViewPager.setLayoutManager(new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL, false));
        mPagerSnapHelper = new PagerSnapHelper();
        mPagerSnapHelper.attachToRecyclerView(mRecyclerViewPager);
        mRecyclerViewPager.addOnScrollListener(new MusicScrollListener());
        if (mMusicAdapter == null) {
            mMusicAdapter = new MusicAdapter();
        }
        mRecyclerViewPager.setAdapter(mMusicAdapter);


        mMusicManager.setServiceConnectionListener(this);
        mMusicManager.addMediaPlayerCallback(mMediaPlayerCallback);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        mMusicManager.removeMediaPlayerCallback(mMediaPlayerCallback);
    }

    private View dialogView;
    private RecyclerView mRecyclerView;
    private PlayListAdapter mPlayListAdapter;

    @OnClick(R.id.iv_menu)
    public void onMenuClick() {
        if (mJDialog == null) {
            mJDialog = new JDialog(getContext());

//            List<String> strings = new ArrayList<>();
//            for (int i = 0; i < 10; i++) {
//                strings.add("index : " + i);
//            }
//            final ListView listView = new ListView(mBaseActivity);
//            listView.setAdapter(new ArrayAdapter<>(mBaseActivity, android.R.layout.simple_list_item_1, strings));
//            mJDialog.setContentView(listView);
//            mJDialog.setCheckScroll(new JDialog.CheckScroll() {
//                @Override
//                public boolean canScrollVertically() {
//                    return listView.canScrollVertically(-1);
//                }
//            });

            createDialogView();
            mJDialog.setContentView(dialogView);
            mJDialog.setCheckScroll(new JDialog.CheckScroll() {
                @Override
                public boolean canScrollVertically() {
                    return mRecyclerView.canScrollVertically(-1);
                }
            });
        }
        mPlayListAdapter.setPlayPosition(mMusicManager.getCurrentIndex());
        mRecyclerView.scrollToPosition(mMusicManager.getCurrentIndex());
        mJDialog.show();
    }

    private void createDialogView() {
        dialogView = LayoutInflater.from(mBaseActivity).inflate(R.layout.dialog_playlist, mJDialog, false);

        mRecyclerView = dialogView.findViewById(R.id.recycle_dialog);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mBaseActivity));
        mPlayListAdapter = new PlayListAdapter();
        mRecyclerView.setAdapter(mPlayListAdapter);

        dialogView.findViewById(R.id.tv_repeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                int mode = Utils.getNextRepeatMode(mMusicManager.getPlayMode());
                if (mode == MusicManager.PLAY_MODE_ALL) {
                    textView.setText("全部循环");
                } else if (mode == MusicManager.PLAY_MODE_SHUFFLE) {
                    textView.setText("随机播放");
                } else {
                    textView.setText("单曲循环");
                }
                mMusicManager.setPlayMode(mode);
            }
        });
        dialogView.findViewById(R.id.tv_clear).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMusicManager.clearMediaSources();
                mPlayListAdapter.notifyDataSetChanged();
            }
        });
    }

    @OnClick(R.id.iv_play)
    public void onPlayClick() {
        if (mMusicManager != null) {
            if (mMusicManager.isPlaying()) {
                mMusicManager.pause();
            } else {
                mMusicManager.start();
            }
        }
    }

//    public void onPagerClick() {
//        Intent intent = new Intent(mBaseActivity, PlayActivity.class);
//        intent.putExtra(CUR_SONG, mCurSongInfo);
//        intent.putExtra(CUR_INDEX, mCurIndex);
//        startActivity(intent);
//    }

    @Override
    public void onConnected() {
        mMusicManager.prepareMediaSources(new ContentHelper(getContext()).getMusic());
        mMusicManager.setPlayMode(MusicManager.PLAY_MODE_SHUFFLE);

        if (mMusicAdapter != null) {
            mMusicAdapter.notifyDataSetChanged();
        }

        int curIndex = mMusicManager.getCurrentIndex();

        mMusicManager.prepare();

        LogUtils.i("curIndex " + curIndex);
        mRecyclerViewPager.scrollToPosition(curIndex + 1);
    }

    class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.MusicViewHolder> {
        private List<SongInfo> mMediaSources;

        MusicAdapter() {
            mMediaSources = mMusicManager.getMediaSourcesForPlayMode();
        }

        @NonNull
        @Override
        public MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MusicViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.pager_bottom_music, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull MusicViewHolder holder, int position) {

            if (position == 0) {
                position = mMediaSources.size() - 1;
            } else if (position == getItemCount() - 1) {
                position = 0;
            } else {
                position -= 1;
            }

            SongInfo songInfo = mMediaSources.get(position);
            holder.mTextViewSinger.setText(songInfo.getArtist());
            holder.mTextViewSongName.setText(songInfo.getTitle());
            holder.mImageViewCover.setImageResource(R.mipmap.ic_launcher_round);
        }

        @Override
        public int getItemCount() {
            if (mMediaSources.size() == 0) {
                return 0;
            }
            return mMediaSources.size() + 2;
        }

        class MusicViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.iv_cover)
            ImageView mImageViewCover;
            @BindView(R.id.tv_singer)
            TextView mTextViewSinger;
            @BindView(R.id.tv_song_name)
            TextView mTextViewSongName;

            MusicViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }
        }
    }

    class MusicScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
            if (recyclerView.getAdapter() != null
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                    && recyclerView.getChildCount() > 0) {
                View view = mPagerSnapHelper.findSnapView(manager);
                int pos = recyclerView.getChildAdapterPosition(view);
                onScrollSelected(recyclerView, pos);
            }
        }

        private void onScrollSelected(RecyclerView recyclerView, int position) {
            int itemCount = recyclerView.getAdapter().getItemCount();
            if (position == 0) {
                recyclerView.scrollToPosition(itemCount - 2);
            } else if (position == itemCount - 1) {
                recyclerView.scrollToPosition(1);
            } else {
                if (mScrollDirection == NEXT) {
                    mMusicManager.next();
                }
                if (mScrollDirection == PREV) {
                    mMusicManager.previous();
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dx > 0) {
                mScrollDirection = NEXT;
            }
            if (dx < 0) {
                mScrollDirection = PREV;
            }
        }
    }

    class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> {

        private List<SongInfo> mPlayLists;

        private int mPlayPosition = -1;

        PlayListAdapter() {
            mPlayLists = mMusicManager.getMediaSources();
        }

        @NonNull
        @Override
        public PlayListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlayListViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.item_list_playlist, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull final PlayListViewHolder holder, final int position) {

            final SongInfo songInfo = mPlayLists.get(position);

            if (songInfo.isPlaying()) {
                mPlayPosition = position;
                holder.mImageViewPic.setVisibility(View.VISIBLE);
                holder.mTextView.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            } else {
                holder.mImageViewPic.setVisibility(View.GONE);
                holder.mTextView.setTextColor(getContext().getResources().getColor(R.color.black));
            }

            holder.mTextView.setText(songInfo.getTitle() + " - " + songInfo.getArtist());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mPlayPosition != position) {
                        setPlayPosition(position);
                        mRecyclerViewPager.scrollToPosition(mMusicManager.indexForPlayMode(songInfo) + 1);
                        mMusicManager.seekToIndex(mMusicManager.indexForPlayMode(songInfo), 0);
                    }
                }
            });
            holder.mImageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    mMusicManager.removeMediaSource(mMediaSources.indexOf(songInfo));
//                    notifyDataSetChanged();
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPlayLists.size();
        }

        void setPlayPosition(int playPosition) {
            if (playPosition >= 0 && playPosition != mPlayPosition) {
                if (mPlayPosition >= 0) {
                    SongInfo songInfo = SongInfo.cloneSongInfo(mPlayLists.get(mPlayPosition));
                    songInfo.setPlaying(false);
                    mMusicManager.updateMediaSource(mPlayPosition, songInfo, true);
                    notifyItemChanged(mPlayPosition);
                }

                SongInfo cur = SongInfo.cloneSongInfo(mPlayLists.get(playPosition));
                cur.setPlaying(true);
                mMusicManager.updateMediaSource(playPosition, cur, true);
                notifyItemChanged(playPosition);
                mPlayPosition = playPosition;
            }
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
