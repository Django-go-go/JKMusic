// IMusicInterface.aidl
package com.jkingone.jkmusic;

interface IMusicInterface {

    void prepare(String path);

    void start();

    void pause();

    void stop();

    void release();

    void reset();

    long getDuration();

    long getCurrentPosition();

    void seekTo(long positionMs);

    boolean isPlaying();

    long getBufferedPosition();

    int getBufferedPercentage();

}
