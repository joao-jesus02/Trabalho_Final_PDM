package com.example.bdroomcomcamera.utils;

import android.content.Context;
import android.media.MediaPlayer;
import android.widget.Toast;

import com.example.bdroomcomcamera.R;

public final class SoundUtils {

    private SoundUtils() {
    }

    public static void tocarClique(Context context) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.click);
        if (mediaPlayer == null) {
            Toast.makeText(context, "Som de clique indisponível.", Toast.LENGTH_SHORT).show();
            return;
        }

        mediaPlayer.setOnCompletionListener(MediaPlayer::release);
        mediaPlayer.start();
    }
}
