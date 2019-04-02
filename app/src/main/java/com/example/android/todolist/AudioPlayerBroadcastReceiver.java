package com.example.android.todolist;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Dell on 4/12/2016.
 */
public class AudioPlayerBroadcastReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        int id = intent.getIntExtra("NotificationID", 0);
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra("notification");
        Log.e("dasd","The ID in NOTIFF IS "+id);

// Sets an ID for the no

        if(action.equalsIgnoreCase("com.example.app.ACTION_PLAY")){
            if(NotificationPublisher1.mp.isPlaying())
            {
               // PlayerActivity.notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_play_arrow_white_48dp);
               // PlayerActivity.notificationView.setViewVisibility(R.id.pause_notif, View.INVISIBLE);
                //PlayerActivity.notificationView.setViewVisibility(R.id.play_notif, View.VISIBLE);
  //              PlayerActivity.notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_play_arrow_white_48dp);

//                notification.contentView = PlayerActivity.notificationView;
                NotificationPublisher1.mp.pause();

    //            mNotificationManager.notify(3, notification);

            }
            else if(!NotificationPublisher1.mp.isPlaying())
            {
               // PlayerActivity.notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_pause_white_48dp);
                //PlayerActivity.notificationView.setViewVisibility(R.id.play_notif, View.INVISIBLE);
                //PlayerActivity.notificationView.setViewVisibility(R.id.pause_notif, View.VISIBLE);
                PlayerActivity.notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_pause_white_36dp);

           //     notification.contentView = PlayerActivity.notificationView;
                NotificationPublisher1.mp.start();

             //   mNotificationManager.notify(id, notification);

            }


        }
    }

}