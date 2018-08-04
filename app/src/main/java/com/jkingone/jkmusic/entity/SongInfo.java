package com.jkingone.jkmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.jkingone.jkmusic.Constant;

/**
 * Created by Administrator on 2017/1/22.
 */
public class SongInfo implements Parcelable {
    private String id; // 歌曲ID 3
    private String title; // 歌曲名称 0
    private String album; // 专辑 7
    private long albumId;//专辑ID 6
    private String artist; // 歌手名称 2
    private long duration; // 歌曲时长 1
    private long size; // 歌曲大小 8
    private String url; // 歌曲路径 5
    private String picUrl;
    private int type;
    private boolean isPlaying = false;

    public SongInfo() {
        super();
    }

    protected SongInfo(Parcel in) {
        id = in.readString();
        title = in.readString();
        album = in.readString();
        albumId = in.readLong();
        artist = in.readString();
        duration = in.readLong();
        size = in.readLong();
        url = in.readString();
        picUrl = in.readString();
        type = in.readInt();
    }

    public static final Creator<SongInfo> CREATOR = new Creator<SongInfo>() {
        @Override
        public SongInfo createFromParcel(Parcel in) {
            return new SongInfo(in);
        }

        @Override
        public SongInfo[] newArray(int size) {
            return new SongInfo[size];
        }
    };

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public int getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getAlbumId() {
        return albumId;
    }

    public void setAlbumId(long albumId) {
        this.albumId = albumId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "SongInfo{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", album='" + album + '\'' +
                ", albumId=" + albumId +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", url='" + url + '\'' +
                ", picUrl='" + picUrl + '\'' +
                ", type=" + type +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(album);
        dest.writeLong(albumId);
        dest.writeString(artist);
        dest.writeLong(duration);
        dest.writeLong(size);
        dest.writeString(url);
        dest.writeString(picUrl);
        dest.writeInt(type);
    }
}
