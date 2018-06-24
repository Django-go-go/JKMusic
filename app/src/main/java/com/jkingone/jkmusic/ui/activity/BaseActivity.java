package com.jkingone.jkmusic.ui.activity;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
;
import com.jkingone.jkmusic.ui.activity.presenter.BasePresenter;

public abstract class BaseActivity<V, T extends BasePresenter<V>> extends AppCompatActivity {


    private boolean isBound = false;
    T mPresenter;

    private boolean isOne = true;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mPresenter.attachView((V) this);
    }

    public abstract T createPresenter();

    private ServiceConnection MyServiceConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
//            MusicService.ServiceBind mBinder = (MusicService.ServiceBind) service;
//            playService = mBinder.getService();
//            playService.registerUICallBack(BaseActivity.this);
//
//            if (SharedPreferencesUtils.getParam(BaseActivity.this, "curpos", -1) != null){
//                int cur = (int) SharedPreferencesUtils.getParam(BaseActivity.this, "curpos", -1);
//                if (cur != -1){
//                    List<Mp3Info> mp3Infos = new ArrayList<>();
//                    List<SongRecorder> songRecorders = GreenDaoUtils.queryAll2(BaseActivity.this);
//                    GreenDaoUtils.deleteAll2(BaseActivity.this);
//                    if (songRecorders == null){
//                        songRecorders = new ArrayList<>();
//                    }
//                    for (int i = 0; i < songRecorders.size(); i++){
//                        mp3Infos.add(OtherUtils.SongRecorderToMp3Info(songRecorders.get(i)));
//                    }
//
//                    if (playService != null){
//                        playService.setCurrPosition(cur);
//                        playService.setPlaylist(mp3Infos);
//                        SharedPreferencesUtils.setParam(BaseActivity.this, "curpos", -1);
//                        songRecorders.clear();
//                        mp3Infos.clear();
//                    }else {
//                        System.out.println("==================================>");
//                    }
//                }
//
//            }

//            playService.notifyUI(playService.getCurrPosition(), playService.getPlaylist());
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
//            playService.unRegisterUICallBack(BaseActivity.this);
//            playService = null;
//            isBound = false;
        }
    };

    public void exeBindService(){
        if(!isBound){
//            Intent intent = new Intent(this, MusicService.class);
//            bindService(intent, MyServiceConn, Context.BIND_AUTO_CREATE);
//            isBound = true;
        }
    }

    public void exeUnbindService(){
        if(isBound){
            unbindService(MyServiceConn);
            isBound = false;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

}
