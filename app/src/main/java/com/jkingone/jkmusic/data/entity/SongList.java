package com.jkingone.jkmusic.data.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Administrator on 2017/9/10.
 */

public class SongList implements Parcelable {
    private String listid;
    private String title;
    private String pic_300;
    private String pic;
    private String tag;
    private String desc;

    public SongList(String listid, String title, String pic_300, String pic, String tag, String desc) {
        this.listid = listid;
        this.title = title;
        this.pic_300 = pic_300;
        this.pic = pic;
        this.tag = tag;
        this.desc = desc;
    }

    protected SongList(Parcel in) {
        listid = in.readString();
        title = in.readString();
        pic_300 = in.readString();
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

    public String getListid() {
        return listid;
    }

    public void setListid(String listid) {
        this.listid = listid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPic_300() {
        return pic_300;
    }

    public void setPic_300(String pic_300) {
        this.pic_300 = pic_300;
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
                "listid='" + listid + '\'' +
                ", title='" + title + '\'' +
                ", pic_300='" + pic_300 + '\'' +
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
        dest.writeString(listid);
        dest.writeString(title);
        dest.writeString(pic_300);
        dest.writeString(pic);
        dest.writeString(tag);
        dest.writeString(desc);
    }
}
