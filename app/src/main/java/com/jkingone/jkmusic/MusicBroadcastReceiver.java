package com.jkingone.jkmusic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jkingone.jkmusic.service.MusicManagerService;

import java.util.ArrayList;
import java.util.List;

public class MusicBroadcastReceiver extends BroadcastReceiver {

    private List<MediaPlayerCallback> mCallbacks;

    public interface MediaPlayerCallback {
        void onCompletion();
        void onBufferingUpdate(int percent);
        void onPrepared(boolean isPlaying);
        void onError(int what);
        void onInfo(int what);
        void onSeekComplete();
    }

    public static class SimpleMediaPlayerCallback implements MediaPlayerCallback {
        @Override
        public void onCompletion() {

        }

        @Override
        public void onBufferingUpdate(int percent) {

        }

        @Override
        public void onError(int what) {

        }

        @Override
        public void onSeekComplete() {

        }

        @Override
        public void onInfo(int what) {

        }

        @Override
        public void onPrepared(boolean isPlaying) {

        }
    }

    public MusicBroadcastReceiver() {
        mCallbacks = new ArrayList<>();
    }

    public void addMediaPlayerCallback(MediaPlayerCallback callback) {
        if (callback != null && !mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
        }
    }

    public void removeMediaPlayerCallback(MediaPlayerCallback callback) {
        mCallbacks.remove(callback);
    }

    public void clearMediaPlayerCallback() {
        mCallbacks.clear();
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        if (MusicManagerService.ACTION.equals(intent.getAction())) {

            for (MediaPlayerCallback callback : mCallbacks) {
                callback.onBufferingUpdate(intent.getIntExtra(MusicManagerService.BUFFER_PERCENT, 0));
            }

            if (intent.getStringExtra(MusicManagerService.COMPLETE) != null) {
                for (MediaPlayerCallback callback : mCallbacks) {
                    callback.onCompletion();
                }
                return;
            }

            if (intent.getStringExtra(MusicManagerService.SEEK_COMPLETE) != null) {
                for (MediaPlayerCallback callback : mCallbacks) {
                    callback.onSeekComplete();
                }
                return;
            }

            int info = intent.getIntExtra(MusicManagerService.INFO, Integer.MIN_VALUE);
            if (info != Integer.MIN_VALUE) {
                for (MediaPlayerCallback callback : mCallbacks) {
                    callback.onInfo(info);
                }
                return;
            }

            int error = intent.getIntExtra(MusicManagerService.ERROR, Integer.MIN_VALUE);
            if (error != Integer.MIN_VALUE) {
                for (MediaPlayerCallback callback : mCallbacks) {
                    callback.onError(error);
                }
                return;
            }

            for (MediaPlayerCallback callback : mCallbacks) {
                callback.onPrepared(intent.getBooleanExtra(MusicManagerService.PLAY_STATE, false));
            }
        }
    }
}
