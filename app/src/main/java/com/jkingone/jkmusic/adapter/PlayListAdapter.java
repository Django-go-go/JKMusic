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

import com.jkingone.jkmusic.R;
import com.jkingone.jkmusic.entity.SongInfo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayListAdapter extends HeadAndFootRecycleAdapter {

    private Context mContext;
    private List<PlayList> mPlayLists = new ArrayList<>();
    private LayoutInflater mLayoutInflater;
    private int isPlayingPos;

    private static final int TYPE_CONTENT = 2;

    class PlayList {
        SongInfo mSongInfo;
        boolean isPlaying;
    }

    public PlayListAdapter(Context context, List<SongInfo> songInfos, int isPlayingPos) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        for (int i = 0; i < songInfos.size(); i++) {
            PlayList playList = new PlayList();
            playList.mSongInfo = songInfos.get(i);
            playList.isPlaying = isPlayingPos == i;
            mPlayLists.add(playList);
        }
        this.isPlayingPos = isPlayingPos;
        addHeaderView(createHeadView());
    }

    private View createHeadView() {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_list_playlist_head, null, false);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeadOnClickListener != null) {
                    mHeadOnClickListener.headOnClick(v);
                }
            }
        });
        return view;
    }

    @Override
    public RecyclerView.ViewHolder onCreateContentViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){

            case TYPE_CONTENT:
                View convertView = mLayoutInflater.inflate(R.layout.item_list_playlist, parent, false);
                return new ContentViewHolder(convertView);

            default:
                throw new IllegalArgumentException("no Type");
        }
    }

    @Override
    public int getItemContentCount() {
        return mPlayLists.size();
    }

    @Override
    public int getItemContentViewType(int position) {
        return TYPE_CONTENT;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ContentViewHolder) {
            final ContentViewHolder contentViewHolder = (ContentViewHolder) holder;

            final PlayList playList = mPlayLists.get(position - 1);

            if (playList.isPlaying) {
                isPlayingPos = position - 1;
                contentViewHolder.mImageViewPic.setVisibility(View.VISIBLE);
                contentViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            } else {
                contentViewHolder.mImageViewPic.setVisibility(View.GONE);
                contentViewHolder.mTextView.setTextColor(mContext.getResources().getColor(R.color.black));
            }

            contentViewHolder.mTextView.setText(playList.mSongInfo.getTitle() + " - " + playList.mSongInfo.getArtist());

            contentViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayList lastSong = mPlayLists.get(isPlayingPos);
                    lastSong.isPlaying = false;
                    PlayList song = new PlayList();
                    song.mSongInfo = playList.mSongInfo;
                    song.isPlaying = true;
                    mPlayLists.set(position - 1, song);
                    isPlayingPos = position - 1;
                    notifyDataSetChanged();
                }
            });
            contentViewHolder.mImageViewDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPlayLists.remove(playList);
                    notifyDataSetChanged();
                }
            });
        }
    }

    class ContentViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_pic)
        ImageView mImageViewPic;
        @BindView(R.id.tv_song)
        TextView mTextView;
        @BindView(R.id.iv_delete)
        ImageView mImageViewDelete;

        ContentViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
