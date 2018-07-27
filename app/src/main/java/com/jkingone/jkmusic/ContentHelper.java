/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * This file is part of FileExplorer.
 *
 * FileExplorer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FileExplorer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SwiFTP.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.jkingone.jkmusic;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video;
import android.util.Log;

import com.jkingone.jkmusic.entity.SongInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class ContentHelper {

    public static final String TAG = "ContentHelper";

    public static HashSet<String> sDocMimeTypesSet = new HashSet<String>() {
        {
            add("text/plain");
            add("text/plain");
            add("application/pdf");
            add("application/msword");
            add("application/vnd.ms-excel");
            add("application/vnd.ms-excel");
        }
    };

    public enum FileCategory {
        All, Music, Video, Picture, Theme, Doc, Zip, Apk, Other, Favorite
    }


    private Context mContext;

    public ContentHelper(Context context) {
        mContext = context;
    }

    private String buildDocSelection() {
        StringBuilder selection = new StringBuilder();
        Iterator<String> iter = sDocMimeTypesSet.iterator();
        while(iter.hasNext()) {
            selection.append("(" + FileColumns.MIME_TYPE + "=='" + iter.next() + "') OR ");
        }
        return  selection.substring(0, selection.lastIndexOf(")") + 1);
    }

    private String buildSelectionByCategory(FileCategory cat) {
        String selection;
        switch (cat) {
            case Theme:
                selection = FileColumns.DATA + " LIKE '%.mtz'";
                break;
            case Doc:
                selection = buildDocSelection();
                break;
            case Zip:
                selection = "(" + FileColumns.MIME_TYPE + " == '" + "application/zip" + "')";
                break;
            case Apk:
                selection = FileColumns.DATA + " LIKE '%.apk'";
                break;
            default:
                selection = null;
        }
        return selection;
    }

    private Uri getContentUriByCategory(FileCategory cat) {
        Uri uri;
        String volumeName = "external";
        switch(cat) {
            case Theme:
            case Doc:
            case Zip:
            case Apk:
                uri = Files.getContentUri(volumeName);
                break;
            case Music:
                uri = Audio.Media.getContentUri(volumeName);
                break;
            case Video:
                uri = Video.Media.getContentUri(volumeName);
                break;
            case Picture:
                uri = Images.Media.getContentUri(volumeName);
                break;
           default:
               uri = null;
        }
        return uri;
    }

    private String buildSortOrder(SortMethod sort) {
        String sortOrder = null;
        switch (sort) {
            case name:
                sortOrder = FileColumns.TITLE + " asc";
                break;
            case size:
                sortOrder = FileColumns.SIZE + " asc";
                break;
            case date:
                sortOrder = FileColumns.DATE_MODIFIED + " desc";
                break;
            case type:
                sortOrder = FileColumns.MIME_TYPE + " asc, " + FileColumns.TITLE + " asc";
                break;
        }
        return sortOrder;
    }

    private Cursor query(FileCategory fc, SortMethod sort) {
        Uri uri = getContentUriByCategory(fc);
        String selection = buildSelectionByCategory(fc);
        String sortOrder = buildSortOrder(sort);
        if (uri == null) {
            return null;
        }

        return mContext.getContentResolver().query(uri, null, selection, null, sortOrder);
    }

    public List<SongInfo> getMusic() {
        ArrayList<SongInfo> mp3Infos = new ArrayList<>();
        Cursor cursor = null;
        try{
            cursor = mContext.getContentResolver().query(Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

            for (int i = 0; i < cursor.getCount(); i++) {
                SongInfo mp3Info = new SongInfo();
                if(cursor.moveToNext()){
                    long id = cursor.getLong(cursor
                            .getColumnIndex(Audio.Media._ID));   //音乐id
                    long album_id = cursor.getLong(cursor
                            .getColumnIndex(Audio.Media.ALBUM_ID));         //专辑id
                    String title = cursor.getString((cursor
                            .getColumnIndex(Audio.Media.TITLE)));//音乐标题
                    String artist = cursor.getString(cursor
                            .getColumnIndex(Audio.Media.ARTIST));//艺术家
                    long duration = cursor.getLong(cursor
                            .getColumnIndex(Audio.Media.DURATION));//时长
                    long size = cursor.getLong(cursor
                            .getColumnIndex(Audio.Media.SIZE));  //文件大小
                    String url = cursor.getString(cursor
                            .getColumnIndex(Audio.Media.DATA)); //文件路径
                    int isMusic = cursor.getInt(cursor
                            .getColumnIndex(Audio.Media.IS_MUSIC));//是否为音乐
                    String album = cursor.getString(cursor
                            .getColumnIndex(Audio.Media.ALBUM));
                    if (isMusic != 0 && duration > 1000 * 5) {     //只把音乐添加到集合当中
                        mp3Info.setId(String.valueOf(id));
                        mp3Info.setTitle(title);
                        mp3Info.setArtist(artist);
                        mp3Info.setDuration(duration);
                        mp3Info.setSize(size);
                        mp3Info.setUrl(url);
                        mp3Info.setAlbum(album);
                        mp3Info.setAlbumId(album_id);
                        mp3Info.setPicUrl("content://media/external/audio/albumart" + "/" + mp3Info.getAlbumId());
                        mp3Infos.add(mp3Info);
                    }
                }
            }
        }catch (Exception e){

        }finally {
            if (cursor != null)
                cursor.close();
        }
        return mp3Infos;
    }

    public String getUrl(long id){
        String path = null;
        String[] projection = {Audio.Albums.ALBUM_ID, Audio.Albums.ALBUM_ART};
        String selection = "(" + Audio.AlbumColumns.ALBUM_ID + " == " + id + ")";
        Log.i(TAG, "getUrl: " + Audio.Albums.EXTERNAL_CONTENT_URI);
        Cursor cursor = mContext.getContentResolver().
                query(Audio.Albums.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (cursor != null){
            if (cursor.moveToNext()){
                int columnIndex = cursor.getColumnIndex(projection[1]);
                path = cursor.getString(columnIndex);
            }
            cursor.close();
        }
        return path;
    }

    public enum SortMethod {
        name, size, date, type
    }

    private SortMethod mSort = SortMethod.name;



}
