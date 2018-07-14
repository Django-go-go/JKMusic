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
import com.jkingone.jkmusic.data.local.ContentHelper;
import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.media.JExoPlayerHelper;

import java.io.File;
import java.io.IOException;
import java.net.CookieHandler;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.jkingone.jkmusic.media.JExoPlayerHelper.DEFAULT_COOKIE_MANAGER;

public class MusicService extends Service {

    private static final String TAG = "MusicService";

    public static final String ACTION = "com.jkingone.jkmusic.music.action";

    public static final String EXTRA_PLAY = "com.jkingone.jkmusic.service.play";
    public static final String EXTRA_PAUSE = "com.jkingone.jkmusic.service.pause";

    public static final String EXTRA_PREV = "com.jkingone.jkmusic.service.prev";
    public static final String EXTRA_NEXT = "com.jkingone.jkmusic.service.next";
    public static final String EXTRA_RELEASE = "com.jkingone.jkmusic.service.release";

    public static final String MUSIC_INDEX = "com.jkingone.jkmusic.service.index";
    public static final String MUSIC_COMPLETE = "com.jkingone.jkmusic.service.complete";

    public static final String MUSIC_DATA_INDEX_CHANGE = "com.jkingone.jkmusic.service.data.indexchange";
    public static final String MUSIC_DATA_CHANGE = "com.jkingone.jkmusic.service.data.change";
    public static final String MUSIC_DATA_INDEX = "com.jkingone.jkmusic.service.index";

    public static final int PLAY_MODE_SHUFFLE = 0;
    public static final int PLAY_MODE_ALL = Player.REPEAT_MODE_ALL;
    public static final int PLAY_MODE_ONE = Player.REPEAT_MODE_ONE;

    private DataSource.Factory mFileDataSourceFactory;
    private DataSource.Factory mHttpDataSourceFactory;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private DefaultTrackSelector trackSelector;
    private DefaultTrackSelector.Parameters trackSelectorParameters;

    private List<SongInfo> mSongInfos = new ArrayList<>();

    private boolean isComplete = false;

    private int mPlayMode = PLAY_MODE_ALL;

    @Override
    public void onCreate() {
        super.onCreate();

        init();

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

    private void init() {
        mFileDataSourceFactory = JExoPlayerHelper.instance(this).buildFileDataSourceFactory();
        mHttpDataSourceFactory = JExoPlayerHelper.instance(this).buildHttpDataSourceFactory(false);
        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }
        trackSelectorParameters = new DefaultTrackSelector.ParametersBuilder().build();
    }

    private void initializePlayer() {
        if (player == null) {
            TrackSelection.Factory trackSelectionFactory = new AdaptiveTrackSelection.Factory(JExoPlayerHelper.BANDWIDTH_METER);

            trackSelector = new DefaultTrackSelector(trackSelectionFactory);
            trackSelector.setParameters(trackSelectorParameters);

            player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);
            player.setRepeatMode(mPlayMode);
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

    private void sendBroadCastForExtra(String key, String value) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(key, value);
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
        }

