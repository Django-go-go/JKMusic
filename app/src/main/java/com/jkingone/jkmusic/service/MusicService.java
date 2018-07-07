package com.jkingone.jkmusic.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TimeUtils;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.metadata.MetadataOutput;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.EventLogger;
import com.google.android.exoplayer2.util.Util;
import com.jkingone.jkmusic.IMusicInterface;
import com.jkingone.jkmusic.data.entity.SongInfo;
import com.jkingone.jkmusic.media.JExoPlayerHelper;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.List;

import static com.jkingone.jkmusic.media.JExoPlayerHelper.DEFAULT_COOKIE_MANAGER;

public class MusicService extends Service {

    private static final String TAG = "MusicService";

    //    public static final String ACTION_PLAY = "com.jkingone.jkmusic.service.play";
    public static final String ACTION = "com.jkingone.jkmusic.music.action";
    public static final String EXTRA_PLAY = "com.jkingone.jkmusic.service.play";
    public static final String EXTRA_PAUSE = "com.jkingone.jkmusic.service.pause";
    public static final String EXTRA_PREV = "com.jkingone.jkmusic.service.prev";
    public static final String EXTRA_NEXT = "com.jkingone.jkmusic.service.next";
    public static final String EXTRA_RELEASE = "com.jkingone.jkmusic.service.release";
    public static final String EXTRA_DATA = "com.jkingone.jkmusic.service.data";
    public static final String EXTRA_DATA_LIST = "com.jkingone.jkmusic.service.datalist";

    private DataSource.Factory mFileDataSourceFactory;
    private DataSource.Factory mHttpDataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;

    private List<SongInfo> mSongInfos = new ArrayList<>();

