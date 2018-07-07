package com.jkingone.jkmusic.ui.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
;
import com.jkingone.jkmusic.IMusicInterface;
import com.jkingone.jkmusic.service.MusicService;
import com.jkingone.jkmusic.ui.mvp.BasePresenter;
import com.jkingone.jkmusic.ui.fragment.PlayFragment;

import java.lang.ref.WeakReference;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {

    protected IMusicInterface playService;
    private boolean isBound = false;
    protected P mPresenter;

    protected FragMusicBroadcastReceiver mMusicBroadcastReceiver;

    private PlayFragment mPlayFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
    }

    public abstract P createPresenter();

    protected void setPlayFragment(int resId) {
        if (mPlayFragment == null) {
            mPlayFragment = PlayFragment.newInstance("PlayFragment");
        }
        getSupportFragmentManager().beginTransaction().add(resId, mPlayFragment).commit();
    }

    private ServiceConnection MyServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = IMusicInterface.Stub.asInterface(service);
            updateForFirstConnect();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
            isBound = false;
        }
    };

    protected void updateForFirstConnect() {}

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

    public IMusicInterface getIMusicInterface() {
        return playService;
    }

    protected void registerFragMusicBroadcast() {
        if (mMusicBroadcastReceiver == null) {
            mMusicBroadcastReceiver = new FragMusicBroadcastReceiver(new WeakReference<>(mPlayFragment));
        }

        registerReceiver(mMusicBroadcastReceiver, new IntentFilter(MusicService.ACTION));
    }

    protected void unregisterFragMusicBroadcast() {
        if (mMusicBroadcastReceiver != null) {
            unregisterReceiver(mMusicBroadcastReceiver);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

    private static class FragMusicBroadcastReceiver extends BroadcastReceiver {

        private WeakReference<PlayFragment> mPlayFragment;

        FragMusicBroadcastReceiver(WeakReference<PlayFragment> playFragment) {
            mPlayFragment = playFragment;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicService.ACTION.equals(intent.getAction())) {
                if (mPlayFragment.get() != null) {
                    mPlayFragment.get().updateUI(intent);
                }
            }
        }
    }

}
