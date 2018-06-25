// IMusicInterface.aidl
package com.jkingone.jkmusic;

import com.jkingone.jkmusic.data.entity.SongInfo;

interface IMusicInterface {
    void add(int index, in List<SongInfo> songInfos);
    void play();
    void pause();
    void next();
    void previous();
}
