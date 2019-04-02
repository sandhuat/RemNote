package com.example.android.todolist;

/**
 * Created by Dell on 2/25/2016.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Dell on 2/22/2016.
 */
public class NotificationPublisher1 extends BroadcastReceiver
{
    public static MediaPlayer mMediaPlayer;
    Context context1;
    public static String NOTIFICATION_ID = "notification-id1";
    public static String NOTIFICATION = "notification1";
    NotificationCompat.Builder notificationBuilder;
    RemoteViews notificationView;
    public static final String BROADCAST_PLAYBACK_PAUSE = "pause";
    BroadcastReceiver bre;
    String file;String tone_uri;
    public static MediaPlayer mp;NotificationManager notificationManager;int id,jid;
    long repeat=0;int noteType;
    int position;
    String days[]={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
    Vibrator v;
    boolean priority;
    Intent musicIntent;
    String content;

    String ti[]={"AM","PM"};
    public void onReceive(Context context, Intent intent)
    {
        context1=context;
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        v=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        String action = intent.getAction();
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        //    FileName fa=(FileName)intent.getParcelableExtra("FileName");
        Intent myIntent = new Intent(context,MainActivity.class);

        file=intent.getStringExtra("filename1");
        noteType=intent.getIntExtra(file+"Type*@",1);
        position=intent.getIntExtra(file+"position%$",-1);
        Log.e("TAG","position in NotificationPublisher is "+position);

        content=intent.getStringExtra(file+"Content*&^");
        id = intent.getIntExtra(NOTIFICATION_ID, 0);
        Log.e("TAG","Notification ID ABOVE is "+id);
        //  jid=intent.getIntExtra("RemID",-1);
        String nb=intent.getStringExtra("path");
        Log.e("TAG","nb in NotificcationPublisher is "+nb);
        SharedPreferences sharedpreferences =context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        mp=new MediaPlayer();
        SharedPreferences.Editor editor = sharedpreferences.edit();
        String akaron = sharedpreferences.getString(file, "993");
        boolean isNotePresent=sharedpreferences.getBoolean(file+".txt"+".*exists&*", false);
        priority=sharedpreferences.getBoolean(file + ".txtpriority#$@", false);

        repeat=sharedpreferences.getLong(file+".txtRESET*$#time", 0);
        int alarmTone=sharedpreferences.getInt(file+".txtREMINDER#$@tone",-1);
        Log.e("TAG","ALARM TONE INT IS "+alarmTone);
        jid=sharedpreferences.getInt(file+".txtRemindPOSIT",-1);

        if(noteType==3)
        {
            alarmTone=sharedpreferences.getInt(file+".3gpREMINDER#$@tone",-1);
            repeat=sharedpreferences.getLong(file+".3gpRESET*$#time", 0);

            priority=sharedpreferences.getBoolean(file + ".3gppriority#$@", false);

            isNotePresent=sharedpreferences.getBoolean(file+".3gp"+".*exists&*", false);
            jid=sharedpreferences.getInt(file+".3gpRemindPOSIT",-1);

        }
        tone_uri=sharedpreferences.getString(file+".txtcustom4*$tone","");
        if(noteType==3)
        {

            tone_uri=sharedpreferences.getString(file+".3gp%&SET*tone","");

        }
        Log.e("TAG","Tonews is "+tone_uri);
        if(repeat==0)
        {
            if (noteType == 3) {
                editor.remove(file + ".3gpRemindPOSIT");
                editor.putBoolean(file + ".3gpREMINDER#$@", false);
            } else {
                editor.putBoolean(file + ".txtREMINDER#$@", false);
                editor.remove(file + ".txtRemindPOSIT");
                Log.e("TAG","Removing Reminder");
            }
            editor.apply();
        }
        Log.e("sad", "FILEEE NAME IN NOTE MANAGER IS " + file);

        editor.commit();
        try {
            Main2.adapter.notifyDataSetChanged();
        }
        catch(NullPointerException e)
        {

        }
        if(action!=null && action.equals("DISMISS"))
        {
            id = intent.getIntExtra("NOTIFICATION_ID", 0);
            Log.e("TAG","Notification ID for dismissal is "+id);


if(MediaNotificationService.mediaPlayer!=null)
{
    MediaNotificationService.mediaPlayer.stop();
    MediaNotificationService.mediaPlayer.release();
    MediaNotificationService.mediaPlayer=null;

    musicIntent=new Intent(context, MediaNotificationService.class);
context.stopService(musicIntent);


}
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.cancel(id);



        }
        else
        {

            Log.e("TAG","Notification ID for dismissal0 is "+id);
            if(priority)
            {
                Intent i=new Intent(context,WholeScreenAlarmActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("LOC_FIN",file);
                i.putExtra("NoteType",noteType);
                i.putExtra("alarmTone",alarmTone);
                Log.e("TAG","Position before sending to Whole Screen Activity is "+position);
                i.putExtra("position",position);
                i.putExtra("NotGEOLoc",true);
                context.startActivity(i);
            }
            else
            {
        if(jid!=-1)
        {

            Main2.adapter.notifyDataSetChanged();
            ReminderListActivity.filenames1.remove(jid);
            Log.e("as","THE MAIN2 FILENAMES lIST IS "+Main2.filenames);
            Log.e("as","THE REMINDER FILENAMES lIST IS "+ ReminderListActivity.filenames1);

            ReminderListActivity.adapter.notifyDataSetChanged();
        }

        //  mp=MediaPlayer.create(context, Uri.parse(nb));

        if(nb!=null )
        {
        /*    Intent playbackServiceIntent = new Intent(context, MediaPlayerService.class);
            playbackServiceIntent.putExtra("song", nb);
            playbackServiceIntent.putExtra("name", file);
            playbackServiceIntent.putExtra("id", id);
            Log.e("dasd", "FILEEID IS " + id);
            context.startService(playbackServiceIntent);*/
            playSound(context, Uri.parse(nb));
            showNotification();
        }
       /*if(nb!=null)
       {
                mp.start();

     //      Ringtone r = RingtoneManager.getRingtone(context, Uri.parse(nb));
       //    r.play();
       }*/

     /*   try {
            if (action.equals("asd"))
                if (mp.isPlaying())
                    mp.pause();
        }
        catch(NullPointerException e)
        {

        }*/
        //notification.sound = Uri.parse("android.resource://my.package.name/raw/notification");
        Log.e("asd", "FILEN IN NOTIFICATION " + file);


        // Get instance of Vibrator from current Context

        Log.e("df","AKARON VALUE I GET IS "+akaron);
        // if(!akaron.equals("993"))
        // {

        Log.e("TAG","VAL of nb is "+nb);
                Log.e("TAG","VAL of isNotePresent is "+isNotePresent);

        if(nb==null && isNotePresent )
        {
            boolean isRepeatOn=sharedpreferences.getBoolean(file+".txtREPEA*&%T",false);
            if(noteType==3)
                isRepeatOn=sharedpreferences.getBoolean(file+".3gpREPEA*&%T",false);
       Log.e("TAG","VALue of repeatOn is "+isRepeatOn);
            if(isRepeatOn)
            {
                ReminderDescActivity.switchCompat.setChecked(true);
                Calendar calendar=Calendar.getInstance();
                long newAlarmTime=calendar.getTimeInMillis()+repeat;


                int month=calendar.get(Calendar.MONTH)+1;

                calendar.setTimeInMillis(newAlarmTime);
                String date=calendar.get(Calendar.DATE)+"/"+month+"/"+calendar.get(Calendar.YEAR)+" "+days[calendar.get(Calendar.DAY_OF_WEEK)-1];
                String timeMag=calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+" "+ti[calendar.get(Calendar.AM_PM)];
               if(noteType==3)
               {
                   editor.putString(file + ".3gpDATE*&^", date);
                   editor.putString(file + ".3gpTIME*&^", timeMag);
                   editor.putLong(file + ".3gpNew*&@Time", newAlarmTime);
                   editor.apply();
               }
                else {
                   editor.putString(file + ".txtDATE*&^", date);
                   editor.putString(file + ".txtTIME*&^", timeMag);
                   editor.putLong(file + ".txtNew*&@Time", newAlarmTime);
                   editor.apply();

               }
                ReminderDescActivity.date_ED.setText(date);
                ReminderDescActivity.time_ED.setText(timeMag);

            }
            else
            {
                if(noteType==3)
                {
                    editor.remove(file + ".3gpDATE*&^");
                    editor.remove(file + ".3gpTIME*&^");
                    editor.apply();

                }
                else {
                    editor.remove(file + ".txtDATE*&^");
                    editor.remove(file + ".txtTIME*&^");
                    editor.apply();

                }
                ReminderDescActivity.date_ED.setText("Tap to set Reminder");
                ReminderDescActivity.time_ED.setText("");
                ReminderDescActivity.switchCompat.setChecked(false);
            }
if(noteType!=3)
editor.putBoolean(file+".txtREMINDER#$@",false);
            else
    editor.putBoolean(file+".3gpREMINDER#$@",false);

            editor.apply();
            if(alarmTone==1 || alarmTone==3)
            notificationManager.notify(id, notification);

            if(alarmTone==2)
            {
              musicIntent=new Intent(context, MediaNotificationService.class);
                musicIntent.putExtra("filename",file);
                musicIntent.putExtra("note_id",id);
                musicIntent.putExtra("content",content);
                musicIntent.putExtra("position",position);
                musicIntent.putExtra("song",tone_uri);
Log.e("TAG","TAG ONE");
                context.startService(musicIntent);
               // playSound(context,Uri.parse(tone_uri));
            }
            else if(alarmTone==3)
            {
                String say="Reminding you of "+file;
                Intent i=new Intent(context, SpeechService.class);
                i.putExtra("SAY",say);
                Log.e("TAG","BEFORE TTS begins");
                context.startService(i);

            }
        }


        if(isNotePresent && alarmTone==1)
        {
             v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

            //  Uri notification1 = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

// Vibrate for 300 milliseconds
            long[] pattern = {0, 1000, 1000,2000,1000,1000};
            v.vibrate(pattern, -1);

        }
//    }
    }}}
    private void playSound(Context context, Uri alert)
    {
        mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
            {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        }
        catch (IOException e)
        {
            System.out.println("OOPS");
        }
    }
    private void showNotification()
    {
        // Create notification
        Intent intent1 = new Intent(context1, MyBroadcastReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(context1.getApplicationContext(), 0, intent1, 0);
        notificationBuilder = new NotificationCompat.Builder(context1);
      /*          .setSmallIcon(R.drawable.ic_mic_white_48dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(this.url) // audio url will show in notification
                .setContentIntent(PendingIntent.getActivity(getApplicationContext(), 0, new Intent(getApplicationContext(), MainActivity.class), 0))
                .addAction(R.drawable.stop_light, "Stop", makePendingIntent(BROADCAST_PLAYBACK_STOP))
                .addAction(R.drawable.pause_light, "Pause", makePendingIntent(BROADCAST_PLAYBACK_PAUSE));*/
        notificationBuilder.setSmallIcon(R.drawable.microphone);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDeleteIntent(pendingIntent1);

        notificationView = new RemoteViews(context1.getPackageName(), R.layout.notification_buttons);
        notificationView.setTextViewText(R.id.rec_name,file);
        notificationView.setImageViewResource(R.id.pause_notif,R.drawable.ic_pause_white_36dp);
        Intent switchIntent = new Intent(BROADCAST_PLAYBACK_PAUSE);
        switchIntent.putExtra("idd",id);
        Log.e("sad","THE IDD IN Service is "+id);
        // switchIntent.putExtra("Notification", n);
        //switchIntent.putExtra("NotificationID", note_id);
        //Log.e("sadd", "THE ID IN NOTIFF in PlayerActivity is " + note_id);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(context1, 100, switchIntent, 0);
        Bundle extras = new Bundle();
        extras.putInt("IDs", id);
        IntentFilter filter = new IntentFilter();
        filter.addAction(BROADCAST_PLAYBACK_PAUSE);



        bre = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                String action = intent.getAction();
                //  int id2=intent.getIntExtra("idd",1);

                if (action.equals(BROADCAST_PLAYBACK_PAUSE))
                {
                    if (mMediaPlayer.isPlaying())
                    {
                        Log.e("dsn","THE ID IN RECVR is "+id);
                        mMediaPlayer.pause();
                        notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_play_arrow_white_48dp);
                        // startForeground(NOTIFICATION_ID, notificationBuilder.build());
                        notificationManager.notify(id, notificationBuilder.build());


                    } else {
                        mMediaPlayer.start();
                        Log.e("dsn", "THE ID IN RECVR2 is " + id);

                        notificationView.setImageViewResource(R.id.pause_notif, R.drawable.ic_pause_white_48dp);
                        // startForeground(NOTIFICATION_ID, notificationBuilder.build());
                        notificationManager.notify(id, notificationBuilder.build());

                    }
                }
            }
        };
        context1.getApplicationContext().registerReceiver(bre, filter);
        notificationView.setOnClickPendingIntent(R.id.pause_notif, pendingSwitchIntent);
        notificationBuilder.setContent(notificationView);
        // Show notification

        // startForeground(NOTIFICATION_ID, notificationBuilder.build());
        notificationManager.notify(id,notificationBuilder.build());

    }


}
