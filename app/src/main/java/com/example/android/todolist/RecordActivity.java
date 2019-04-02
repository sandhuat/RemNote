package com.example.android.todolist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class RecordActivity extends AppCompatActivity {
    ImageButton record,stop,play,pause,new_btn;
    private MediaRecorder myAudioRecorder;
    private String outputFile=null;
    EditText aud_title;
    boolean isRecording=false;
    MediaPlayer m;
    boolean playing=false;
    int note_id;SeekBar seek1;
    TextView time;    Timer t;
    private double finalTime=0;
    private Handler myHandler;
    private double startTime=0;
    VisualizerView visualizerView;
    Handler viewHandler;
    boolean isRecorded;
    Runnable updater;
    public static final int REPEAT_INTERVAL = 40;

    int cnt=0; int minute =0, seconds = 0, hour = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        MainActivity.instanceCounter++;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
note_id=MainActivity.instanceCounter;
aud_title=(EditText)findViewById(R.id.textView);
        play=(ImageButton)findViewById(R.id.button3);
        stop=(ImageButton)findViewById(R.id.button2);
        record=(ImageButton)findViewById(R.id.button);
        pause=(ImageButton)findViewById(R.id.button4);
        time=(TextView)findViewById(R.id.time1);
        seek1=(SeekBar)findViewById(R.id.seek1);
        new_btn=(ImageButton)findViewById(R.id.new_btn);
        new_btn.setEnabled(false);
        stop.setEnabled(false);
        visualizerView = (VisualizerView) findViewById(R.id.visualizer);
        play.setEnabled(false);
        play.setColorFilter(Color.argb(255, 85, 89, 99));
        stop.setColorFilter(Color.argb(255, 85, 89, 99));
        new_btn.setColorFilter(Color.argb(255, 85, 89, 99));

        viewHandler=new Handler();

        m = new MediaPlayer();
        isRecorded=false;

//android.app.ActionBar toolbar=getActionBar();
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO))
            {


              showPermissionAlert();


            }
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 124);


            }


        }
        else
        {
           init_audio();
        }}
     Runnable UpdateSongTime=new Runnable()
    {
        public void run()
        {
            if(m!=null) {
                startTime = m.getCurrentPosition();
                time.setText(String.format("%d:%d",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                        toMinutes((long) startTime))));


                seek1.setProgress((int) startTime);
                myHandler.postDelayed(this, 100);

            }
        }

    };

    public void  init_audio()
    {
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);
        myHandler = new Handler();

        Log.e("dsa", "AUDIO RECORDER i1s " + myAudioRecorder);


        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                time.setVisibility(View.VISIBLE);
                record.setColorFilter(Color.argb(255, 23, 79, 114));
                stop.setColorFilter(Color.argb(255, 0, 0, 0));
                isRecording = true;
                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                    t = new Timer("hello", true);
                    t.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            time.post(new Runnable() {

                                public void run() {
                                    seconds++;
                                    if (seconds == 60) {
                                        seconds = 0;
                                        minute++;
                                    }
                                    if (minute == 60) {
                                        minute++;
                                    }
                                    time.setText((minute > 9 ? minute : ("0" + minute))
                                            + ":"
                                            + (seconds > 9 ? seconds : "0" + seconds));

                                }
                            });

                        }
                    }, 1000, 1000);
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                viewHandler.post(updateVisualizer);
                record.setEnabled(false);
                stop.setEnabled(true);
                Toast.makeText(getApplicationContext(), "Recording Started.", Toast.LENGTH_LONG).show();

            }
        });
        MediaPlayer.OnCompletionListener cListener = new MediaPlayer.OnCompletionListener() {

            public void onCompletion(MediaPlayer mp) {
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.INVISIBLE);
            }
        };
        m.setOnCompletionListener(cListener);


        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (seconds > 5) {
                    record.setColorFilter(Color.argb(255, 85, 89, 99));
                    stop.setColorFilter(Color.argb(255, 185, 15, 21));
                    play.setColorFilter(Color.argb(255, 0, 0, 0));
                    new_btn.setEnabled(true);
                    new_btn.setColorFilter(Color.argb(255, 0, 0, 0));
                    Log.e("dsa", "AUDIO RECORDER is " + myAudioRecorder);
                    myAudioRecorder.stop();
                    t.cancel();
                    visualizerView.clear();
                    isRecorded = true;
                    viewHandler.removeCallbacks(updateVisualizer);
                    myAudioRecorder.release();
                    myAudioRecorder = null;
                    stop.setEnabled(false);
                    play.setEnabled(true);
                    Toast.makeText(getApplicationContext(), "Audio recorded successfully", Toast.LENGTH_LONG).show();
                    isRecording = false;
                } else
                    Toast.makeText(getApplicationContext(), "Minimum duration required - 5 sec", Toast.LENGTH_LONG).show();

            }
        });
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) throws IllegalArgumentException, SecurityException, IllegalStateException {
                record.setColorFilter(Color.argb(255, 85, 89, 99));
                pause.setColorFilter(Color.argb(255, 0, 77, 79));
                stop.setColorFilter(Color.argb(255, 85, 89, 99));
                seek1.setVisibility(View.VISIBLE);
                play.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
                if (!playing) {
                    try {
                        m.setDataSource(outputFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        m.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    m.start();
                    playing = true;
                } else {
                    m.start();
                }
                finalTime = m.getDuration();
                startTime = m.getCurrentPosition();

                seek1.setMax((int) finalTime);
                seek1.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
                Toast.makeText(getApplicationContext(), "Playing audio", Toast.LENGTH_LONG).show();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pause.setColorFilter(Color.argb(255, 0, 0, 0));
                pause.setVisibility(View.INVISIBLE);
                play.setVisibility(View.VISIBLE);
                play.setColorFilter(Color.argb(255, 0, 0, 0));


                m.pause();
                Toast.makeText(getApplicationContext(), "Recording Paused", Toast.LENGTH_LONG).show();

            }
        });
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                //   volBar.setAlpha(0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean fromUser) {
                if (fromUser) {
                    m.seekTo(progress);
                }


            }
        });
        new_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                if (m.isPlaying())
                    m.pause();
                finish();
                startActivity(intent);
            }
        });
    }
    // updates the visualizer every 50 milliseconds
    Runnable updateVisualizer = new Runnable() {
        @Override
        public void run() {
            if (isRecording) // if we are already recording
            {
                // get the current amplitude
                int x = myAudioRecorder.getMaxAmplitude();
                visualizerView.addAmplitude(x); // update the VisualizeView
                visualizerView.invalidate(); // refresh the VisualizerView

                // update in 40 milliseconds
                viewHandler.postDelayed(this, REPEAT_INTERVAL);
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }
    public void saveRecording()
    {
        if(aud_title.getText().length()>0 && myAudioRecorder == null)
        {

            File oldFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp");

            oldFile.renameTo(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDo/" + aud_title.getText().toString() + ".3gp"));

            SharedPreferences sharedpreferences1 = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor1 = sharedpreferences1.edit();

            editor1.putBoolean(aud_title.getText() + "REMINDER#$@", true);
            editor1.putString(aud_title.getText().toString()+".3gp","three");
            //editor1.putString(aud_title.getText() + "DATE*&^", date);4
            editor1.putInt(aud_title.getText()+"NoteID",-1);
            editor1.commit();
            play.setEnabled(false);
            record.setEnabled(true);
            stop.setEnabled(true);
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            Log.e("TAG", "The FILE SAVED ARE " + aud_title.getText().toString() + ".3gp");
            //note_id= sharedpreferences.getInt(tv.getText() + "NoteID#$@", -1);
            note_id=MainActivity.instanceCounter;
            MainActivity.instanceCounter++;

            note_id++;
            editor.putInt(aud_title.getText().toString() + "NoteID#$@",note_id);
            editor.commit();
            if(m.isPlaying())
                m.pause();
         //  finish();
            play.setEnabled(true);
            record.setEnabled(false);
            stop.setEnabled(false);
            Toast.makeText(getApplicationContext(), "Recording saved", Toast.LENGTH_SHORT).show();
            //note_id= sharedpreferences.getInt(tv.getText() + "NoteID#$@", -1);

            Intent i=new Intent(RecordActivity.this,Main2.class);
            Main2.newViewCond=false;
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
           //finish();

        }
        else if(myAudioRecorder!=null)
            Toast.makeText(getApplicationContext(),"You need to record to save",Toast.LENGTH_SHORT).show();
else
        Toast.makeText(getApplicationContext(),"Write a description to save the log",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.action_save:
                saveRecording();
                return true;

            case R.id.action_save1:
                saveRecording();
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }
    private void sendSaveAlert()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Cancel Recording");
        alert.setMessage("Mic is still recording. Do you want to continue?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {


            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
                myAudioRecorder.stop();
                 myAudioRecorder.release();
                visualizerView.clear();
                viewHandler.removeCallbacks(updateVisualizer);

                finish();
            }
        });

        alert.show();




    }
    private void sendSaveAlert2()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Save Recording");
        alert.setMessage("New audio recorded. Do you want to save?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                saveRecording();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.


                finish();
            }
        });

        alert.show();




    }
    public void onBackPressed()
    {


        if(m.isPlaying())
            m.pause();
if(isRecording)
{
    sendSaveAlert();
    //myAudioRecorder.stop();

   // myAudioRecorder.release();
}
else if(isRecorded)
{
    sendSaveAlert2();
}
else
{
    super.onBackPressed();
    Intent goToMainActivity = new Intent(getApplicationContext(), Main2.class);
    goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Will clear out your activity history stack till now
    startActivity(goToMainActivity);
}
    }
    public void showPermissionAlert()
    {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Recording Permission");
        alert.setMessage("Allow app access to record audio for the VoiceLog?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton) {
                ActivityCompat.requestPermissions(RecordActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 124);


            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//super.onBackPressed();
                alert.show();

            }
        });

        alert.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode) {

            case 124:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    init_audio();

                }
                else
                {
                  showPermissionAlert();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

}
