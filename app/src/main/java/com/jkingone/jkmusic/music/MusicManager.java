package com.jkingone.jkmusic.music;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jkingone.jkmusic.IMusicInterface;
import com.jkingone.jkmusic.MusicBroadcastReceiver;
import com.jkingone.jkmusic.entity.SongInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MusicManager {

    private static final String TAG = "MusicManager";
    private static final int SEED = 10;

    private int mPlayMode = PLAY_MODE_ALL;

    private int mCurrentIndex = 0;

    public static final int PLAY_MODE_SHUFFLE = 0;
    public static final int PLAY_MODE_ALL = 1;
    public static final int PLAY_MODE_ONE = 2;
    public static final int NO_POSITION = Integer.MIN_VALUE;


    private IMusicInterface mProxy;

    private boolean isBound = false;

    private MusicBroadcastReceiver mMusicBroadcastReceiver;

    private ServiceConnectionListener mConnectionListener;

    private Application mApplication;

    private List<SongInfo> mMediaSources;
    private List<SongInfo> mShuffleMediaSources;

    private volatile static MusicManager sInstance;

    public static MusicManager getInstance() {
        if (sInstance == null) {
            synchronized (MusicManager.class) {
                if (sInstance == null) {
                    sInstance = new MusicManager();
                }
            }
        }

        return sInstance;
    }

    public interface ServiceConnectionListener {
        void onConnected();
    }

    private MusicManager() {
        mMediaSources = new ArrayList<>();
        mShuffleMediaSources = new ArrayList<>();
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mProxy = IMusicInterface.Stub.asInterface(service);
            isBound = true;
            if (mConnectionListener != null) {
                mConnectionListener.onConnected();
            }
            registerMusicBroadcast();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mProxy = null;
            isBound = false;
            mConnectionListener = null;
            unregisterMusicBroadcast();
        }
    };

    public void init(Application application) {

        Log.i(TAG, "init");

        if (application == null) {
            throw new NullPointerException("context must not null");
        }
        mApplication = application;

        bindService();

        Log.i(TAG, "init is done");
    }

    public void destroy() {
        Log.i(TAG, "destroy start");

        unbindService();

        Log.i(TAG, "destroy is done");
    }

    private void bindService() {
        if(!isBound){
            Intent intent = new Intent(mApplication, MusicService.class);
            mApplication.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    private void unbindService() {
        if(isBound){
            mApplication.unbindService(mConnection);
            isBound = false;
        }
    }

    public synchronized void setServiceConnectionListener(ServiceConnectionListener listener) {
        mConnectionListener = listener;
        if (mProxy != null && mConnectionListener != null) {
            mConnectionListener.onConnected();
        }
    }

    public void addMediaPlayerCallback(MusicBroadcastReceiver.MediaPlayerCallback mediaPlayerCallback) {
        if (mMusicBroadcastReceiver == null) {
            mMusicBroadcastReceiver = new MusicBroadcastReceiver();
        }
        mMusicBroadcastReceiver.addMediaPlayerCallback(mediaPlayerCallback);
    }

    public void removeMediaPlayerCallback(MusicBroadcastReceiver.MediaPlayerCallback callback) {
        if (mMusicBroadcastReceiver != null) {
            mMusicBroadcastReceiver.removeMediaPlayerCallback(callback);
        }
    }

    private void registerMusicBroadcast() {
        if (mMusicBroadcastReceiver == null) {
            mMusicBroadcastReceiver = new MusicBroadcastReceiver();
        }

        mApplication.registerReceiver(mMusicBroadcastReceiver, new IntentFilter(MusicManagerService.ACTION));
    }

    private void unregisterMusicBroadcast() {
        if (mMusicBroadcastReceiver != null) {
            mApplication.unregisterReceiver(mMusicBroadcastReceiver);
            mMusicBroadcastReceiver.clearMediaPlayerCallback();
        }
    }

    //==============================================================================================
    // Impl
    //==============================================================================================

    public void prepareMediaSources(List<SongInfo> songInfos) {
        if (!songInfos.isEmpty()) {
            mMediaSources.clear();
            mMediaSources.addAll(songInfos);

            mShuffleMediaSources.clear();
            mShuffleMediaSources.addAll(songInfos);
            shuffleMediaSources(mShuffleMediaSources);
        }
    }

    public void addMediaSource(SongInfo songInfo) {
        if (songInfo != null) {
            mMediaSources.add(songInfo);
            mShuffleMediaSources.add(songInfo);
        }
    }

    public void removeMediaSource(int index, boolean isShuffle) {
        if (index < 0 || index >= mMediaSources.size()) {
            return;
        }

        if (!isShuffle) {
            SongInfo songInfo = mMediaSources.remove(index);
            mShuffleMediaSources.remove(songInfo);
        } else {
            SongInfo songInfo = mShuffleMediaSources.remove(index);
            mMediaSources.remove(songInfo);
        }

    }

    public void clearMediaSources() {
        mMediaSources.clear();
        mShuffleMediaSources.clear();
    }

    public void addMediaSources(List<SongInfo> songInfos) {
        if (!songInfos.isEmpty()) {
            mMediaSources.addAll(songInfos);

            mShuffleMediaSources.clear();
            mShuffleMediaSources.addAll(mMediaSources);
            shuffleMediaSources(mShuffleMediaSources);
        }
    }

    public void updateMediaSource(int index, SongInfo mediaSource, boolean isShuffle) {
        if (index < 0 || index >= mMediaSources.size()) {
            return;
        }

        if (!isShuffle) {
            SongInfo prev = mMediaSources.set(index, mediaSource);
            mShuffleMediaSources.set(mShuffleMediaSources.indexOf(prev), mediaSource);
        } else {
            SongInfo prev = mShuffleMediaSources.set(index, mediaSource);
            mMediaSources.set(mMediaSources.indexOf(prev), mediaSource);
        }

    }

    public int indexForPlayMode(SongInfo mediaSource) {
        if (mPlayMode == PLAY_MODE_SHUFFLE) {
            return mShuffleMediaSources.indexOf(mediaSource);
        }
        return mMediaSources.indexOf(mediaSource);
    }

    public List<SongInfo> getMediaSources() {
        return mMediaSources;
    }

    public List<SongInfo> getMediaSourcesForPlayMode() {
        int playMode = getPlayMode();
        if (playMode == PLAY_MODE_ONE) {
            List<SongInfo> oneMediaSource = new ArrayList<>();
            int index = getCurrentIndex();
            if (index != NO_POSITION) {
                oneMediaSource.add(mMediaSources.get(index));
            }
            return oneMediaSource;
        }
        if (playMode == PLAY_MODE_SHUFFLE) {
            return mShuffleMediaSources;
        }
        return mMediaSources;
    }

    private boolean checkPlayListAndIndex() {
        return mMediaSources.isEmpty() || mShuffleMediaSources.isEmpty()
                || mCurrentIndex < 0 || mCurrentIndex >= mMediaSources.size();
    }

    private static void shuffleMediaSources(List<SongInfo> songInfos) {
        Random random = new Random(SEED);
        Collections.shuffle(songInfos, random);
    }

    public void playIndex(int index) {

        if (index < 0 || index >= mMediaSources.size()) {
            return;
        }
        mCurrentIndex = index;

        play();
    }

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

    public void setPlayMode(int playMode) {
        if (mPlayMode != playMode) {
            if (playMode == PLAY_MODE_SHUFFLE) {
                SongInfo mediaSource = mMediaSources.get(mCurrentIndex);
                int size = mShuffleMediaSources.size();
                for (int i = 0; i < size; i++) {
                    SongInfo song = mShuffleMediaSources.get(i);
                    if (mediaSource.getId().equals(song.getId())) {
                        mCurrentIndex = i;
                        mPlayMode = playMode;
                        return;
                    }
                }
                return;
            }
            if (mPlayMode == PLAY_MODE_SHUFFLE) {
                SongInfo mediaSource = mShuffleMediaSources.get(mCurrentIndex);
                int size = mMediaSources.size();
                for (int i = 0; i < size; i++) {
                    SongInfo song = mMediaSources.get(i);
                    if (mediaSource.getId().equals(song.getId())) {
                        mCurrentIndex = i;
                        mPlayMode = playMode;
                        return;
                    }
                }
            }
        }
    }

    public int getPlayMode() {
        return mPlayMode;
    }

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
                seekTo(positionMs);
            }
        } catch (Exception e) {
            Log.e(TAG, "seekToPosition: ", e);
        }
    }

    public int getCurrentIndex() {
        if (checkPlayListAndIndex()) {
            return NO_POSITION;
        }

        return mCurrentIndex;
    }

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

    public void prepare() {
        if (mProxy != null && !checkPlayListAndIndex()) {
            try {
                String path = mMediaSources.get(mCurrentIndex).getUrl();
                mProxy.prepare(path);
            } catch (RemoteException e) {
                // do nothing
            }
        }
    }

    public void play() {
        prepare();
        start();
    }

    public void start() {
        if (mProxy != null) {
            try {
                mProxy.start();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void pause() {
        if (mProxy != null) {
            try {
                mProxy.pause();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public boolean isPlaying() {
        if (mProxy != null) {
            try {
                return mProxy.isPlaying();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return false;
    }

    public void seekTo(long positionMs) {
        if (mProxy != null) {
            try {
                mProxy.seekTo(positionMs);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public long getDuration() {
        if (mProxy != null) {
            try {
                return mProxy.getDuration();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return 0;
    }

    public long getCurrentPosition() {
        if (mProxy != null) {
            try {
                return mProxy.getCurrentPosition();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return 0;
    }
}
