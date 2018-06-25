package com.jkingone.jkmusic;

import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.jkingone.jkmusic.service.MusicService;

/**
 * Created by Administrator on 2018/6/25.
 */

public class MusicBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MusicBroadcastReceiver";

    public static final String ACTION = "com.jkingone.jkmusic.music";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Log.i(TAG, "onReceive: ");
        }
    }
}
