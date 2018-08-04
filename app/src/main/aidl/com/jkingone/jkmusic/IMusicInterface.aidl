// IMusicInterface.aidl
package com.jkingone.jkmusic;

import com.jkingone.jkmusic.entity.SongInfo;

interface IMusicInterface {

    void play();

    void playIndex(int index);

    void start();

    void pause();

    void stop();

    void release();

    void reset();

    long getDuration();

    long getCurrentPosition();

    void seekToPosition(long positionMs);

    void seekToIndex(int index, long positionMs);

    void next();

    void previous();

    int getCurrentIndex();

    int getNextIndex();

    int getPreviousIndex();

    void setPlayMode(int playMode);

    int getPlayMode();

    boolean isPlaying();

    long getBufferedPosition();

    int getBufferedPercentage();

    //==============================================================================================
    // MediaSource
    //==============================================================================================

    void prepareMediaSources(in List<SongInfo> songInfos);

    void addMediaSource(in SongInfo songInfo);
    void addMediaSources(in List<SongInfo> songInfos);

    void removeMediaSource(int index);

    void clearMediaSources();
}
