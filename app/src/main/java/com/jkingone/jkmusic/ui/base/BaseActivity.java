package com.jkingone.jkmusic.ui.base;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
;
import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.fragment.PlayFragment;

public abstract class BaseActivity<P extends BasePresenter> extends AppCompatActivity {

    protected P mPresenter;

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPresenter != null) {
            mPresenter.detachView();
        }
    }

}
