package com.jkingone.jkmusic.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.jkingone.jkmusic.IMusicInterface;
import com.jkingone.jkmusic.ContentHelper;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.media.JExoPlayerHelper;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import tv.danmaku.ijk.media.player.AndroidMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

import static com.jkingone.jkmusic.media.JExoPlayerHelper.DEFAULT_COOKIE_MANAGER;

public class MusicService extends Service {

    private MusicManagerService mMusicManagerService;

    @Override
    public void onCreate() {
        super.onCreate();
        if (mMusicManagerService == null) {
            mMusicManagerService = MusicManagerService.createMusicManagerService(this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMusicManagerService;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMusicManagerService.release();
    }
}