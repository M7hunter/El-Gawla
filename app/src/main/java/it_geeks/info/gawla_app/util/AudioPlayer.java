package it_geeks.info.gawla_app.util;

import android.content.Context;
import android.media.MediaPlayer;

import it_geeks.info.gawla_app.repository.Storage.SharedPrefManager;

public class AudioPlayer {

    private static AudioPlayer audioPlayer;
    private MediaPlayer mMediaPlayer;

    private AudioPlayer() {
    }

    public static AudioPlayer getInstance() {
        if (audioPlayer == null)
        {
            audioPlayer = new AudioPlayer();
        }
        return audioPlayer;
    }

    public void play(Context c, int rid) {
        stop();

        if (SharedPrefManager.getInstance(c).isSoundEnabled())
        {
            mMediaPlayer = MediaPlayer.create(c, rid);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    stop();
                }
            });

            mMediaPlayer.start();
        }
    }

    private void stop() {
        if (mMediaPlayer != null)
        {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    public boolean isPlaying() {
        if (mMediaPlayer != null)
        {
            return mMediaPlayer.isPlaying();
        }
        return false;
    }
}