        @Override
        public void onLoadStarted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            Log.i(TAG, "onLoadStarted: " + windowIndex);
        }

        @Override
        public void onLoadCompleted(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            Log.i(TAG, "onLoadCompleted: " + windowIndex);
            isComplete = true;
        }

        @Override
        public void onLoadCanceled(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData) {
            Log.i(TAG, "onLoadCanceled: " + windowIndex);
            isComplete = false;
        }

        @Override
        public void onLoadError(int windowIndex, @Nullable MediaSource.MediaPeriodId mediaPeriodId, LoadEventInfo loadEventInfo, MediaLoadData mediaLoadData, IOException error, boolean wasCanceled) {
            Log.i(TAG, "onLoadError: " + windowIndex);
        }

        @Override
        public void onReadingStarted(int windowIndex, MediaSource.MediaPeriodId mediaPeriodId) {
            Log.i(TAG, "onReadingStarted: " + windowIndex);
            Intent intent = new Intent(ACTION);
            if (isComplete) {
                intent.putExtra(MUSIC_COMPLETE, true);
            } else {
                intent.putExtra(MUSIC_COMPLETE, false);
            }
            intent.putExtra(MUSIC_INDEX, windowIndex);
            sendBroadcast(intent);
            isComplete = false;
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
                    sendBroadCastForExtra(MusicService.EXTRA_PLAY, MusicService.EXTRA_PLAY);
                } else {
                    sendBroadCastForExtra(MusicService.EXTRA_PAUSE, MusicService.EXTRA_PAUSE);
                }
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException e) {
            Log.e(TAG, "Player is error: ", e);
        }
    }

    private class MusicManager extends IMusicInterface.Stub {

        MusicManager() {
            if (mediaSource == null) {
                prepareMediaSourcesInner(new ContentHelper(MusicService.this).getMusic());
            }
        }

        private MediaSource[] createMediaSources(Collection<SongInfo> songInfos) {
            if (songInfos == null) {
                Log.i(TAG, "song is null");
                throw new IllegalArgumentException("song is null");
            }

            Uri[] uris = new Uri[songInfos.size()];
            int j = 0;
            for (SongInfo songInfo : songInfos) {
                String path = songInfo.getUrl();
                if (path.startsWith("http")) {
                    uris[j] = Uri.parse(path);
                } else {
                    uris[j] = Uri.fromFile(new File(path));
                }
                j++;
            }

            MediaSource[] mediaSources = new MediaSource[uris.length];

            for (int i = 0; i < mediaSources.length; i++) {
                if (uris[i].getScheme().startsWith("http")) {
                    mediaSources[i] = JExoPlayerHelper.instance(MusicService.this).buildMediaSource(uris[i], null, mHttpDataSourceFactory);
                } else {
                    mediaSources[i] = JExoPlayerHelper.instance(MusicService.this).buildMediaSource(uris[i], null, mFileDataSourceFactory);
                }
            }

            return mediaSources;
        }

        private MediaSource createMediaSource(SongInfo songInfo) {
            if (songInfo == null) {
                Log.i(TAG, "song is null");
                throw new IllegalArgumentException("song is null");
            }

            Uri uri;
                String path = songInfo.getUrl();
                if (path.startsWith("http")) {
                    uri = Uri.parse(path);
                } else {
                    uri = Uri.fromFile(new File(path));
                }

            MediaSource mediaSource;

                if (uri.getScheme().startsWith("http")) {
                    mediaSource = JExoPlayerHelper.instance(MusicService.this).buildMediaSource(uri, null, mHttpDataSourceFactory);
                } else {
                    mediaSource = JExoPlayerHelper.instance(MusicService.this).buildMediaSource(uri, null, mFileDataSourceFactory);
            }

            return mediaSource;
        }

        private void prepareMediaSourcesInner(List<SongInfo> songInfos) {
            if (songInfos == null) {
                Log.i(TAG, "song is null");
                return;
            } else {
                mSongInfos.clear();
            }

            mSongInfos.addAll(songInfos);

            mediaSource = /*mediaSources.length == 1 ? mediaSources[0] : */new ConcatenatingMediaSource(createMediaSources(songInfos));

            mediaSource.addEventListener(new Handler(player.getPlaybackLooper()), new DefaultMediaSourceEventListener());
        }

        @Override
        public void prepareMediaSources(List<SongInfo> songInfos) throws RemoteException {

            if (songInfos == null) {
                Log.i(TAG, "song is null");
                throw new IllegalArgumentException("song is null");
            }

            prepareMediaSourcesInner(songInfos);

        }

        @Override
        public void addMediaSource(final SongInfo songInfo) throws RemoteException {
            if (mediaSource instanceof ConcatenatingMediaSource) {
                ConcatenatingMediaSource concatenatingMediaSource = (ConcatenatingMediaSource) mediaSource;
                final int index = getCurrentWindowIndex();
                concatenatingMediaSource.addMediaSource(createMediaSource(songInfo), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSongInfos.add(songInfo);
                            Intent intent = new Intent(ACTION);
                            intent.putParcelableArrayListExtra(MUSIC_DATA_CHANGE, new ArrayList<Parcelable>(mSongInfos));
                            if (index != getCurrentWindowIndex()) {
                                intent.putExtra(MUSIC_DATA_INDEX_CHANGE, true);
                                intent.putExtra(MUSIC_DATA_INDEX, getCurrentWindowIndex());
                            } else {
                                intent.putExtra(MUSIC_DATA_INDEX_CHANGE, false);
                            }
                            sendBroadcast(intent);
                        } catch (RemoteException e) {
                            //do nothing
                        }
                    }
                });
            }
        }

        @Override
        public List<SongInfo> getMediaSources() throws RemoteException {
            return mSongInfos;
        }

        @Override
        public void removeMediaSource(final int index) throws RemoteException {
            if (mediaSource instanceof ConcatenatingMediaSource) {
                ConcatenatingMediaSource concatenatingMediaSource = (ConcatenatingMediaSource) mediaSource;
                final int pos = getCurrentWindowIndex();
                concatenatingMediaSource.removeMediaSource(index, new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSongInfos.remove(index);
                            Intent intent = new Intent(ACTION);
                            intent.putParcelableArrayListExtra(MUSIC_DATA_CHANGE, new ArrayList<Parcelable>(mSongInfos));
                            if (pos != getCurrentWindowIndex()) {
                                intent.putExtra(MUSIC_DATA_INDEX_CHANGE, true);
                                intent.putExtra(MUSIC_DATA_INDEX, getCurrentWindowIndex());
                            } else {
                                intent.putExtra(MUSIC_DATA_INDEX_CHANGE, false);
                            }
                            sendBroadcast(intent);
                        } catch (RemoteException e) {
                            //do nothing
                        }
                    }
                });
            }
        }

        @Override
        public void clearMediaSources() throws RemoteException {
            if (mediaSource instanceof ConcatenatingMediaSource) {
                ConcatenatingMediaSource concatenatingMediaSource = (ConcatenatingMediaSource) mediaSource;
                concatenatingMediaSource.clear(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSongInfos.clear();
                            Intent intent = new Intent(ACTION);
                            intent.putParcelableArrayListExtra(MUSIC_DATA_CHANGE, new ArrayList<Parcelable>(mSongInfos));
                            intent.putExtra(MUSIC_DATA_INDEX_CHANGE, true);
                            intent.putExtra(MUSIC_DATA_INDEX, getCurrentWindowIndex());
                            sendBroadcast(intent);
                        } catch (RemoteException e) {
                            //do nothing
                        }
                    }
                });
            }
        }

        @Override
        public void addMediaSources(final List<SongInfo> songInfos) throws RemoteException {
            if (mediaSource instanceof ConcatenatingMediaSource) {

                if (songInfos == null) {
                    Log.i(TAG, "song is null");
                    throw new IllegalArgumentException("song is null");
                }

                ConcatenatingMediaSource source = (ConcatenatingMediaSource) mediaSource;
                final int index = getCurrentWindowIndex();
                source.addMediaSources(Arrays.asList(createMediaSources(songInfos)), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            mSongInfos.addAll(songInfos);
                            Intent intent = new Intent(ACTION);
                            intent.putParcelableArrayListExtra(MUSIC_DATA_CHANGE, new ArrayList<Parcelable>(mSongInfos));
                            if (index != getCurrentWindowIndex()) {
                                intent.putExtra(MUSIC_DATA_INDEX_CHANGE, true);
                                intent.putExtra(MUSIC_DATA_INDEX, getCurrentWindowIndex());
                            } else {
                                intent.putExtra(MUSIC_DATA_INDEX_CHANGE, false);
                            }
                            sendBroadcast(intent);
                        } catch (RemoteException e) {
                            //do nothing
                        }
                    }
                });
            }
        }

        @Override
        public void play() throws RemoteException {
            checkPlayerNotNull();

            if (mediaSource != null) {
                if (getPlaybackState() == Player.STATE_ENDED
                        || getPlaybackState() == Player.STATE_IDLE) {
                    player.prepare(mediaSource);
                }
                player.setPlayWhenReady(true);
            }
        }

        @Override
        public void playIndex(int index) throws RemoteException {
            checkPlayerNotNull();
            player.seekToDefaultPosition(index);
            play();
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
        }

        @Override
        public void previous() throws RemoteException {
            checkPlayerNotNull();
            seekToIndex(getPreviousWindowIndex(), 0);
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
        public void setPlayMode(int playMode) throws RemoteException {
            checkPlayerNotNull();
            if (playMode != PLAY_MODE_SHUFFLE) {
                player.setRepeatMode(playMode);
            } else {
                player.setRepeatMode(PLAY_MODE_ALL);
                player.setShuffleModeEnabled(true);
            }
            mPlayMode = playMode;
        }

        @Override
        public int getPlayMode() throws RemoteException {
            checkPlayerNotNull();
            return mPlayMode;
        }

        @Override
        public int getPlaybackState() throws RemoteException {
            checkPlayerNotNull();
            return player.getPlaybackState();
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
            play();
        }

        @Override
        public void seekToIndex(int windowIndex, long positionMs) throws RemoteException {
            checkPlayerNotNull();
            player.seekTo(windowIndex, positionMs);
            play();
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
