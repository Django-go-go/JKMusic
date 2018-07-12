package com.jkingone.jkmusic;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.jkingone.jkmusic.entity.SongInfo;
import com.jkingone.jkmusic.service.MusicService;

import java.util.List;

public class MusicManagerService implements IMusicInterface {

    private IMusicInterface mIMusicInterface;
    private boolean isBound = false;

    private MusicBroadcastReceiver mMusicBroadcastReceiver;
    private MusicBroadcastReceiver.PlayCallback mPlayCallback;

    private BindServiceCallback mBindServiceCallback;

    private Context mContext;

    public interface BindServiceCallback {
        void updateFirst();
    }

    public MusicManagerService(Context context) {
        mContext = context;
    }

    private ServiceConnection MyServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mIMusicInterface = IMusicInterface.Stub.asInterface(service);
            if (mBindServiceCallback != null) {
                mBindServiceCallback.updateFirst();
            }
            registerMusicBroadcast();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIMusicInterface = null;
            isBound = false;
            if (mBindServiceCallback != null) {
                mBindServiceCallback = null;
            }
            unregisterMusicBroadcast();
        }
    };

    public boolean checkIsNull() {
        return mIMusicInterface == null;
    }

    public void exeBindService() {
        if(!isBound){
            Intent intent = new Intent(mContext, MusicService.class);
            mContext.bindService(intent, MyServiceConn, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    public void exeUnbindService() {
        if(isBound){
            mContext.unbindService(MyServiceConn);
            isBound = false;
        }
    }

    public void setBindServiceCallback(BindServiceCallback bindServiceCallback) {
        mBindServiceCallback = bindServiceCallback;
    }

    public void setPlayCallback(MusicBroadcastReceiver.PlayCallback playCallback) {
        mPlayCallback = playCallback;
    }

    private void registerMusicBroadcast() {
        if (mMusicBroadcastReceiver == null) {
            mMusicBroadcastReceiver = new MusicBroadcastReceiver(mPlayCallback);
        }

        mContext.registerReceiver(mMusicBroadcastReceiver, new IntentFilter(MusicService.ACTION));
    }

    private void unregisterMusicBroadcast() {
        if (mMusicBroadcastReceiver != null) {
            mContext.unregisterReceiver(mMusicBroadcastReceiver);
        }
    }

    @Override
    public void prepareMediaSources(List<SongInfo> songInfos) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.prepareMediaSources(songInfos);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void addMediaSource(SongInfo songInfo) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.addMediaSource(songInfo);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public List<SongInfo> getMediaSources() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getMediaSources();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return null;
    }

    @Override
    public void removeMediaSource(int index) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.removeMediaSource(index);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void clearMediaSources() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.clearMediaSources();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void addMediaSources(List<SongInfo> songInfos) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.addMediaSources(songInfos);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void play() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.play();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void playIndex(int index) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.playIndex(index);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void pause() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.pause();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void next() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.next();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void previous() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.previous();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void initializePlayer() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.initializePlayer();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void releasePlayer() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.releasePlayer();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public boolean isPlaying() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.isPlaying();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return false;
    }

    @Override
    public int getPlaybackState() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getPlaybackState();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public void setRepeatMode(int repeatMode) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.setRepeatMode(repeatMode);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public int getRepeatMode() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getRepeatMode();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return 0;
    }

    @Override
    public boolean isLoading() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.isLoading();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return false;
    }

    @Override
    public void seekTo(long positionMs) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.seekTo(positionMs);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void seekToIndex(int windowIndex, long positionMs) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.seekToIndex(windowIndex, positionMs);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void stop() {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.stop();
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public void stopCanReset(boolean reset) {
        if (mIMusicInterface != null) {
            try {
                mIMusicInterface.stopCanReset(reset);
            } catch (RemoteException e) {
                //do nothing
            }
        }
    }

    @Override
    public int getCurrentWindowIndex() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getCurrentWindowIndex();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public int getNextWindowIndex() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getNextWindowIndex();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public int getPreviousWindowIndex() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getPreviousWindowIndex();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return Integer.MIN_VALUE;
    }

    @Override
    public long getDuration() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getDuration();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return 0;
    }

    @Override
    public long getCurrentPosition() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getCurrentPosition();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return 0;
    }

    @Override
    public long getBufferedPosition() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getBufferedPosition();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return 0;
    }

    @Override
    public int getBufferedPercentage() {
        if (mIMusicInterface != null) {
            try {
                return mIMusicInterface.getBufferedPercentage();
            } catch (RemoteException e) {
                //do nothing
            }
        }
        return 0;
    }

    @Override
    public IBinder asBinder() {
        return null;
    }
}
