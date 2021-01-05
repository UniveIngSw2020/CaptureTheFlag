package com.junipero.capturetheflag;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
/*
 * this service is used to manage the main soundtrack of the application
 * the name of this song is Awesomeness from https://opengameart.org/
 */

public class BackgroundSoundService extends Service {
    private MediaPlayer player;
    public IBinder onBind(Intent arg0) {
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        player = MediaPlayer.create(this, R.raw.awesomeness);
        player.setLooping(true); // Set looping
        player.setVolume(30,30);
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        player.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {
        player.stop();
        player.release();
    }
}
