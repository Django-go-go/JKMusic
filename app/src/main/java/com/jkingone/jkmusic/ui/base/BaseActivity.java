package com.jkingone.jkmusic.ui.base;

import android.support.v7.app.AppCompatActivity;
import com.jkingone.jkmusic.ui.fragment.PlayFragment;

public abstract class BaseActivity extends AppCompatActivity {

    private PlayFragment mPlayFragment;

    protected void setPlayFragment(int resId) {
        if (mPlayFragment == null) {
            mPlayFragment = PlayFragment.newInstance("PlayFragment");
        }
        getSupportFragmentManager().beginTransaction().add(resId, mPlayFragment).commit();
    }

}
