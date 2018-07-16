package com.jkingone.jkmusic.ui.mvp;

import com.jkingone.jkmusic.ui.mvp.contract.ArtistContract;

public class ArtistPresenter extends BasePresenter<ArtistContract.ViewCallback, ArtistContract.Model> {

    public ArtistPresenter(ArtistContract.ViewCallback viewCallback) {
        super();
        attachView(viewCallback);
    }



    @Override
    public ArtistContract.Model createModel() {
        return null;
    }
}
