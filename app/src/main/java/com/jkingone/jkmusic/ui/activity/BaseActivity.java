package com.jkingone.jkmusic.ui.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
;
import com.jkingone.jkmusic.MusicManagerService;
import com.jkingone.jkmusic.ui.mvp.BasePresenter;
import com.jkingone.jkmusic.ui.fragment.PlayFragment;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {

    protected P mPresenter;

    private PlayFragment mPlayFragment;

    protected MusicManagerService mMusicManagerService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = createPresenter();
        mMusicManagerService = new MusicManagerService(this);
    }

    public abstract P createPresenter();

    protected void setPlayFragment(int resId) {
        if (mPlayFragment == null) {
            mPlayFragment = PlayFragment.newInstance("PlayFragment");
        }
        getSupportFragmentManager().beginTransaction().add(resId, mPlayFragment).commit();
    }

    protected void exeBindService(){
        mMusicManagerService.exeBindService();
    }

    protected void exeUnbindService(){
        mMusicManagerService.exeUnbindService();
    }

    public MusicManagerService getMusicManagerService() {
        return mMusicManagerService;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
