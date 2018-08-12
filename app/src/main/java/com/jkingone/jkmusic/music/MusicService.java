package com.jkingone.jkmusic.music;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

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