package com.jkingone.jkmusic.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
;
import com.jkingone.jkmusic.IMusicInterface;
import com.jkingone.jkmusic.MusicBroadcastReceiver;
import com.jkingone.jkmusic.data.entity.SongInfo;
import com.jkingone.jkmusic.service.MusicService;
import com.jkingone.jkmusic.ui.activity.presenter.BasePresenter;
import com.jkingone.jkmusic.ui.fragment.PlayFragment;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {

    private static final String TAG = "BaseActivity";

    protected IMusicInterface playService;
    private boolean isBound = false;
    protected T mPresenter;

    protected MusicBroadcastReceiver mMusicBroadcastReceiver;

    private PlayFragment mPlayFragment = PlayFragment.newInstance("PlayFragment");

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        if (mPresenter != null) {
            mPresenter.attachView((V) this);
        }
    }

    public abstract T createPresenter();

    protected void setPlayFragment(int resId) {
        getSupportFragmentManager().beginTransaction().add(resId, mPlayFragment).commit();
    }

    private ServiceConnection MyServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = IMusicInterface.Stub.asInterface(service);
            try {
                playService.play();
            } catch (RemoteException e) {
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
            isBound = false;
        }
    };

    protected void exeBindService(){
        if(!isBound){
            Intent intent = new Intent(this, MusicService.class);
            bindService(intent, MyServiceConn, Context.BIND_AUTO_CREATE);
            isBound = true;
        }
    }

    protected void exeUnbindService(){
        if(isBound){
            unbindService(MyServiceConn);
            isBound = false;
        }
    }

    protected void registerMusicBroadcast() {
        if (mMusicBroadcastReceiver == null) {
            mMusicBroadcastReceiver = new MusicBroadcastReceiver();
        }

        registerReceiver(mMusicBroadcastReceiver, new IntentFilter(MusicBroadcastReceiver.ACTION));
    }

    protected void unregisterMusicBroadcast() {
        if (mMusicBroadcastReceiver != null) {
            unregisterReceiver(mMusicBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
}
