package com.jkingone.jkmusic.service;

import android.app.Service;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.jkingone.jkmusic.IMusicInterface;
import com.jkingone.jkmusic.entity.SongInfo;

import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;


/**
 * Created by Jkingone at 2018/7/30
 */
public class MusicManagerService extends IMusicInterface.Stub implements IMediaPlayer.OnBufferingUpdateListener,
        IMediaPlayer.OnCompletionListener, IMediaPlayer.OnErrorListener, IMediaPlayer.OnInfoListener,
        IMediaPlayer.OnPreparedListener, IMediaPlayer.OnSeekCompleteListener {

    private static final String TAG = "MusicService";

    public static final String ACTION = "com.jkingone.jkmusic.music.action";

    public static final int PLAY_MODE_SHUFFLE = 0;
    public static final int PLAY_MODE_ALL = 1;
    public static final int PLAY_MODE_ONE = 2;

    public static final int NO_POSITION = Integer.MIN_VALUE;

    private Service mService;

    private IjkMediaPlayer mIjkMediaPlayer;

    private List<SongInfo> mMediaSources = new ArrayList<>();
    private List<SongInfo> mShuffleMediaSources = new ArrayList<>();

    private int mPlayMode = PLAY_MODE_ALL;

    private int mCurrentIndex = 0;

    public static MusicManagerService createMusicManagerService(Service service) {
        return new MusicManagerService(service);
    }

    private MusicManagerService(Service service) {

        mService = service;

    }

    private void init() {
        mIjkMediaPlayer = new IjkMediaPlayer();
        mIjkMediaPlayer.setOnCompletionListener(this);
        mIjkMediaPlayer.setOnErrorListener(this);
        mIjkMediaPlayer.setOnInfoListener(this);
        mIjkMediaPlayer.setOnSeekCompleteListener(this);
        mIjkMediaPlayer.setOnBufferingUpdateListener(this);
        mIjkMediaPlayer.setOnPreparedListener(this);
        mIjkMediaPlayer.reset();
    }

    //==============================================================================================
    // mediaSource
    //==============================================================================================

    @Override
    public void prepareMediaSources(List<SongInfo> songInfos) {

        if (songInfos == null) {
            Log.i(TAG, "song is null");
            return;
        }

        mMediaSources.clear();
        mMediaSources.addAll(songInfos);

        mShuffleMediaSources.clear();
        mShuffleMediaSources.addAll(mMediaSources);
        MusicManager.shuffleMediaSources(mShuffleMediaSources);

    }

    @Override
    public void addMediaSource(final SongInfo songInfo) {
        if (songInfo != null) {
            mMediaSources.add(songInfo);
            mShuffleMediaSources.add(songInfo);
        }
    }

    @Override
    public void removeMediaSource(int index) {
        if (index < 0 || index >= mMediaSources.size()) {
            return;
        }

        SongInfo songInfo = mMediaSources.remove(index);
        mShuffleMediaSources.remove(songInfo);
    }

    @Override
    public void clearMediaSources() {
        mMediaSources.clear();
        mShuffleMediaSources.clear();
    }

    @Override
    public void addMediaSources(final List<SongInfo> songInfos) {
        if (!songInfos.isEmpty()) {
            mMediaSources.addAll(songInfos);

            mShuffleMediaSources.clear();
            mShuffleMediaSources.addAll(mMediaSources);

            MusicManager.shuffleMediaSources(mShuffleMediaSources);
        }
    }

    //==============================================================================================
    // common
    //==============================================================================================

    @Override
    public void play() {
        if (checkPlayListAndIndex()) {
            return;
        }

        String path = mMediaSources.get(mCurrentIndex).getUrl();

        if (TextUtils.isEmpty(path)) {
            return;
        }

        if (mIjkMediaPlayer != null) {
            release();
        }

        init();

        try {
            mIjkMediaPlayer.setDataSource(path);
            mIjkMediaPlayer.prepareAsync();
            mIjkMediaPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "play: ", e);
        }
    }

    @Override
    public void playIndex(int index) {

        if (index < 0 || index >= mMediaSources.size()) {
            return;
        }
        mCurrentIndex = index;

        play();
    }

    @Override
    public void start() {
        try {
            if (mIjkMediaPlayer == null) {
                play();
            } else {
                mIjkMediaPlayer.start();
            }
        } catch (Exception e) {
            Log.e(TAG, "start: ", e);
        }
    }

    @Override
    public void pause() {
        try {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.pause();
            }
        } catch (Exception e) {
            Log.e(TAG, "pause: ", e);
        }
    }

    @Override
    public void next() {
        if (checkPlayListAndIndex()) {
            return;
        }

        mCurrentIndex++;
        if (mCurrentIndex == mMediaSources.size()) {
            mCurrentIndex = 0;
        }

        play();
    }

    @Override
    public void previous() {
        if (checkPlayListAndIndex()) {
            return;
        }
        mCurrentIndex--;
        if (mCurrentIndex == -1) {
            mCurrentIndex = mMediaSources.size() - 1;
        }

        play();
    }

    @Override
    public boolean isPlaying() {
        return mIjkMediaPlayer != null && mIjkMediaPlayer.isPlaying();
    }

    @Override
    public void setPlayMode(int playMode) {
        if (mPlayMode != playMode) {
            if (playMode == PLAY_MODE_SHUFFLE) {
                SongInfo songInfo = mMediaSources.get(mCurrentIndex);
                int size = mShuffleMediaSources.size();
                for (int i = 0; i < size; i++) {
                    SongInfo song = mShuffleMediaSources.get(i);
                    if (songInfo.getId().equals(song.getId())) {
                        mCurrentIndex = i;
                        mPlayMode = playMode;
                        return;
                    }
                }
                return;
            }
            if (mPlayMode == PLAY_MODE_SHUFFLE) {
                SongInfo songInfo = mShuffleMediaSources.get(mCurrentIndex);
                int size = mMediaSources.size();
                for (int i = 0; i < size; i++) {
                    SongInfo song = mMediaSources.get(i);
                    if (songInfo.getId().equals(song.getId())) {
                        mCurrentIndex = i;
                        mPlayMode = playMode;
                        return;
                    }
                }
            }
        }
    }

    @Override
    public int getPlayMode() {
        return mPlayMode;
    }

    @Override
    public void seekToPosition(long positionMs) {
        if (checkPlayListAndIndex()) {
            return;
        }

        try {
            if (mIjkMediaPlayer != null && mIjkMediaPlayer.getDataSource() != null) {
                mIjkMediaPlayer.seekTo(positionMs);
            }
        } catch (Exception e) {
            Log.e(TAG, "seekToPosition: ", e);
        }
    }

    @Override
    public void seekToIndex(int index, long positionMs) {
        if (checkPlayListAndIndex()) {
            return;
        }
        if (index < 0 || index >= mMediaSources.size() || positionMs < 0) {
            return;
        }
        mCurrentIndex = index;

        play();

        try {
            if (positionMs != 0) {
                mIjkMediaPlayer.seekTo(positionMs);
            }
        } catch (Exception e) {
            Log.e(TAG, "seekToPosition: ", e);
        }
    }

    @Override
    public void stop() {
        try {
            if (mIjkMediaPlayer != null) {
                mIjkMediaPlayer.stop();
            }
        } catch (Exception e) {
            Log.e(TAG, "stop: ", e);
        }
    }

    @Override
    public void release() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.release();
            mIjkMediaPlayer = null;
        }
    }

    @Override
    public void reset() {
        if (mIjkMediaPlayer != null) {
            mIjkMediaPlayer.reset();
        }
    }

    @Override
    public int getCurrentIndex() {
        if (checkPlayListAndIndex()) {
            return NO_POSITION;
        }

        return mCurrentIndex;
    }

    @Override
    public int getNextIndex() {
        if (checkPlayListAndIndex()) {
            return NO_POSITION;
        }

        int index = mCurrentIndex;

        if (mPlayMode == PLAY_MODE_ONE) {
            return mCurrentIndex;
        }

        index++;
        if (index == mMediaSources.size()) {
            index = 0;
        }
        return index;
    }

    @Override
    public int getPreviousIndex() {
        if (checkPlayListAndIndex()) {
            return NO_POSITION;
        }

        int index = mCurrentIndex;

        if (mPlayMode == PLAY_MODE_ONE) {
            return mCurrentIndex;
        }

        index--;
        if (index == -1) {
            index = mMediaSources.size() - 1;
        }
        return index;
    }

    @Override
    public long getDuration() {
        if (mIjkMediaPlayer == null) {
            return 0;
        }
        return mIjkMediaPlayer.getDuration();
    }

    @Override
    public long getCurrentPosition() {
        if (mIjkMediaPlayer == null) {
            return 0;
        }
        return mIjkMediaPlayer.getCurrentPosition();
    }

    @Override
    public long getBufferedPosition() {
        throw new UnsupportedOperationException("no support");
    }

    @Override
    public int getBufferedPercentage() {
        throw new UnsupportedOperationException("no support");
    }

    private boolean checkPlayListAndIndex() {
        return mMediaSources.isEmpty() || mShuffleMediaSources.isEmpty()
                || mCurrentIndex < 0 || mCurrentIndex >= mMediaSources.size();
    }


    //==============================================================================================
    // Listener
    //==============================================================================================

    public static final String PLAY_STATE = "play_state";
    public static final String SEEK_COMPLETE = "seek_complete";
    public static final String BUFFER_PERCENT = "buffer_percent";
    public static final String COMPLETE = "complete";
    public static final String ERROR = "error";
    public static final String INFO = "info";

    @Override
    public void onPrepared(IMediaPlayer iMediaPlayer) {
        Log.i(TAG, "onPrepared");
        Intent intent = new Intent();
        intent.putExtra(PLAY_STATE, isPlaying());
        mService.sendBroadcast(intent);
    }

    @Override
    public void onSeekComplete(IMediaPlayer iMediaPlayer) {
        Log.i(TAG, "onSeekComplete");

        Intent intent = new Intent();
        intent.putExtra(SEEK_COMPLETE, SEEK_COMPLETE);
        mService.sendBroadcast(intent);
    }

    @Override
    public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int percent) {
        Log.i(TAG, "onBufferingUpdate: " + percent);
        Intent intent = new Intent();
        intent.putExtra(BUFFER_PERCENT, percent);
        mService.sendBroadcast(intent);
    }

    @Override
    public void onCompletion(IMediaPlayer iMediaPlayer) {
        Log.i(TAG, "onCompletion");
        Intent intent = new Intent();
        intent.putExtra(COMPLETE, COMPLETE);
        mService.sendBroadcast(intent);
    }

