package com.jkingone.jkmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/*
{
"avatar_middle": "http:\/\/qukufile2.qianqian.com\/data2\/pic\/140d09665d1c204efe00973c3e16282c\/579342600\/579342600.jpg@s_0,w_120",
"area": "0",
"avatar_mini": "http:\/\/qukufile2.qianqian.com\/data2\/pic\/140d09665d1c204efe00973c3e16282c\/579342600\/579342600.jpg@s_0,w_20",
"firstchar": "X",
"ting_uid": "2517",
"name": "薛之谦",
"songs_total": "90",
"islocate": 0,
"gender": "0",
"country": "中国",
"piao_id": "0",
"is_yyr": "0",
"avatar_small": "http:\/\/qukufile2.qianqian.com\/data2\/pic\/140d09665d1c204efe00973c3e16282c\/579342600\/579342600.jpg@s_0,w_48",
"avatar_big": "http:\/\/qukufile2.qianqian.com\/data2\/pic\/140d09665d1c204efe00973c3e16282c\/579342600\/579342600.jpg@s_0,w_240",
"albums_total": "18",
"artist_id": "88"
}
 */


public class ArtistList implements Parcelable {

    @SerializedName("avatar_middle")
    public String avatarMiddle;
    @SerializedName("avatar_mini")
    public String avatarMini;
    @SerializedName("avatar_big")
    public String avatarBig;
    @SerializedName("avatar_small")
    public String avatarSmall;
    @SerializedName("artist_id")
    public String artistId;
    public String name;
    @SerializedName("firstchar")
    public String firstChar;
    @SerializedName("ting_uid")
    public String tingUid;
    public String country;
    @SerializedName("albums_total")
    public int albumsTotal;
    @SerializedName("songs_total")
    public int songsTotal;

    protected ArtistList(Parcel in) {
        avatarMiddle = in.readString();
        avatarMini = in.readString();
        avatarBig = in.readString();
        avatarSmall = in.readString();
        artistId = in.readString();
        name = in.readString();
        firstChar = in.readString();
        tingUid = in.readString();
        country = in.readString();
        albumsTotal = in.readInt();
        songsTotal = in.readInt();
    }

    public static final Creator<ArtistList> CREATOR = new Creator<ArtistList>() {
        @Override
        public ArtistList createFromParcel(Parcel in) {
            return new ArtistList(in);
        }

        @Override
        public ArtistList[] newArray(int size) {
            return new ArtistList[size];
        }
    };

    @Override
    public String toString() {
        return "ArtistList{" +
                "avatarMiddle='" + avatarMiddle + '\'' +
                ", avatarMini='" + avatarMini + '\'' +
                ", avatarBig='" + avatarBig + '\'' +
                ", avatarSmall='" + avatarSmall + '\'' +
                ", artistId='" + artistId + '\'' +
                ", name='" + name + '\'' +
                ", firstChar='" + firstChar + '\'' +
                ", tingUid='" + tingUid + '\'' +
                ", country='" + country + '\'' +
                ", albumsTotal=" + albumsTotal +
                ", songsTotal=" + songsTotal +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(avatarMiddle);
        dest.writeString(avatarMini);
        dest.writeString(avatarBig);
        dest.writeString(avatarSmall);
        dest.writeString(artistId);
        dest.writeString(name);
        dest.writeString(firstChar);
        dest.writeString(tingUid);
        dest.writeString(country);
        dest.writeInt(albumsTotal);
        dest.writeInt(songsTotal);
    }
}