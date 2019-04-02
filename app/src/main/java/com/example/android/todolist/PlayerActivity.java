package com.example.android.todolist;


import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener
{

    ImageButton play_btn;
    ImageButton pause_btn,mic_btn,vol_btn;
    private Bundle extras;
    String filename;
    TextView tv;
    boolean vol;
    Calendar calendar;
    public static RemoteViews notificationView;
    long differ;
    SeekBar seekBar,volBar;FileName f;
    int pos;MediaButtonReceiver myBroadcastReceiver;
    private MediaPlayer mp;
    ImageButton loop_btn;
    int note_id;
    Handler mHandler;boolean reminderSet=false;
    int delay=4000;
    TextView t2;
    View yourSeekBarView;
    AudioManager audioManager;
    private double startTime=0;
    Runnable mRunnable;
    boolean loopcond;
    FragmentManager fm;
    private static final String TAG="dads";
    private double finalTime=0;
    private Handler myHandler=new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        vol=false;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        loopcond=false;
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
      //  audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_SAME, AudioManager.FLAG_SHOW_UI);

       // audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        tv=(TextView)findViewById(R.id.aud_name);
        t2=(TextView)findViewById(R.id.dur);
        mHandler = new Handler();
        seekBar=(SeekBar)findViewById(R.id.sekkbar);
vol_btn=(ImageButton)findViewById(R.id.vol_btn);
        yourSeekBarView = getLayoutInflater().inflate(R.layout.seekbar, null, false);
        volBar=(SeekBar)findViewById(R.id.vol_control);
pause_btn=(ImageButton)findViewById(R.id.pause_btn);
        loop_btn=(ImageButton)findViewById(R.id.loop_btn);
        mic_btn=(ImageButton)findViewById(R.id.mic_btn);
play_btn=(ImageButton)findViewById(R.id.play_btn);
        mp=new MediaPlayer();
     volBar.setMax(maxVolume);
       f= ListViewAdapter.filenames.get(pos);


//android.app.ActionBar toolbar=getActionBar();
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fm = getSupportFragmentManager();
        volBar.setProgress(curVolume);
        calendar = Calendar.getInstance();
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        mRunnable = new Runnable() {
            @Override
            public void run() {
                Animation anim = new AlphaAnimation(1, 0);
                anim.setDuration(1000);
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {

                        volBar.setAlpha(0);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                });
                         volBar.startAnimation(anim);

            }
        };

        extras = getIntent().getExtras();

        if(extras != null)
        {
            filename = extras.getString("filename");
          //  pos=extras.getInt("position");
            int len=filename.length();
            //FileName f=ListViewAdapter.filenames.get(pos);
            tv.setText(filename.substring(0, len - 4));
            note_id= sharedpreferences.getInt(tv.getText() + "NoteID#$@", -1);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putBoolean(filename+".*exists&*",true);
            editor.apply();

        }
        volBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                mHandler.postDelayed(mRunnable, delay);
             //   volBar.setAlpha(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
                mHandler.removeCallbacks(mRunnable);
            }
            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
               // audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, arg1, 0);
              //  AudioManager manager = (AudioManager) getSystemService(AUDIO_SERVICE);
                 audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,progress,0);


            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                //   volBar.setAlpha(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }
            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
            if(fromUser)
            {
                mp.seekTo(progress);
            }


            }
        });

