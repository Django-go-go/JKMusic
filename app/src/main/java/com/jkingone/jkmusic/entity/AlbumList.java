package com.jkingone.jkmusic.entity;


import com.google.gson.annotations.SerializedName;

public class AlbumList {

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
    @SerializedName("artist_id")
    private String artistId;
    private String author;
    @SerializedName("publishtime")
    private String publishTime;
    private String info;


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
}

