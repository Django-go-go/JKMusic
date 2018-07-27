package com.jkingone.jkmusic.ui.mvp.contract;

import com.jkingone.jkmusic.entity.Album;
import com.jkingone.jkmusic.entity.AlbumList;
import com.jkingone.jkmusic.entity.Artist;
import com.jkingone.jkmusic.ui.mvp.base.BaseContract;

import java.util.List;

import retrofit2.Call;

public interface AlbumAndArtistContract {
    interface ViewCallback extends BaseContract.BaseView {
        void showAlbum(Album album);
        void showArtistInfo(Artist.ArtistInfo artistInfo);
        void showArtistSong(List<Artist.Song> songlist);
    }

    interface Model extends BaseContract.BaseModel {
        Call<Album> getAlbum(String albumId);
        Call<Artist.ArtistInfo> getArtistInfo(String tingUid, String artistId);
        Call<List<Artist.Song>> getArtistSong(String tingUid, String artistId, int offset, int limit);
    }
}
