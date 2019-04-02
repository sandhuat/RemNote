package com.example.android.todolist;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

public class MediaPlayerService extends Service implements MediaPlayer.OnCompletionListener
{
    public static MediaPlayer mediaPlayer;
String song;
    String name;
    static RemoteViews notificationView;
    static NotificationManager notificationManager;
    static NotificationCompat.Builder notificationBuilder;
    BroadcastReceiver broadcastReceiver;
    MediaPlayerReceiver mRecv;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    protected void onHandleIntent(Intent intent) {
        song=(String) intent.getExtras().get("song");
    }
    @Override
    public void onCreate() {
        Log.e("das", "SONG IS " + song);
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
       /* broadcastReceiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();

                if (action.equals(BROADCAST_PLAYBACK_STOP)) stopSelf();
                else if (action.equals(BROADCAST_PLAYBACK_PAUSE))
                {
                    if (mediaPlayer.isPlaying()) {

                        mediaPlayer.pause();
                        notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_play_arrow_white_48dp);
                        // startForeground(NOTIFICATION_ID, notificationBuilder.build());
                        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());


                    }
                    else
                    {
                        mediaPlayer.start();

                        notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_pause_white_48dp);
                        // startForeground(NOTIFICATION_ID, notificationBuilder.build());
                        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());

                    }
                }
            }
        };*/
        mRecv=new MediaPlayerReceiver();
        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(BROADCAST_PLAYBACK_PAUSE);
        registerReceiver(mRecv, intentFilter);

    }
   int NOTIFICATION_ID = 1;
    public static final String
            BROADCAST_PLAYBACK_STOP = "stop",
            BROADCAST_PLAYBACK_PAUSE = "pause";

    private PendingIntent makePendingIntent(String broadcast)
    {
        Intent intent = new Intent(broadcast);
        return PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
    }

    private void showNotification()
    {
        // Create notification
        Intent intent1 = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent1, 0);
        notificationBuilder = new NotificationCompat.Builder(this);
      /*          .setSmallIcon(R.drawable.ic_mic_white_48dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(this.url) // audio url will show in notification
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 0))
                .addAction(R.drawable.stop_light, "Stop", makePendingIntent(BROADCAST_PLAYBACK_STOP))
                .addAction(R.drawable.pause_light, "Pause", makePendingIntent(BROADCAST_PLAYBACK_PAUSE));*/
        notificationBuilder.setSmallIcon(R.drawable.microphone);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDeleteIntent(pendingIntent1);

        notificationView = new RemoteViews(getPackageName(), R.layout.notification_buttons);
        notificationView.setTextViewText(R.id.rec_name,name);
        notificationView.setImageViewResource(R.id.pause_notif,R.drawable.ic_pause_white_36dp);
        Intent switchIntent = new Intent(BROADCAST_PLAYBACK_PAUSE);
        switchIntent.putExtra("idd",NOTIFICATION_ID);
        Log.e("sad","THE IDD IN Service is "+NOTIFICATION_ID);
        // switchIntent.putExtra("Notification", n);
        //switchIntent.putExtra("NotificationID", note_id);
        //Log.e("sadd", "THE ID IN NOTIFF in PlayerActivity is " + note_id);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 100, switchIntent, 0);

        notificationView.setOnClickPendingIntent(R.id.pause_notif, pendingSwitchIntent);
        notificationBuilder.setContent(notificationView);
        // Show notification

     // startForeground(NOTIFICATION_ID, notificationBuilder.build());
        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        song=(String) intent.getExtras().get("song");
        NOTIFICATION_ID=(int)intent.getExtras().get("id");
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(song));
        name=(String)intent.getExtras().get("name");
        Log.e("dasd","FILEE IS "+name);

        showNotification();
        mediaPlayer.setOnCompletionListener(this);
        if (!mediaPlayer.isPlaying())
        {
            mediaPlayer.start();
        }
        return START_STICKY;
    }

    public void onDestroy() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
       // stopForeground(true);

        unregisterReceiver(mRecv);
    }

    public void onCompletion(MediaPlayer _mediaPlayer)
    {
        stopSelf();
    }

}