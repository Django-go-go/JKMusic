package com.jkingone.jkmusic.ui.mvp.presenter;

import com.jkingone.jkmusic.ui.mvp.base.BasePresenter;
import com.jkingone.jkmusic.ui.mvp.contract.LocalFragContract;

public class LocalFragPresenter extends BasePresenter<LocalFragContract.ViewCallback, LocalFragContract.Model> {

    public LocalFragPresenter(LocalFragContract.ViewCallback viewCallback) {
        super();
        attachView(viewCallback);
    }

    @Override
    public LocalFragContract.Model createModel() {
        return null;
    }
}
