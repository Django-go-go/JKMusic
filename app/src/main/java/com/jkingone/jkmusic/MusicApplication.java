package com.jkingone.jkmusic;

import android.app.Application;

import com.jkingone.jkmusic.music.MusicManager;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MusicApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        IjkMediaPlayer.native_setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);

        MusicManager.getInstance().init(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        IjkMediaPlayer.native_profileEnd();
        MusicManager.getInstance().destroy();
    }
}
