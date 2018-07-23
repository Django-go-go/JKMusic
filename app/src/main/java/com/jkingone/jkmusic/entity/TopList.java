package com.jkingone.jkmusic.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Administrator on 2017/9/8.
 */

public class TopList implements Parcelable {

    private String name;
    private String type;
    private int count;
    private String comment;
    @SerializedName("pic_s192")
    private String picS192;
    @SerializedName("pic_s444")
    private String picS444;
    @SerializedName("pic_s260")
    private String picS260;
    @SerializedName("pic_s210")
    private String picS210;
    private List<Content> content;

    protected TopList(Parcel in) {
        name = in.readString();
        type = in.readString();
        count = in.readInt();
        comment = in.readString();
        picS192 = in.readString();
        picS444 = in.readString();
        picS260 = in.readString();
        picS210 = in.readString();
    }

    public static final Creator<TopList> CREATOR = new Creator<TopList>() {
        @Override
        public TopList createFromParcel(Parcel in) {
            return new TopList(in);
        }

        @Override
        public TopList[] newArray(int size) {
            return new TopList[size];
        }
    };

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }


    public void setType(String type) {
        this.type = type;
    }
    public String getType() {
        return type;
    }


    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }


    public void setComment(String comment) {
        this.comment = comment;
    }
    public String getComment() {
        return comment;
    }


    public void setPicS192(String picS192) {
        this.picS192 = picS192;
    }
    public String getPicS192() {
        return picS192;
    }


    public void setPicS444(String picS444) {
        this.picS444 = picS444;
    }
    public String getPicS444() {
        return picS444;
    }


    public void setPicS260(String picS260) {
        this.picS260 = picS260;
    }
    public String getPicS260() {
        return picS260;
    }


    public void setPicS210(String picS210) {
        this.picS210 = picS210;
    }
    public String getPicS210() {
        return picS210;
    }


    public void setContent(List<Content> content) {
        this.content = content;
    }
    public List<Content> getContent() {
        return content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(type);
        dest.writeInt(count);
        dest.writeString(comment);
        dest.writeString(picS192);
        dest.writeString(picS444);
        dest.writeString(picS260);
        dest.writeString(picS210);
    }

    public static class Content implements Parcelable {

        private String title;
        private String author;

        protected Content(Parcel in) {
            title = in.readString();
            author = in.readString();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeString(author);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<Content> CREATOR = new Creator<Content>() {
            @Override
            public Content createFromParcel(Parcel in) {
                return new Content(in);
            }

            @Override
            public Content[] newArray(int size) {
                return new Content[size];
            }
        };

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

    }

}