//    int MEDIA_ERROR_UNKNOWN = 1;
//    int MEDIA_ERROR_SERVER_DIED = 100;
//    int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;
//    int MEDIA_ERROR_IO = -1004;
//    int MEDIA_ERROR_MALFORMED = -1007;
//    int MEDIA_ERROR_UNSUPPORTED = -1010;
//    int MEDIA_ERROR_TIMED_OUT = -110;
    @Override
    public boolean onError(IMediaPlayer iMediaPlayer, int what, int extra) {
        Log.i(TAG, "onError: " + what + " " + extra);
        Intent intent = new Intent();
        intent.putExtra(ERROR, what);
        mService.sendBroadcast(intent);
        return true;
    }

//    int MEDIA_INFO_UNKNOWN = 1;
//    int MEDIA_INFO_STARTED_AS_NEXT = 2;
//    int MEDIA_INFO_BUFFERING_START = 701;
//    int MEDIA_INFO_BUFFERING_END = 702;
//    int MEDIA_INFO_NETWORK_BANDWIDTH = 703;
//    int MEDIA_INFO_BAD_INTERLEAVING = 800;
//    int MEDIA_INFO_NOT_SEEKABLE = 801;
//    int MEDIA_INFO_METADATA_UPDATE = 802;
//    int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
//    int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;
//    int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;
//    int MEDIA_INFO_AUDIO_RENDERING_START = 10002;
//    int MEDIA_INFO_AUDIO_DECODED_START = 10003;
//    int MEDIA_INFO_OPEN_INPUT = 10005;
//    int MEDIA_INFO_FIND_STREAM_INFO = 10006;
//    int MEDIA_INFO_COMPONENT_OPEN = 10007;
//    int MEDIA_INFO_AUDIO_SEEK_RENDERING_START = 10009;
//    int MEDIA_INFO_MEDIA_ACCURATE_SEEK_COMPLETE = 10100;
    @Override
    public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
        Log.i(TAG, "onInfo: " + what + " " + extra);
        Intent intent = new Intent();
        intent.putExtra(INFO, what);
        mService.sendBroadcast(intent);
        return true;
    }
}
