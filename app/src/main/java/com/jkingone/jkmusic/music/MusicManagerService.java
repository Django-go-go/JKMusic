package com.jkingone.jkmusic.music;

import android.app.Service;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.jkingone.jkmusic.IMusicInterface;

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

    private Service mService;

    private IjkMediaPlayer mIjkMediaPlayer;

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

    @Override
    public void prepare(String path) {
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
        } catch (Exception e) {
            Log.e(TAG, "play: ", e);
        }
    }

    @Override
    public void start() {
        try {
            if (mIjkMediaPlayer != null && mIjkMediaPlayer.getDataSource() != null) {
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
    public boolean isPlaying() {
        return mIjkMediaPlayer != null && mIjkMediaPlayer.isPlaying();
    }

    @Override
    public void seekTo(long positionMs) {
        try {
            if (mIjkMediaPlayer != null && mIjkMediaPlayer.getDataSource() != null) {
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
