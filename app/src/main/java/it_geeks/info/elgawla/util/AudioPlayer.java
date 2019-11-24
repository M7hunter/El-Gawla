package it_geeks.info.elgawla.util;

import android.content.Context;
import android.media.MediaPlayer;

import it_geeks.info.elgawla.repository.Storage.SharedPrefManager;

public class AudioPlayer {

    private static AudioPlayer audioPlayer;
    private MediaPlayer mMediaPlayer;
    private MediaPlayer.OnCompletionListener listener;

    private AudioPlayer() {
        listener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                stop();
            }
        };
    }

    public static AudioPlayer getInstance() {
        if (audioPlayer == null) {
            audioPlayer = new AudioPlayer();
        }
        return audioPlayer;
    }

    public void play(Context context, int mediaResId) {
        stop();

        if (SharedPrefManager.getInstance(context).isSoundEnabled()) {
            mMediaPlayer = MediaPlayer.create(context, mediaResId);
            mMediaPlayer.setOnCompletionListener(listener);

            mMediaPlayer.start();
        }
    }

    private void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }
}
