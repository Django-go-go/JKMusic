package com.jkingone.jkmusic;

/**
 * Created by Administrator on 2017/9/8.
 */

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class UrlString {

    public static final String FORMAT = "json";
    public static final String BASE = "http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=" + FORMAT;



    /**
     * 音乐场景
     *
     * @author Sanron
     */
    public static class Scene {

        /**
         * 推荐音乐场景(需要cuid，暂时关闭)
         * @return
         */
//		public static String sugestionScene(){
//			StringBuffer sb = new StringBuffer(BASE);
//			sb.append("&method=").append("baidu.ting.scene.getSugScene");
//			return sb.toString();
//		}

        /**
         * 固定场景
         *
         * @return
         */
        public static String constantScene() {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.scene.getConstantScene");
            return sb.toString();
        }

        /**
         * 所有场景类别
         *
         * @return
         */
        public static String sceneCategories() {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.scene.getCategoryList");
            return sb.toString();
        }

        /**
         * 场景类别下的所有场景
         *
         * @param categoreid 类别id
         * @return
         */
        public static String categoryScenes(String categoreid) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.scene.getCategoryScene")
                    .append("&category_id=").append(categoreid);
            return sb.toString();
        }
    }

    /**
     * 音乐标签
     *
     * @author Sanron
     */
    public static class Tag {
        /**
         * 所有音乐标签
         *
         * @return
         */
        public static String allSongTags() {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.tag.getAllTag");
            return sb.toString();
        }

        /**
         * 热门音乐标签
         *
         * @param num 数量
         * @return
         */
        public static String hotSongTags(int num) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.tag.getHotTag")
                    .append("&nums=").append(num);
            return sb.toString();
        }

        /**
         * 标签为tagname的歌曲
         *
         * @param tagname 标签名
         * @param limit   数量
         * @return
         */
        public static String tagSongs(String tagname, int limit) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.tag.songlist")
                    .append("&tagname=").append(encode(tagname))
                    .append("&limit=").append(limit);
            return sb.toString();
        }
    }

    public static class Song {

        /**
         * 歌曲基本信息
         *
         * @param songid 歌曲id
         * @return
         */

        //http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=json&method=baidu.ting.song.baseInfos&song_id=
        public static String songBaseInfo(String songid) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.song.baseInfos")
                    .append("&song_id=").append(songid);
            return sb.toString();
        }

        /**
         * 编辑推荐歌曲
         *
         * @param num 数量
         * @return
         */
        //http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=json&method=baidu.ting.song.getEditorRecommend&num=3
        public static String recommendSong(int num) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.song.getEditorRecommend")
                    .append("&num=").append(num);
            return sb.toString();
        }

        /**
         * 歌曲信息和下载地址
         *
         * @param songid
         * @return
         */
        public static String songInfo(String songid) {
            StringBuffer sb = new StringBuffer("http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=6&format=json");
            String str = "songid=" + songid + "&ts=" + System.currentTimeMillis();
            String e = AESTools.encrpty(str);
            sb.append("&method=").append("baidu.ting.song.getInfos")
                    .append("&").append(str)
                    .append("&e=").append(e);
            return sb.toString();
        }

        /**
         * 歌曲伴奏信息
         *
         * @param songid
         * @return
         */
