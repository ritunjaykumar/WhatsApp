package com.softgyan.whatsapp.tools;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class AudioService {
    private static final String TAG = "my_tag";
    private static MediaPlayer mediaPlayer;

    public AudioService() {
    }

    public void playAudioFromUrl(String audioUrl, OnAudioServiceCallBack callBack) {
        try {
            mediaPlayer.stop();
            mediaPlayer = null;
        } catch (Exception e) {
            Log.d(TAG, "playAudioFromUrl: " + e.getMessage());
        }

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(audioUrl);
            mediaPlayer.prepare();
            mediaPlayer.start();
            callBack.onStart(mediaPlayer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                callBack.onFinish();
            }
        });
    }

    public interface OnAudioServiceCallBack {
        void onFinish();
        void onStart(MediaPlayer mediaPlayer);
    }
}
