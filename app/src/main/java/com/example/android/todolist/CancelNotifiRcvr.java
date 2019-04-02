package com.example.android.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Dell on 5/6/2016.
 */
public class CancelNotifiRcvr extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

            // your code
              NotificationPublisher1.mMediaPlayer.stop();

    }
}
