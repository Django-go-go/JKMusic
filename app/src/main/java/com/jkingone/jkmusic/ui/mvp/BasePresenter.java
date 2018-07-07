package com.jkingone.jkmusic.ui.mvp;

import com.jkingone.jkmusic.ui.mvp.contract.BaseContract;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/6/17.
 */

public abstract class BasePresenter<V extends BaseContract.BaseView, M extends BaseContract.BaseModel> {
    private Reference<V> mViewRef;
    protected M mModel;

    public BasePresenter() {
        mModel = createModel();
    }

    protected void attachView(V view){
        mViewRef = new WeakReference<V>(view);
    }

    protected V getView(){
        return mViewRef.get();
    }

    public boolean isViewAttached(){
        return mViewRef != null && mViewRef.get() != null;
    }

    public void detachView(){
        if(mViewRef != null){
            mViewRef.clear();
            mViewRef = null;
        }
    }

    public abstract M createModel();

}