vol_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if (volBar.getAlpha() == 0) {

            Animation anim = new AlphaAnimation(0, 1);
            anim.setDuration(1000);
            //anim.setInterpolator(new DecelerateInterpolator());
            anim.setAnimationListener(new Animation.AnimationListener() {
                                          @Override
                                          public void onAnimationStart(Animation animation) {

                                          }

                                          @Override
                                          public void onAnimationEnd(Animation animation) {
                                              mHandler.postDelayed(mRunnable, delay);
                                              volBar.setAlpha(1);
                                          }

                                          @Override
                                          public void onAnimationRepeat(Animation animation) {
                                          }
                                      }
            );
            anim.setRepeatCount(0);
            volBar.setAlpha(1);
            volBar.startAnimation(anim);

        }

    }
});
        mic_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RecordActivity.class);
                if(mp.isPlaying())
                    mp.pause();
                startActivity(i);

            }
        });
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              // mp = MediaPlayer.create(this,Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/ToDo/"+filename));
                mp=MediaPlayer.create(getApplicationContext(), Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDo/" +filename));
                MediaPlayer.OnCompletionListener cListener = new MediaPlayer.OnCompletionListener(){

                    public void onCompletion(MediaPlayer mp){
                        play_btn.setVisibility(View.VISIBLE);
                        pause_btn.setVisibility(View.INVISIBLE);
                    }
                };

                    mp.setOnCompletionListener(cListener);
                if(loopcond)
                    mp.setLooping(true);
                else
                mp.setLooping(false);
                mp.start();
                finalTime = mp.getDuration();
                startTime = mp.getCurrentPosition();

                seekBar.setMax((int) finalTime);
                seekBar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                play_btn.setVisibility(View.INVISIBLE);
                pause_btn.setVisibility(View.VISIBLE);
            }
        });

        pause_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // mp = MediaPlayer.create(this,Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/ToDo/"+filename));

                mp.pause();
                play_btn.setVisibility(View.VISIBLE);
                pause_btn.setVisibility(View.INVISIBLE);
            }
        });
        loop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mp.isLooping()) {
                    mp.setLooping(false);
                    loopcond=false;
                    Log.e("asdas","STOP LOOP");

                    loop_btn.setColorFilter(Color.argb(255, 0, 0, 0));
                }
                else
                {
                    Log.e("asdas","WIll LOOP");
                    mp.setLooping(true);
                    loopcond=true;
                    loop_btn.setColorFilter(Color.argb(255,37,133,174));
                }
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_player, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_remind_aud:
                remindNote1();
                return true;
            case R.id.action_delete1:
                deleteNote();
                return true;

            case R.id.action_share:
                shareRecord();
                return true;

            case R.id.action_backup:
              saveToDrive();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void remindNote()
    {
        DatePickerFragment dateDialog = new DatePickerFragment();
        dateDialog.show(fm, "fragment_date");
    }
    public void saveToDrive()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Note Backup");
        alert.setMessage("Are you sure you want to back up the recording on Google Drive?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(getApplicationContext(), CreateFolderActivity.class);
                i.putExtra("title",tv.getText().toString());
                i.putExtra("content","MUx76cc^g.h");
                startActivity(i);

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//super.onBackPressed();


            }
        });
        alert.show();

    }

    public void shareRecord()
    {
        if(filename==null)
        {
            Toast.makeText(this,"Save the file before sharing",Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("audio/*");
        Log.e("TAG", "The filename in SHARE is " + filename);
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory() + "/Todo/" + filename));
        File file_to_transfer = new File(Environment.getExternalStorageDirectory() +"/Todo/"+filename);
        startActivity(Intent.createChooser(intent, "Share Recording"));

    }
    @Override
    public void onDateSet(int year, int month, int day)
    {
        calendar.set(year, month, day);

        // Start Time dialog
        TimePickerFragment timeDialog = new TimePickerFragment();
        timeDialog.show(fm, "fragment_time");
    }

    @Override
    public void onTimeSet(int hourOfDay, int minute) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 00);
        Log.e(TAG, "THe date and time is " + (calendar.get(Calendar.YEAR) + " " + (calendar.get(Calendar.MONTH) + 1) + " "
                + calendar.get(Calendar.DAY_OF_MONTH) + " " + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE)));
        Calendar c=Calendar.getInstance();
        Log.e(TAG, "THe date and time is " + (c.get(Calendar.YEAR) + " " + (c.get(Calendar.MONTH) + 1) + " "
                + c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.HOUR_OF_DAY) + ":"
                + c.get(Calendar.MINUTE)));
        Log.e(TAG,"the set time "+calendar.getTimeInMillis());
        Log.e(TAG,"the now time "+calendar.getTimeInMillis());

        differ= (calendar.getTimeInMillis()-c.getTimeInMillis());
        String days[]={"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        Log.e("TAG", "DAY OF THE WEEK " + calendar.get(Calendar.DAY_OF_WEEK));
        Log.e("TAG", "MONTH OF THE WEEK " + calendar.get(Calendar.MONTH));
        int month=calendar.get(Calendar.MONTH)+1;
        String ti[]={"AM","PM"};
        String date=calendar.get(Calendar.DATE)+"/"+month+"/"+calendar.get(Calendar.YEAR)+" "+days[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+" "+ti[calendar.get(Calendar.AM_PM)];
        Log.e(TAG, "RED DIFFERENCE IN TIME IS " + differ);
      //  Log.e(TAG, "Something in note is " + et_note.getText().toString());
        if(differ<0)
            Toast.makeText(getApplicationContext(), "Unable to set Reminder in the past.", Toast.LENGTH_SHORT).show();
        else
        {
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putBoolean(filename+"REMINDER#$@",true);
            Main2.adapter.notifyDataSetChanged();
            editor.putString(filename+"DATE*&^",date);
            editor.commit();
            scheduleNotification(getNotification(tv.getText().toString()), differ);

        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (volBar.getAlpha() == 0) {
            Animation anim = new AlphaAnimation(0, 1);
            anim.setDuration(1000);
            //anim.setInterpolator(new DecelerateInterpolator());
            anim.setAnimationListener(new Animation.AnimationListener() {
                                          @Override
                                          public void onAnimationStart(Animation animation) {

                                          }

                                          @Override
                                          public void onAnimationEnd(Animation animation) {
                                              mHandler.postDelayed(mRunnable, delay);
                                              volBar.setAlpha(1);
                                          }

                                          @Override
                                          public void onAnimationRepeat(Animation animation) {
                                          }
                                      }
            );
            anim.setRepeatCount(0);
            volBar.setAlpha(1);
            volBar.startAnimation(anim);
        }
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            int index = volBar.getProgress();
            volBar.setProgress(index + 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            int index = volBar.getProgress();
            volBar.setProgress(index - 1);
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    private void scheduleNotification(Notification notification, long delay)
    {


        Intent notificationIntent = new Intent(this, NotificationPublisher1.class);

        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION_ID, note_id);
        notificationIntent.putExtra("filename","three");
        notificationIntent.putExtra("filename1",tv.getText());

        notificationIntent.putExtra("path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDo/" + filename);
        //  Log.e(TAG,"RED NOTE id is "+note_id);
        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,note_id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        Log.e(TAG, "RED futureInMillis is " + futureInMillis);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        Toast.makeText(this,"Reminder is set.",Toast.LENGTH_SHORT).show();
        ListViewAdapter.filenames.get(pos).reminderSet(true);
        reminderSet=true;
    }

    public Notification getNotification(String content)
    {
        Intent myIntent = new Intent(this, NoteActivity.class);
        myIntent.putExtra("filename", filename);

        Intent intent1 = new Intent(this, MyBroadcastReceiver.class);
        PendingIntent pendingIntent1 = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent1, 0);

        Intent deleteIntent = new Intent(this,NotificationPublisher1.class);
        deleteIntent.setAction("ads");
        PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, deleteIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Log.e(TAG, "RED NOTIFICATION FILENAME IS " + filename);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent,0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(tv.getText().toString());
        Log.e(TAG, "RED NOTIFICATION TITLE IS " + tv.getText().toString());
        builder.setContentText("Sound Reccording");
        Log.e(TAG, "RED CONTENT IS " + content);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.microphone);
        builder.setDeleteIntent(pendingIntent1);
      notificationView = new RemoteViews(getPackageName(), R.layout.notification_buttons);
        notificationView.setTextViewText(R.id.rec_name, tv.getText().toString());
        notificationView.setImageViewResource(R.id.pause_notif,R.drawable.ic_pause_white_36dp);
        Notification n= builder.build();

        n.tickerText=tv.getText().toString();
        n.icon=R.drawable.microphone;

        n.contentView = notificationView;
        Intent switchIntent = new Intent("com.example.app.ACTION_PLAY");
        switchIntent.putExtra("Notification", n);
        switchIntent.putExtra("NotificationID", note_id);
Log.e("sadd","THE ID IN NOTIFF in PlayerActivity is "+note_id);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 100, switchIntent, 0);

        notificationView.setOnClickPendingIntent(R.id.pause_notif, pendingSwitchIntent);

       // n.sound=Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDo/" +filename);
        return n;

    }


    private Runnable UpdateSongTime=new Runnable()
    {
        public void run()
        {
            // Need to save these values
            if(mp!=null) {
                startTime = mp.getCurrentPosition();
               t2.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime))));


                seekBar.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);

            }
        }

    };
    private void deleteNote()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Delete Note");
        alert.setMessage("Are you sure you want to delete the note?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                new NoteManager(PlayerActivity.this).deleteNotea(filename,true,f);
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();




    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
       if(mp.isPlaying())
           mp.pause();

    }
    public void remindNote1()
    {
        Intent i=new Intent(this,ReminderDescActivity.class);
        i.putExtra("filename",filename);
        i.putExtra("note_id",note_id);
        i.putExtra("note_type",3);
        i.putExtra("position",pos);
        i.putExtra("Content","Sound Recording");


        startActivity(i);
    }


}
