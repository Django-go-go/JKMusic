package com.jkingone.jkmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/11.
 */

public class Song implements Parcelable {
    @SerializedName("song_id")
    private String songId;
    private String title;
    private String author;
    @SerializedName("pic_big")
    private String picBig;
    private String url;

    public Song(String songId, String title, String author, String picBig, String url) {
        this.songId = songId;
        this.title = title;
        this.author = author;
        this.picBig = picBig;
        this.url = url;
    }

    protected Song(Parcel in) {
        songId = in.readString();
        title = in.readString();
        author = in.readString();
        picBig = in.readString();
        url = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPicBig() {
        return picBig;
    }

    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId='" + songId + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", picBig='" + picBig + '\'' +
                ", url='" + url + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(songId);
        dest.writeString(title);
        dest.writeString(author);
        dest.writeString(picBig);
        dest.writeString(url);
    }
}
