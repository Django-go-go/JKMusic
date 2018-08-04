package com.jkingone.jkmusic.service;

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

public class MusicManager {

    private static final String TAG = "MusicManager";
    private static final int SEED = 10;

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
    // Proxy
    //==============================================================================================


    public void prepareMediaSources(List<SongInfo> songInfos) {
        if (!songInfos.isEmpty()) {
            mMediaSources.clear();
            mMediaSources.addAll(songInfos);

            mShuffleMediaSources.clear();
            mShuffleMediaSources.addAll(songInfos);
            shuffleMediaSources(mShuffleMediaSources);

            if (mProxy != null) {
                try {
                    mProxy.prepareMediaSources(songInfos);
                } catch (RemoteException e) {
                    //do nothing
                }
            }
        }
    }

    public void addMediaSource(SongInfo songInfo) {
        if (songInfo != null) {
            mMediaSources.add(songInfo);
            mShuffleMediaSources.add(songInfo);
            if (mProxy != null) {
                try {
                    mProxy.addMediaSource(songInfo);
                } catch (RemoteException e) {
                    //do nothing
                }
            }
        }
    }

    public void removeMediaSource(int index) {
        if (index < 0 || index >= mMediaSources.size()) {
            return;
        }

        SongInfo songInfo = mMediaSources.remove(index);
        mShuffleMediaSources.remove(songInfo);

        if (mProxy != null) {
            try {
                mProxy.removeMediaSource(index);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void clearMediaSources() {
        mMediaSources.clear();
        mShuffleMediaSources.clear();
        if (mProxy != null) {
            try {
                mProxy.clearMediaSources();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    void addMediaSources(List<SongInfo> songInfos) {
        if (!songInfos.isEmpty()) {
            mMediaSources.addAll(songInfos);

            mShuffleMediaSources.clear();
            mShuffleMediaSources.addAll(mMediaSources);
            shuffleMediaSources(mShuffleMediaSources);

            if (mProxy != null) {
                try {
                    mProxy.addMediaSources(songInfos);
                } catch (RemoteException e) {
                    //do nothing
                }
            }
        }
    }

    public List<SongInfo> getMediaSources() {
        return mMediaSources;
    }

    public List<SongInfo> getMediaSourcesForPlayMode() {
        int playMode = getPlayMode();
        if (playMode == MusicManagerService.PLAY_MODE_ONE) {
            List<SongInfo> oneMediaSource = new ArrayList<>();
            int index = getCurrentIndex();
            if (index != MusicManagerService.NO_POSITION) {
                oneMediaSource.add(mMediaSources.get(index));
            }
            return oneMediaSource;
        }
        if (playMode == MusicManagerService.PLAY_MODE_SHUFFLE) {
            return mShuffleMediaSources;
        }
        return mMediaSources;
    }

    public static void shuffleMediaSources(List<SongInfo> songInfos) {
        Random random = new Random(SEED);
        Collections.shuffle(songInfos, random);
    }

    public void play() {
        if (mProxy != null) {
            try {
                mProxy.play();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void playIndex(int index) {
        if (mProxy != null) {
            try {
                mProxy.playIndex(index);
            } catch (RemoteException e) {
                //do nothing
            }
        }
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

    public void next() {
        if (mProxy != null) {
            try {
                mProxy.next();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void previous() {
        if (mProxy != null) {
            try {
                mProxy.previous();
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

    public void setPlayMode(int playMode) {
        if (mProxy != null) {
            try {
                mProxy.setPlayMode(playMode);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public int getPlayMode() {
        if (mProxy != null) {
            try {
                return mProxy.getPlayMode();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return MusicManagerService.PLAY_MODE_ALL;
    }

    public void seekToPosition(long positionMs) {
        if (mProxy != null) {
            try {
                mProxy.seekToPosition(positionMs);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void seekToIndex(int index, long positionMs) {
        if (mProxy != null) {
            try {
                mProxy.seekToIndex(index, positionMs);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void stop() {
        if (mProxy != null) {
            try {
                mProxy.stop();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void release() {
        if (mProxy != null) {
            try {
                mProxy.release();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public void reset() {
        if (mProxy != null) {
            try {
                mProxy.reset();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    public int getCurrentIndex() {
        if (mProxy != null) {
            try {
                return mProxy.getCurrentIndex();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return MusicManagerService.NO_POSITION;
    }

    public int getNextIndex() {
        if (mProxy != null) {
            try {
                return mProxy.getNextIndex();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return MusicManagerService.NO_POSITION;
    }

    public int getPreviousIndex() {
        if (mProxy != null) {
            try {
                return mProxy.getPreviousIndex();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return MusicManagerService.NO_POSITION;
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

    public long getBufferedPosition() {
        throw new UnsupportedOperationException();
    }

    public int getBufferedPercentage() {
        throw new UnsupportedOperationException();
    }
}
