package com.example.android.todolist;

/**
 * Created by Dell on 5/19/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class MediaNotificationService extends Service implements MediaPlayer.OnCompletionListener {
    public static MediaPlayer mediaPlayer;
    String song;
     NotificationManager notificationManager;
     NotificationCompat.Builder notificationBuilder;
   // MediaPlayerReceiver mRecv;
    int noteType;
    String filename; int position,note_id;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    protected void onHandleIntent(Intent intent) {

    }
    @Override
    public void onCreate()
    {
        Log.e("das", "SONG IS " + song);
        super.onCreate();
        notificationManager = (NotificationManager)getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mediaPlayer = new MediaPlayer();

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
      //  mRecv=new MediaPlayerReceiver();
        //IntentFilter intentFilter = new IntentFilter();

        //intentFilter.addAction(BROADCAST_PLAYBACK_PAUSE);
       // registerReceiver(mRecv, intentFilter);

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


        notificationManager.notify(NOTIFICATION_ID,notificationBuilder.build());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
       // song=(String) intent.getExtras().get("song");
//        NOTIFICATION_ID=(int)intent.getExtras().get("id");
        mediaPlayer=null;
        song=(String) intent.getExtras().get("song");
        int type= intent.getIntExtra("type",-1);

        mediaPlayer = new MediaPlayer();


        final AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        final int originalVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
       Log.e("TAG","SOng in MediaNotificationService is "+song);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);


        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(song));

        mediaPlayer.setLooping(true);
        position=intent.getIntExtra("position",-1);
        String content=intent.getStringExtra("content");
        if(content==null)
            content="";
        note_id=intent.getIntExtra("note_id",-1);
        NOTIFICATION_ID=note_id;
        filename=intent.getStringExtra("filename");
        if(content!=null)
        if(!content.equals("NoNotificationReqd$%##%"))
        {
            Notification n = getNotification(content);
            Log.e("TAG", "TAG TWO");

            notificationManager.notify(NOTIFICATION_ID, n);
        }

        //mediaPlayer.setOnCompletionListener(this);
        if (!mediaPlayer.isPlaying())
        {
            mediaPlayer.start();
        }
        return START_STICKY;
    }
@Override
    public void onDestroy() {
        if (mediaPlayer!=null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer=null;

        }
        super.onDestroy();
        // stopForeground(true);
      //  unregisterReceiver(mRecv);
    }

    public void onCompletion(MediaPlayer _mediaPlayer)
    {
        stopSelf();
    }

    public Notification getNotification(String content)
    {
        noteType=1;

        Intent myIntent = null;
        if(noteType==1)
            myIntent= new Intent(this, NoteActivity.class);
        else if(noteType==2)
            myIntent= new Intent(this, MainActivity.class);

       filename+=".txt";

        myIntent.putExtra("filename", filename);
        myIntent.putExtra("position",position);
       // Log.e(TAG,"RED NOTIFICATION FILENAME IS "+filename);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent,0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(filename.substring(0, filename.length() - 4));
        //Log.e(TAG, "RED NOTIFICATION TITLE IS " + et_title.getText().toString());
        builder.setContentText(content);
       // Log.e(TAG, "RED CONTENT IS " + content);
        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(R.drawable.utilities_notepad_icon);
        builder.setOngoing(true);



        // New Intent for dismissal of Notification

        Intent buttonIntent = new Intent(this, NotificationPublisher1.class);

        buttonIntent.putExtra("NOTIFICATION_ID",note_id);
        buttonIntent.setAction("DISMISS");
        Log.e("TAG","Notif ID in REMDESC is "+note_id);

        PendingIntent btPendingIntent = PendingIntent.getBroadcast(this, note_id, buttonIntent,0);
        builder.addAction(R.drawable.ic_action_cancel, "DISMISS", btPendingIntent);
        Notification n=builder.build();
        n.tickerText=filename.substring(0, filename.length() - 4);
        n.icon=R.drawable.utilities_notepad_icon;
        return n;
    }

}