//        public static String accompanyInfo(String songid) {
//            StringBuffer sb = new StringBuffer(BASE);
//            String str = "song_id=" + songid + "&ts=" + System.currentTimeMillis();
//            String e = AESTools.encrpty(str);
//            sb.append("&method=").append("baidu.ting.learn.down")
//                    .append("&").append(str)
//                    .append("&e=").append(e);
//            return sb.toString();
//        }

        /**
         * 相似歌曲
         *
         * @param songid
         * @return
         */
        public static String recommendSongList(String songid, int num) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.song.getRecommandSongList")
                    .append("&song_id=").append(songid)
                    .append("&num=").append(num);
            return sb.toString();
        }
    }



    /**
     * 电台
     *
     * @author Sanron
     */
    public static class Radio {

        /**
         * 录制电台
         *
         * @param pageNo   页数
         * @param pageSize 每页数量，也是返回数量
         * @return
         */
        //http://tingapi.ting.baidu.com/v1/restserver/ting?method=baidu.ting.radio.getRecommendRadioList
        public static String recChannel(int pageNo, int pageSize) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.radio.getRecChannel")
                    .append("&page_no=").append(pageNo)
                    .append("&page_size=").append(pageSize);
            return sb.toString();
        }

        /**
         * 推荐电台（注意返回的都是乐播节目)
         *
         * @param num
         * @return
         */
        public static String recommendRadioList(int num) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.radio.getRecommendRadioList")
                    .append("&num=").append(num);
            return sb.toString();
        }

        /**
         * 频道歌曲
         *
         * @param channelname 频道名,注意返回的json数据频道有num+1个，但是最后一个是空的
         * @return
         */
        public static String channelSong(String channelname, int num) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.radio.getChannelSong")
                    .append("&channelname=").append(encode(channelname))
                    .append("&pn=0")
                    .append("&rn=").append(num);
            return sb.toString();
        }
    }

    /**
     * 乐播节目
     * 节目相当于一个专辑
     * 每一期相当于专辑里的每首歌
     *
     * @author Sanron
     */
    public static class Lebo {

        /**
         * 频道
         *
         * @param pageNo   页码(暂时无用)
         * @param pageSize 每页数量，也是返回数量(暂时无用)
         * @return
         */
        public static String channelTag(int pageNo, int pageSize) {

            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.lebo.getChannelTag")
                    .append("&page_no=").append(pageNo)
                    .append("&page_size=0").append(pageSize);
            return sb.toString();
        }

        /**
         * 返回频道下的不同节目的几期
         * 包含几个节目，每个节目有一期或多期
         * 比如返回 	节目1第1期，节目1第2期，节目2第1期，节目3第6期
         *
         * @param tagId 频道id
         * @param num   数量
         * @return
         */
        public static String channelSongList(String tagId, int num) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.lebo.channelSongList")
                    .append("&tag_id=").append(tagId)
                    .append("&num=").append(num);
            return sb.toString();
        }

        /**
         * 节目信息
         *
         * @param albumid        节目id
         * @param lastestSongNum 返回最近几期
         * @return
         */
        public static String albumInfo(String albumid, int lastestSongNum) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.lebo.albumInfo")
                    .append("&album_id=").append(albumid)
                    .append("&num=").append(lastestSongNum);
            return sb.toString();
        }
    }

    /**
     * 搜索
     *
     * @author Sanron
     */
    public static class Search {

        /**
         * 热门关键字
         *
         * @return
         */
        //http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=json&method=baidu.ting.search.hot
        public static String hotWord() {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.search.hot");
            return sb.toString();
        }

        /**
         * 搜索建议
         *
         * @param
         * @return
         */
        //MP3网址：http://link.hhtjim.com/baidu/14945107.mp3


        //http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=json&method=baidu.ting.search.catalogSug&query=泡沫
        public static String searchSugestion(String query) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.search.catalogSug")
                    .append("&query=").append(encode(query));
            return sb.toString();
        }

        /**
         * 搜歌词
         *
         * @param songname 歌名
         * @param artist   艺术家
         * @return
         */
//        public static String searchLrcPic(String songname, String artist) {
//            StringBuffer sb = new StringBuffer(BASE);
//            String ts = Long.toString(System.currentTimeMillis());
//            String query = encode(songname) + "$$" + encode(artist);
//            String e = AESTools.encrpty("query=" + songname + "$$" + artist + "&ts=" + ts);
//            sb.append("&method=").append("baidu.ting.search.lrcpic")
//                    .append("&query=").append(query)
//                    .append("&ts=").append(ts)
//                    .append("&type=2")
//                    .append("&e=").append(e);
//            return sb.toString();
//        }

        /**
         * 合并搜索结果，用于搜索建议中的歌曲
         *
         * @param query
         * @return
         */
        //http://tingapi.ting.baidu.com/v1/restserver/ting?from=android&version=5.6.5.6&format=json&method=baidu.ting.search.merge&query=泡沫&page_no=1&page_size=12&type=-1&data_source=0
        public static String searchMerge(String query, int pageNo, int pageSize) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.search.merge")
                    .append("&query=").append(encode(query))
                    .append("&page_no=").append(pageNo)
                    .append("&page_size=").append(pageSize)
                    .append("&type=-1&data_source=0");
            return sb.toString();
        }

        /**
         * 搜索伴奏
         *
         * @param query    关键词
         * @param pageNo   页码
         * @param pageSize 每页数量，也是返回数量
         * @return
         */
        public static String searchAccompany(String query, int pageNo, int pageSize) {
            StringBuffer sb = new StringBuffer(BASE);
            sb.append("&method=").append("baidu.ting.learn.search")
                    .append("&query=").append(encode(query))
                    .append("&page_no=").append(pageNo)
                    .append("&page_size=").append(pageSize);
            return sb.toString();
        }
    }

    public static String encode(String str) {
        if (str == null) return "";

        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str;
    }

}
