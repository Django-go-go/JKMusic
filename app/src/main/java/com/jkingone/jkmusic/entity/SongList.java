package com.jkingone.jkmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/9/10.
 */

public class SongList implements Parcelable {
    @SerializedName("listId")
    private String listId;
    private String title;
    @SerializedName("pic_300")
    private String pic300;
    private String pic;
    private String tag;
    private String desc;

    public SongList(String listId, String title, String pic300, String pic, String tag, String desc) {
        this.listId = listId;
        this.title = title;
        this.pic300 = pic300;
        this.pic = pic;
        this.tag = tag;
        this.desc = desc;
    }

    protected SongList(Parcel in) {
        listId = in.readString();
        title = in.readString();
        pic300 = in.readString();
        pic = in.readString();
        tag = in.readString();
        desc = in.readString();
    }

    public static final Creator<SongList> CREATOR = new Creator<SongList>() {
        @Override
        public SongList createFromParcel(Parcel in) {
            return new SongList(in);
        }

        @Override
        public SongList[] newArray(int size) {
            return new SongList[size];
        }
    };

    public String getListId() {
        return listId;
    }

    public void setListId(String listId) {
        this.listId = listId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic300() {
        return pic300;
    }

    public void setPic300(String pic300) {
        this.pic300 = pic300;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public String toString() {
        return "SongList{" +
                "listId='" + listId + '\'' +
                ", title='" + title + '\'' +
                ", pic300='" + pic300 + '\'' +
                ", pic='" + pic + '\'' +
                ", tag='" + tag + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(listId);
        dest.writeString(title);
        dest.writeString(pic300);
        dest.writeString(pic);
        dest.writeString(tag);
        dest.writeString(desc);
    }
}
