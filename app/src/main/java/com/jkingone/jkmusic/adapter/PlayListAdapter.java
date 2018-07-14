package com.jkingone.jkmusic.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkingone.jkmusic.MusicManagerService;
import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.Utils;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.service.MusicService;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.PlayListViewHolder> {

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

    public PlayListAdapter(Context context, List<SongInfo> songInfos, MusicManagerService musicManagerService) {
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

    public void setPlayPosition(int playPosition) {
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
