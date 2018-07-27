package com.jkingone.jkmusic.entity;


import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class AlbumList implements Parcelable {

    @SerializedName("album_id")
    private String albumId;
    private String title;
    @SerializedName("publishcompany")
    private String publishCompany;
    private String country;
    @SerializedName("songs_total")
    private String songsTotal;
    @SerializedName("pic_small")
    private String picSmall;
    @SerializedName("pic_big")
    private String picBig;
    @SerializedName("pic_radio")
    private String picRadio;
    @SerializedName("artist_id")
    private String artistId;
    private String author;
    @SerializedName("publishtime")
    private String publishTime;
    private String info;


    protected AlbumList(Parcel in) {
        albumId = in.readString();
        title = in.readString();
        publishCompany = in.readString();
        country = in.readString();
        songsTotal = in.readString();
        picSmall = in.readString();
        picBig = in.readString();
        picRadio = in.readString();
        artistId = in.readString();
        author = in.readString();
        publishTime = in.readString();
        info = in.readString();
    }

    public static final Creator<AlbumList> CREATOR = new Creator<AlbumList>() {
        @Override
        public AlbumList createFromParcel(Parcel in) {
            return new AlbumList(in);
        }

        @Override
        public AlbumList[] newArray(int size) {
            return new AlbumList[size];
        }
    };

    public String getPicRadio() {
        return picRadio;
    }

    public void setPicRadio(String picRadio) {
        this.picRadio = picRadio;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getAlbumId() {
        return albumId;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public void setPublishCompany(String publishCompany) {
        this.publishCompany = publishCompany;
    }

    public String getPublishCompany() {
        return publishCompany;
    }


    public void setCountry(String country) {
        this.country = country;
    }

    public String getCountry() {
        return country;
    }


    public void setSongsTotal(String songsTotal) {
        this.songsTotal = songsTotal;
    }

    public String getSongsTotal() {
        return songsTotal;
    }


    public void setPicSmall(String picSmall) {
        this.picSmall = picSmall;
    }

    public String getPicSmall() {
        return picSmall;
    }


    public void setPicBig(String picBig) {
        this.picBig = picBig;
    }

    public String getPicBig() {
        return picBig;
    }


    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getArtistId() {
        return artistId;
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthor() {
        return author;
    }


    public void setPublishTime(String publishTime) {
        this.publishTime = publishTime;
    }

    public String getPublishTime() {
        return publishTime;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(albumId);
        dest.writeString(title);
        dest.writeString(publishCompany);
        dest.writeString(country);
        dest.writeString(songsTotal);
        dest.writeString(picSmall);
        dest.writeString(picBig);
        dest.writeString(picRadio);
        dest.writeString(artistId);
        dest.writeString(author);
        dest.writeString(publishTime);
        dest.writeString(info);
    }

    @Override
    public String toString() {
        return "AlbumList{" +
                "albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", publishCompany='" + publishCompany + '\'' +
                ", country='" + country + '\'' +
                ", songsTotal='" + songsTotal + '\'' +
                ", picSmall='" + picSmall + '\'' +
                ", picBig='" + picBig + '\'' +
                ", picRadio='" + picRadio + '\'' +
                ", artistId='" + artistId + '\'' +
                ", author='" + author + '\'' +
                ", publishTime='" + publishTime + '\'' +
                ", info='" + info + '\'' +
                '}';
    }
}

