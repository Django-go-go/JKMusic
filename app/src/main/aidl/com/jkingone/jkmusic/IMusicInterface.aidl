// IMusicInterface.aidl
package com.jkingone.jkmusic;

import com.jkingone.jkmusic.data.entity.SongInfo;

interface IMusicInterface {
    void prepareMediaSources(in List<SongInfo> songInfos);
    void addMediaSource(in SongInfo songInfo);
    List<SongInfo> getMediaSources();
    void removeMediaSource(int index);
    void clearMediaSources();
    void addMediaSources(in List<SongInfo> songInfos);

    void play();
    void playIndex(int index);
    void pause();
    void next();
    void previous();

    void initializePlayer();

    void releasePlayer();

    boolean isPlaying();

      /**
       * Returns the current state of the player.
       *
       * @return One of the {@code STATE} constants defined in this interface.
       */
      int getPlaybackState();

      /**
       * Sets the {@link RepeatMode} to be used for playback.
       *
       * @param repeatMode A repeat mode.
       */
      void setRepeatMode(int repeatMode);

      /**
       * Returns the current {@link RepeatMode} used for playback.
       *
       * @return The current repeat mode.
       */
      int getRepeatMode();

      /**
       * Whether the player is currently loading the source.
       *
       * @return Whether the player is currently loading the source.
       */
      boolean isLoading();

      /**
       * Seeks to a position specified in milliseconds in the current window.
       *
       * @param positionMs The seek position in the current window, or {@link C#TIME_UNSET} to seek to
       *     the window's default position.
       */
      void seekTo(long positionMs);

      /**
       * Seeks to a position specified in milliseconds in the specified window.
       *
       * @param windowIndex The index of the window.
       * @param positionMs The seek position in the specified window, or {@link C#TIME_UNSET} to seek to
       *     the window's default position.
       * @throws IllegalSeekPositionException If the player has a non-empty timeline and the provided
       *     {@code windowIndex} is not within the bounds of the current timeline.
       */
      void seekToIndex(int windowIndex, long positionMs);

      /**
       * Stops playback without resetting the player. Use {@code setPlayWhenReady(false)} rather than
       * this method if the intention is to pause playback.
       *
       * <p>Calling this method will cause the playback state to transition to {@link #STATE_IDLE}. The
       * player instance can still be used, and {@link #release()} must still be called on the player if
       * it's no longer required.
       *
       * <p>Calling this method does not reset the playback position.
       */
      void stop();

      /**
       * Stops playback and optionally resets the player. Use {@code setPlayWhenReady(false)} rather
       * than this method if the intention is to pause playback.
       *
       * <p>Calling this method will cause the playback state to transition to {@link #STATE_IDLE}. The
       * player instance can still be used, and {@link #release()} must still be called on the player if
       * it's no longer required.
       *
       * @param reset Whether the player should be reset.
       */
      void stopCanReset(boolean reset);

      /**
       * Returns the index of the window currently being played.
       */
      int getCurrentWindowIndex();

      /**
       * Returns the index of the next timeline window to be played, which may depend on the current
       * repeat mode and whether shuffle mode is enabled. Returns {@link C#INDEX_UNSET} if the window
       * currently being played is the last window.
       */
      int getNextWindowIndex();

      /**
       * Returns the index of the previous timeline window to be played, which may depend on the current
       * repeat mode and whether shuffle mode is enabled. Returns {@link C#INDEX_UNSET} if the window
       * currently being played is the first window.
       */
      int getPreviousWindowIndex();

      /**
       * Returns the duration of the current window in milliseconds, or {@link C#TIME_UNSET} if the
       * duration is not known.
       */
      long getDuration();

      /**
       * Returns the playback position in the current window, in milliseconds.
       */
      long getCurrentPosition();

      /**
       * Returns an estimate of the position in the current window up to which data is buffered, in
       * milliseconds.
       */
      long getBufferedPosition();

      /**
       * Returns an estimate of the percentage in the current window up to which data is buffered, or 0
       * if no estimate is available.
       */
      int getBufferedPercentage();

}
