package com.jkingone.jkmusic.service;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.jkingone.jkmusic.IMusicInterface;
import com.jkingone.jkmusic.MusicBroadcastReceiver;
import com.jkingone.jkmusic.data.entity.SongInfo;

import java.util.List;

public class MusicService extends Service {

    private static final String TAG = "MusicService";

//    public static final String ACTION_PLAY = "com.jkingone.jkmusic.service.play";
    public static final String EXTRA_PLAY = "com.jkingone.jkmusic.service.play";

    public MusicService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new MusicManager();
    }

    private class MusicManager extends IMusicInterface.Stub {


        @Override
        public void add(int index, List<SongInfo> songInfos) throws RemoteException {

        }

        @Override
        public void play() throws RemoteException {
            Log.i(TAG, "play: ");
            Intent intent = new Intent(MusicBroadcastReceiver.ACTION);
            intent.putExtra(EXTRA_PLAY, EXTRA_PLAY);
            sendBroadcast(intent);
        }

        @Override
        public void pause() throws RemoteException {

        }

        @Override
        public void next() throws RemoteException {

        }

        @Override
        public void previous() throws RemoteException {

        }
    }
}
