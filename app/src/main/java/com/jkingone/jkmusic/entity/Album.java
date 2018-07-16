package com.jkingone.jkmusic.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Album {

    private AlbumInfo albumInfo;
    private List<Songlist> songlist;


    public void setAlbumInfo(AlbumInfo albumInfo) {
        this.albumInfo = albumInfo;
    }
    public AlbumInfo getAlbumInfo() {
        return albumInfo;
    }


    public void setSonglist(List<Songlist> songlist) {
        this.songlist = songlist;
    }
    public List<Songlist> getSonglist() {
        return songlist;
    }


    public class AlbumInfo {

        @SerializedName("album_id")
        private String albumId;
        private String author;
        private String title;
        private String publishcompany;
        private String prodcompany;
        private String country;
        private String language;
        @SerializedName("songs_total")
        private String songsTotal;
        private String info;
        private String publishtime;
        @SerializedName("pic_small")
        private String picSmall;
        @SerializedName("pic_big")
        private String picBig;
        @SerializedName("favorites_num")
        private int favoritesNum;
        @SerializedName("recommend_num")
        private int recommendNum;
        @SerializedName("collect_num")
        private int collectNum;
        @SerializedName("share_num")
        private int shareNum;
        @SerializedName("comment_num")
        private int commentNum;
        @SerializedName("artist_id")
        private String artistId;
        @SerializedName("pic_s500")
        private String picS500;
        @SerializedName("pic_s1000")
        private String picS1000;
        @SerializedName("listen_num")
        private String listenNum;


        public void setAlbumId(String albumId) {
            this.albumId = albumId;
        }
        public String getAlbumId() {
            return albumId;
        }


        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
        }


        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }


        public void setPublishcompany(String publishcompany) {
            this.publishcompany = publishcompany;
        }
        public String getPublishcompany() {
            return publishcompany;
        }


        public void setProdcompany(String prodcompany) {
            this.prodcompany = prodcompany;
        }
        public String getProdcompany() {
            return prodcompany;
        }


        public void setCountry(String country) {
            this.country = country;
        }
        public String getCountry() {
            return country;
        }


        public void setLanguage(String language) {
            this.language = language;
        }
        public String getLanguage() {
            return language;
        }


        public void setSongsTotal(String songsTotal) {
            this.songsTotal = songsTotal;
        }
        public String getSongsTotal() {
            return songsTotal;
        }


        public void setInfo(String info) {
            this.info = info;
        }
        public String getInfo() {
            return info;
        }


        public void setPublishtime(String publishtime) {
            this.publishtime = publishtime;
        }
        public String getPublishtime() {
            return publishtime;
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

        public void setFavoritesNum(int favoritesNum) {
            this.favoritesNum = favoritesNum;
        }
        public int getFavoritesNum() {
            return favoritesNum;
        }


        public void setRecommendNum(int recommendNum) {
            this.recommendNum = recommendNum;
        }
        public int getRecommendNum() {
            return recommendNum;
        }


        public void setCollectNum(int collectNum) {
            this.collectNum = collectNum;
        }
        public int getCollectNum() {
            return collectNum;
        }


        public void setShareNum(int shareNum) {
            this.shareNum = shareNum;
        }
        public int getShareNum() {
            return shareNum;
        }


        public void setCommentNum(int commentNum) {
            this.commentNum = commentNum;
        }
        public int getCommentNum() {
            return commentNum;
        }


        public void setArtistId(String artistId) {
            this.artistId = artistId;
        }
        public String getArtistId() {
            return artistId;
        }

        public void setPicS500(String picS500) {
            this.picS500 = picS500;
        }
        public String getPicS500() {
            return picS500;
        }


        public void setPicS1000(String picS1000) {
            this.picS1000 = picS1000;
        }
        public String getPicS1000() {
            return picS1000;
        }

        public void setListenNum(String listenNum) {
            this.listenNum = listenNum;
        }
        public String getListenNum() {
            return listenNum;
        }

    }


    public class Songlist {

        @SerializedName("artist_id")
        private String artistId;
        private String language;
        private String publishtime;
        @SerializedName("pic_big")
        private String picBig;
        @SerializedName("pic_small")
        private String picSmall;
        private String country;
        private String lrclink;
        private String info;
        @SerializedName("song_id")
        private String songId;
        private String title;
        private String author;
        @SerializedName("pic_s500")
        private String picS500;
        @SerializedName("pic_premium")
        private String picPremium;
        @SerializedName("pic_huge")
        private String picHuge;


        public void setArtistId(String artistId) {
            this.artistId = artistId;
        }
        public String getArtistId() {
            return artistId;
        }

        public void setLanguage(String language) {
            this.language = language;
        }
        public String getLanguage() {
            return language;
        }


        public void setPublishtime(String publishtime) {
            this.publishtime = publishtime;
        }
        public String getPublishtime() {
            return publishtime;
        }

        public void setPicBig(String picBig) {
            this.picBig = picBig;
        }
        public String getPicBig() {
            return picBig;
        }


        public void setPicSmall(String picSmall) {
            this.picSmall = picSmall;
        }
        public String getPicSmall() {
            return picSmall;
        }

        public void setCountry(String country) {
            this.country = country;
        }
        public String getCountry() {
            return country;
        }

        public void setLrclink(String lrclink) {
            this.lrclink = lrclink;
        }
        public String getLrclink() {
            return lrclink;
        }

        public void setInfo(String info) {
            this.info = info;
        }
        public String getInfo() {
            return info;
        }

        public void setSongId(String songId) {
            this.songId = songId;
        }
        public String getSongId() {
            return songId;
        }


        public void setTitle(String title) {
            this.title = title;
        }
        public String getTitle() {
            return title;
        }

        public void setAuthor(String author) {
            this.author = author;
        }
        public String getAuthor() {
            return author;
        }

        public void setPicS500(String picS500) {
            this.picS500 = picS500;
        }
        public String getPicS500() {
            return picS500;
        }


        public void setPicPremium(String picPremium) {
            this.picPremium = picPremium;
        }
        public String getPicPremium() {
            return picPremium;
        }


        public void setPicHuge(String picHuge) {
            this.picHuge = picHuge;
        }
        public String getPicHuge() {
            return picHuge;
        }

    }
}
