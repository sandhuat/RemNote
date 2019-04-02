package com.example.android.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dell on 4/15/2016.
 */
public class MediaPlayerReceiver extends BroadcastReceiver {
    public static final String BROADCAST_PLAYBACK_PAUSE = "pause";
    int idd1;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        idd1=intent.getIntExtra("idd",1);
        Log.e("d","THe IDD IN MEDIAplayerReceiver is "+idd1);
        if (action.equals(BROADCAST_PLAYBACK_PAUSE)) {
            if (MediaPlayerService.mediaPlayer.isPlaying()) {

                MediaPlayerService.mediaPlayer.pause();
                MediaPlayerService.notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_play_arrow_white_48dp);
                // startForeground(NOTIFICATION_ID, notificationBuilder.build());
                MediaPlayerService.notificationManager.notify(idd1, MediaPlayerService.notificationBuilder.build());
            }
            else
            {
                MediaPlayerService.mediaPlayer.start();

                MediaPlayerService.notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_pause_white_48dp);
                // startForeground(NOTIFICATION_ID, notificationBuilder.build());
                MediaPlayerService.notificationManager.notify(idd1, MediaPlayerService.notificationBuilder.build());

            }
        }

    }
}