    private boolean isReleaseAndComplete = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mFileDataSourceFactory = JExoPlayerHelper.instance(this).buildFileDataSourceFactory();
        mHttpDataSourceFactory = JExoPlayerHelper.instance(this).buildHttpDataSourceFactory(false);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
        initializePlayer();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicManager();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        releasePlayer();
    }

    private void checkPlayerNotNull() {
        if (player == null) {
            Log.i(TAG, "player is null");
            throw new IllegalArgumentException("player is null");
        }
    }

    private void initializePlayer() {
        if (player == null) {
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(JExoPlayerHelper.BANDWIDTH_METER);

            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);

            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            player.addListener(new PlayerEventListener());
        }

    }

    private void releasePlayer() {
        if (player != null) {
            player.release();
            player = null;
            mediaSource = null;
            trackSelector = null;
        }
    }

    private void prepareMediaSource(Uri[] uris) {

        MediaSource[] mediaSources = new MediaSource[uris.length];

        for (int i = 0; i < mediaSources.length; i++) {
            if (uris[i].getScheme().startsWith("http")) {
                mediaSources[i] = JExoPlayerHelper.instance(MusicService.this).buildMediaSource(uris[i], null, mHttpDataSourceFactory);
            } else {
                mediaSources[i] = JExoPlayerHelper.instance(MusicService.this).buildMediaSource(uris[i], null, mFileDataSourceFactory);
            }
        }

        mediaSource = mediaSources.length == 1 ? mediaSources[0] : new ConcatenatingMediaSource(mediaSources);

        mediaSource.addEventListener(new Handler(player.getPlaybackLooper()), new DefaultMediaSourceEventListener());
    }

    private void sendBroadCastForFragExtra(String key, String value) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(key, value);
        sendBroadcast(intent);
    }

    private void sendBroadCastForFragData(SongInfo songInfo) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(MusicService.EXTRA_DATA, songInfo);
        sendBroadcast(intent);
    }

    private class DefaultMediaSourceEventListener implements MediaSourceEventListener {
        @Override
        public void onMediaPeriodCreated(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            Log.i(TAG, "onMediaPeriodCreated: " + windowIndex);
        }

        @Override
        public void onMediaPeriodReleased(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            Log.i(TAG, "onMediaPeriodReleased: " + windowIndex);
            isReleaseAndComplete = true;
        }

        @Override
        public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            Log.i(TAG, "onLoadStarted: " + windowIndex);
        }

        @Override
        public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            Log.i(TAG, "onLoadCompleted: " + windowIndex);
            isReleaseAndComplete = true;
        }

        @Override
        public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            Log.i(TAG, "onLoadCanceled: " + windowIndex);
        }

        @Override
        public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            Log.i(TAG, "onLoadError: " + windowIndex);
        }

        @Override
        public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            Log.i(TAG, "onReadingStarted: " + windowIndex);
            if (isReleaseAndComplete) {
                sendBroadCastForFragExtra(EXTRA_RELEASE, EXTRA_RELEASE);
                isReleaseAndComplete = false;
            }
        }

        @Override
        public void onUpstreamDiscarded(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
//            Log.i(TAG, "onUpstreamDiscarded: " + windowIndex);
        }

        @Override
        public void onDownstreamFormatChanged(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, MediaLoadData mediaLoadData) {
//            Log.i(TAG, "onDownstreamFormatChanged: " + windowIndex);
        }
    }

    private class PlayerEventListener extends Player.DefaultEventListener {

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            if (playbackState == Player.STATE_ENDED) {
//                Log.i(TAG, "onPlayerStateChanged: END");
            }
            if (playbackState == Player.STATE_BUFFERING) {
//                Log.i(TAG, "onPlayerStateChanged: BUF");
            }
            if (playbackState == Player.STATE_IDLE) {
//                Log.i(TAG, "onPlayerStateChanged: IDLE");
            }
            if (playbackState == Player.STATE_READY) {
                Log.i(TAG, "onPlayerStateChanged: READY " + player.getCurrentPosition() + " "
                        + player.getBufferedPosition() + " " + player.getDuration());

                if (playWhenReady) {
                    sendBroadCastForFragExtra(MusicService.EXTRA_PLAY, MusicService.EXTRA_PLAY);
                } else {
                    sendBroadCastForFragExtra(MusicService.EXTRA_PAUSE, MusicService.EXTRA_PAUSE);
                }
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            Log.e(TAG, "Player is error: ", e);
        }
    }

    private class MusicManager extends IMusicInterface.Stub {

        @Override
        public void prepareMediaSource(List<SongInfo> songInfos) throws RemoteException {

            if (songInfos == null) {
                Log.i(TAG, "song is null");
                throw new IllegalArgumentException("song is null");
            }

            mSongInfos.addAll(songInfos);
            Uri[] uris = new Uri[songInfos.size()];
            for (int i = 0; i < uris.length; i++) {
                String path = songInfos.get(i).getUrl();
                if (path.startsWith("http")) {
                    uris[i] = Uri.parse(path);
                } else {
                    uris[i] = Uri.fromFile(new File(path));
                }
            }
            MusicService.this.prepareMediaSource(uris);
        }

        @Override
        public void addMediaSource(SongInfo songInfo) throws RemoteException {
            sendBroadCastForFragExtra(MusicService.EXTRA_DATA_LIST, MusicService.EXTRA_DATA_LIST);
        }

        @Override
        public void play() throws RemoteException {
            if (mediaSource != null && getPlaybackState() == Player.STATE_READY) {
                checkPlayerNotNull();
                player.setPlayWhenReady(true);
            }
        }

        @Override
        public void playForIndex(int index) throws RemoteException {
            checkPlayerNotNull();
            player.setPlayWhenReady(true);
            player.prepare(mediaSource);
            player.seekToDefaultPosition(index);
        }

        @Override
        public void pause() throws RemoteException {
            checkPlayerNotNull();
            player.setPlayWhenReady(false);
        }

        @Override
        public void next() throws RemoteException {
            checkPlayerNotNull();
            seekToIndex(getNextWindowIndex(), 0);
            play();
        }

        @Override
        public void previous() throws RemoteException {
            checkPlayerNotNull();
            seekToIndex(getPreviousWindowIndex(), 0);
            play();
        }

        @Override
        public void initializePlayer() throws RemoteException {
            MusicService.this.initializePlayer();
        }

        @Override
        public void releasePlayer() throws RemoteException {
            MusicService.this.releasePlayer();
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            checkPlayerNotNull();
            return getPlaybackState() == Player.STATE_READY && player.getPlayWhenReady();
        }

        @Override
        public List<SongInfo> getMediaSource() throws RemoteException {
            return mSongInfos;
        }

        @Override
        public int getPlaybackState() throws RemoteException {
            checkPlayerNotNull();
            return player.getPlaybackState();
        }

        @Override
        public void setRepeatMode(int repeatMode) throws RemoteException {
            checkPlayerNotNull();
            player.setRepeatMode(repeatMode);
        }

        @Override
        public int getRepeatMode() throws RemoteException {
            checkPlayerNotNull();
            return player.getRepeatMode();
        }

        @Override
        public boolean isLoading() throws RemoteException {
            checkPlayerNotNull();
            return player.isLoading();
        }

        @Override
        public void seekTo(long positionMs) throws RemoteException {
            checkPlayerNotNull();
            player.seekTo(positionMs);
        }

        @Override
        public void seekToIndex(int windowIndex, long positionMs) throws RemoteException {
            checkPlayerNotNull();
            player.seekTo(windowIndex, positionMs);
        }

        @Override
        public void stop() throws RemoteException {
            checkPlayerNotNull();
            player.stop();
        }

        @Override
        public void stopCanReset(boolean reset) throws RemoteException {
            checkPlayerNotNull();
            player.stop(reset);
        }

        @Override
        public int getCurrentWindowIndex() throws RemoteException {
            checkPlayerNotNull();
            return player.getCurrentWindowIndex();
        }

        @Override
        public int getNextWindowIndex() throws RemoteException {
            checkPlayerNotNull();
            return player.getNextWindowIndex();
        }

        @Override
        public int getPreviousWindowIndex() throws RemoteException {
            checkPlayerNotNull();
            return player.getPreviousWindowIndex();
        }

        @Override
        public long getDuration() throws RemoteException {
            checkPlayerNotNull();
            return player.getDuration();
        }

        @Override
        public long getCurrentPosition() throws RemoteException {
            checkPlayerNotNull();
            return player.getCurrentPosition();
        }

        @Override
        public long getBufferedPosition() throws RemoteException {
            checkPlayerNotNull();
            return player.getBufferedPosition();
        }

        @Override
        public int getBufferedPercentage() throws RemoteException {
            checkPlayerNotNull();
            return player.getBufferedPercentage();
        }
    }
}
