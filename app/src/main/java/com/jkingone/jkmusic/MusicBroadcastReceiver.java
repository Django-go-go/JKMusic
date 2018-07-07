package com.jkingone.jkmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jkingone.jkmusic.data.entity.SongInfo;
import com.jkingone.jkmusic.service.MusicService;

import java.util.List;

public class MusicBroadcastReceiver extends BroadcastReceiver {

    private PlayCallback mPlayCallback;

    public interface PlayCallback {
        void playStateChange(boolean isPlaying);
        void mediaSourceChange(boolean indexChanged, int index, List<SongInfo> songInfos);
        void indexChanged(int index, boolean isComplete);
    }

    public MusicBroadcastReceiver() {
    }

    public MusicBroadcastReceiver(PlayCallback playCallback) {
        mPlayCallback = playCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (MusicService.ACTION.equals(intent.getAction())) {
            if (intent.getStringExtra(MusicService.EXTRA_PLAY) != null) {
                if (mPlayCallback != null) {
                    mPlayCallback.playStateChange(true);
                }
            }
            if (intent.getStringExtra(MusicService.EXTRA_PAUSE) != null) {
                if (mPlayCallback != null) {
                    mPlayCallback.playStateChange(false);
                }
            }

            List<SongInfo> songInfos = intent.getParcelableArrayListExtra(MusicService.MUSIC_DATA_CHANGE);
            if (songInfos != null) {
                boolean indexChanged = intent.getBooleanExtra(MusicService.MUSIC_DATA_INDEX_CHANGE, false);
                int index = intent.getIntExtra(MusicService.MUSIC_DATA_INDEX, Integer.MIN_VALUE);
                if (mPlayCallback != null) {
                    mPlayCallback.mediaSourceChange(indexChanged, index, songInfos);
                }
            }
            int index = intent.getIntExtra(MusicService.MUSIC_INDEX, Integer.MIN_VALUE);
            boolean isCompelete = intent.getBooleanExtra(MusicService.MUSIC_COMPLETE, false);
            if (index != Integer.MIN_VALUE) {
                if (mPlayCallback != null) {
                    mPlayCallback.indexChanged(index, isCompelete);
                }
            }
        }
    }
}
