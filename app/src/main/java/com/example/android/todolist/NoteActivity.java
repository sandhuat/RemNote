package com.example.android.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todolist.colorPicker.ColorPickerDialogDash;
import com.example.android.todolist.colorPicker.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener
{
    long differ;
    private Calendar calendar;
    FragmentManager fm;
    Button time;
    Calendar cal;
    private static final String TAG="sad";
    private NoteManager nm;
    private String filename = null;
    String filename1="";
    private Bundle extras;
    private EditText et_note;
    LinedEditText et_note1;
    TextView lastModified;
    boolean note_type=false;
    int pos;
    String orig="",orig2="";
    int note_id = 0;
    String name_init;
    private EditText et_title;
    boolean reminderSet=false;
    String et2_note; String text;
    FileName f;

    //Color picker variables
    // Only for Menu

    private String[] menuItems;
    private static final int MENU_DASH_0 = 0;
    private static final int MENU_DASH_1 = 1;
    private static final int MENU_DASH_2 = 2;
    private static final int MENU_CALENDAR_0 = 100;
    private static final int MENU_CALENDAR_1 = 101;
    // ---------------------------------------------------------------

    // Selected colors
    private int mSelectedColorDash0 = 0;
    private int mSelectedColorDash1 = 0;
    private int mSelectedColorCal0 = 0;
    private int mSelectedColorCal1 = 0;

    // Keys for savedInstanceState
    private final static String KEY_BUNDLE_POSITION = "KEY_BUNDLE_POSITION";
    private final static String KEY_BUNDLE_SCD0 = "KEY_BUNDLE_SCD0";
    private final static String KEY_BUNDLE_SCD1 = "KEY_BUNDLE_SCD1";
    private final static String KEY_BUNDLE_SCC0 = "KEY_BUNDLE_SCC0";
    private final static String KEY_BUNDLE_SCC1 = "KEY_BUNDLE_SCC1";
    int col;
    int mLastPosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note);
        MainActivity.instanceCounter++;
        et_note = (EditText) findViewById(R.id.et_note);
        et_title=(EditText)findViewById(R.id.et_title);
        et_note1 = (LinedEditText) findViewById(R.id.et_note1);
        note_id=MainActivity.instanceCounter;
        lastModified=(TextView)findViewById(R.id.date);
        extras = getIntent().getExtras();
        name_init=et_title.getText().toString();
        calendar = Calendar.getInstance();
        fm = getSupportFragmentManager();

       this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
      setSupportActionBar(toolbar);
//android.app.ActionBar toolbar=getActionBar();
        // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar2);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
onBackPressed();
            }
        });

        if(extras != null)
        {
            filename = extras.getString("filename");
            filename1=filename;
            pos=extras.getInt("position");
           // Log.e("fgh","The size of filenames in NoteActivity is "+)
            if(Main2.filenames==null)
            {
                nm = new NoteManager(NoteActivity.this);

                Main2.filenames = nm.GetNotesList();

            }
            f = Main2.filenames.get(pos);
            long d=f.getLongDate();
            Date date=new Date(d);
            String cx;
            SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yyyy HH:mm");
            cx = format1.format(date);
            lastModified.setText("Last Modified - "+cx);
            int len=filename.length();
            nm = new NoteManager(NoteActivity.this);
            String text = nm.readNote(filename);
            et2_note=text;
            et_title.setText(filename.substring(0, len - 4));


            //Color picker stuff begin
// Get selected colors
            restoreSelectedColor(savedInstanceState);

            // initialize menu
            _initMenu();

            ColorPickerDialogDash colordash = (ColorPickerDialogDash) getFragmentManager().findFragmentByTag("dash");
            if (colordash != null) {
                // re-bind listener to fragment
                colordash.setOnColorSelectedListener(colordashListner);
            }

            //Color picker stuff end
             SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            note_type=sharedpreferences.getBoolean(filename+"LAYOUT#$@",false);
             col=sharedpreferences.getInt(filename+"CoLoR",-1);
            if(col!=-1)
                setActivityBackgroundColor(col);

            if(note_type)
            {
                et_note1.setVisibility(View.VISIBLE);
                et_note.setVisibility(View.INVISIBLE);
                et_note1.setText(text);

                Log.e("asdda", "NOTE TYPE IS TRUE");
                orig=text;
                orig2=orig;
                et_note1.setSelection(et_note1.getText().length());
                et_note1.requestFocus();
            }
            else
            {
                et_note1.setVisibility(View.INVISIBLE);
                et_note.setVisibility(View.VISIBLE);
                et_note.setText(text);
                orig=text;
                orig2=orig;
                Log.e("asdda", "NOTE TYPE IS FALSE");

                et_note.setSelection(et_note.getText().length());
                et_note.requestFocus();
            }

        }

        et_note.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                orig2=s.toString();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });
        et_note1.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                orig2=s.toString();

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);

        return super.onCreateOptionsMenu(menu);
    }
    ColorPickerDialogDash.OnColorSelectedListener colordashListner=new ColorPickerDialogDash.OnColorSelectedListener(){

        @Override
        public void onColorSelected(int color) {
            mSelectedColorDash1 = color;
            setActivityBackgroundColor(mSelectedColorDash1);
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
col=color;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putInt(filename+"CoLoR",color);
            editor.apply();

        }

    };


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
      int[] mColor = Utils.ColorUtils.colorChoice(this);
        //int [] mColor = new int[0];
       // mColor[0]=col;

        switch (item.getItemId()) {
            case R.id.action_save:
                SaveNote(true);
                return true;
            case R.id.action_delete:
                deleteNote();
                return true;
            case R.id.action_save2:
                SaveNote(true);
                return true;
            case R.id.action_del2:
                deleteNote();
                return true;
            case R.id.action_remind:
                remindNote1();
                return true;
            case R.id.action_share:
                shareNote();
                return true;
            case R.id.action_chngLayout:
                changeLayout();
                return true;
            case R.id.action_backup:
                saveToDrive();
                return true;

            case R.id.action_narrate:
                narrate_note();
                return true;

            case R.id.action_color:
                ColorPickerDialogDash colordashfragment = ColorPickerDialogDash
                        .newInstance(R.string.color_picker_default_title,mColor ,
                                mSelectedColorDash1, 5);

                // Implement listener to get selected color value
                colordashfragment.setOnColorSelectedListener(colordashListner);
                colordashfragment.show(getFragmentManager(), "dash");
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void remindNote1()
    {
        Intent i=new Intent(this,ReminderDescActivity.class);
        i.putExtra("filename",filename);
        i.putExtra("note_id",note_id);
        i.putExtra("note_type",1);
        i.putExtra("position",pos);
        if(note_type)
            i.putExtra("Content",et_note1.getText().toString());
        else
            i.putExtra("Content",et_note.getText().toString());

        startActivity(i);
    }

    public void setActivityBackgroundColor(int color) {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(color);
    }
    public void remindNote()
    {
        SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String akaron = sharedpreferences.getString(filename, "993");
        boolean isReminderSet= sharedpreferences.getBoolean(filename + "REMINDER#$@", false);
        String mnb = sharedpreferences.getString(filename + "DATE*&^", "");

        if(isReminderSet)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Override Reminder");
            alert.setMessage("The reminder is already set for " + mnb + ".\n Do you want to remove the reminder ? ");
                    alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int whichButton) {
//                Log.e("sd","FILE DEL IS "+f.getName());
                            AlarmManager alarmManager4 = (AlarmManager)getSystemService(ALARM_SERVICE);
                            Intent I = new Intent(getApplicationContext(),NotificationPublisher1.class);
                            PendingIntent P = PendingIntent.getBroadcast(getApplicationContext(), note_id, I, 0);
                          //  PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                            alarmManager4.cancel(P);
                            P.cancel();
                            DatePickerFragment dateDialog = new DatePickerFragment();
                            dateDialog.show(fm, "fragment_date");

                        }
                    }
                    );

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
//super.onBackPressed();


                }
            });

            alert.show();
        }
        else
        {
        if(!akaron.equals("993"))
        {
            DatePickerFragment dateDialog = new DatePickerFragment();
            dateDialog.show(fm, "fragment_date");
        }

        else
        {
            Toast.makeText(this,"Save the file to set the reminder",Toast.LENGTH_SHORT).show();
        }
    }}


    public void narrate_note()
    {
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
Log.e("fd","Narrate 1");
        note_type=sharedpreferences.getBoolean(filename+"LAYOUT#$@",false);

       Intent ttsIntent =new Intent(NoteActivity.this, SpeechService.class);
        ttsIntent.putExtra("SAY","Narration of Note Begins"+et_note.getText().toString());
        getApplicationContext().startService(ttsIntent);

        // this giving me buttload of probs.
       // AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        //int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        //am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
     /*   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            Log.e("fd","Narrate 2");
            engine.speak("Narration of note titled "+et_title.getText().toString()+"begins", TextToSpeech.QUEUE_ADD, null, null);
            engine.playSilentUtterance (750,TextToSpeech.QUEUE_ADD,null);
            if(note_type) {
                String[] array_of_sentences = et_note1.getText().toString().split("(?<=[.!?])\\s*");
                for (String s : array_of_sentences) {
                    //System.out.println(s);
                    engine.speak(s, TextToSpeech.QUEUE_ADD, null, null);
                    engine.playSilentUtterance (750,TextToSpeech.QUEUE_ADD,null);


                }
            }
else {
                String[] array_of_sentences = et_note.getText().toString().split("(?<=[.!?])\\s*");


                for (String s : array_of_sentences) {
                    //System.out.println(s);
                    engine.speak(s, TextToSpeech.QUEUE_ADD, null, null);
                    engine.playSilentUtterance (750,TextToSpeech.QUEUE_ADD,null);


                }
                Log.e("fd","Narrate 3");


            }
        }
        else
        {
            if(note_type) {
                String[] array_of_sentences = et_note1.getText().toString().split("(?<=[.!?])\\s*");


                for (String s : array_of_sentences) {
                    //System.out.println(s);
                    engine.speak(s, TextToSpeech.QUEUE_ADD, null);
                    engine.playSilence (750,TextToSpeech.QUEUE_ADD,null);


                }
            }
            else {

                String[] array_of_sentences = et_note.getText().toString().split("(?<=[.!?])\\s*");


                for (String s : array_of_sentences)
                {
                    //System.out.println(s);
                    engine.speak(s, TextToSpeech.QUEUE_ADD, null);
                    engine.playSilence (750,TextToSpeech.QUEUE_ADD,null);


                }
            }
        }*/
    }

    public void saveToDrive()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        note_type=sharedpreferences.getBoolean(filename+"LAYOUT#$@",false);

        alert.setTitle("Note Backup");
        alert.setMessage("Are you sure you want to back up the note on Google Drive?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(getApplicationContext(), CreateFolderActivity.class);
                i.putExtra("title", et_title.getText().toString());
                if(note_type)
                    i.putExtra("content", et_note1.getText().toString());
                          else
                i.putExtra("content", et_note.getText().toString());
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

    public void shareNote()
    {
        if(filename==null)
        {
            Toast.makeText(this,"Save the file before sharing",Toast.LENGTH_SHORT).show();
        return;
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        Log.e("TAG","The filename in SHARE is "+filename);
        File file_to_transfer = new File(Environment.getExternalStorageDirectory() +"/Todo/"+filename);
        if(!note_type)
            intent.putExtra(Intent.EXTRA_TEXT,et_note.getText().toString());
        else
            intent.putExtra(Intent.EXTRA_TEXT,et_note1.getText().toString());


        startActivity(Intent.createChooser(intent, "Share Note"));
    }
    public void changeLayout()
    {
        if(note_type==false)
        {
            String as=et_note.getText().toString();
        et_note1.setText(as);
        et_note.setVisibility(View.INVISIBLE);
        et_note1.setVisibility(View.VISIBLE);
            note_type=true;
        }
        else
        {
            String as=et_note1.getText().toString();
            et_note.setText(as);
            et_note1.setVisibility(View.INVISIBLE);
            et_note.setVisibility(View.VISIBLE);
            note_type=false;

        }
    }




    private void restoreSelectedColor(Bundle savedInstanceState){
        // Get selected colors
        if (savedInstanceState != null) {
            mSelectedColorDash0 = savedInstanceState.getInt(KEY_BUNDLE_SCD0);
            mSelectedColorDash1 = savedInstanceState.getInt(KEY_BUNDLE_SCD1);
            mSelectedColorCal0 = savedInstanceState.getInt(KEY_BUNDLE_SCC0);
            mSelectedColorCal1 = savedInstanceState.getInt(KEY_BUNDLE_SCC1);
            mLastPosition = savedInstanceState.getInt(KEY_BUNDLE_POSITION);
        }
    }

    private void _initMenu()
    {

        // Read preferences to get selected Color
        SharedPreferences shared = PreferenceManager
                .getDefaultSharedPreferences(this);
        if (shared != null) {
            mSelectedColorDash0 = shared.getInt("dash_colorkey", 0);
            mSelectedColorCal1 = shared.getInt("calendar_colorkey", 0);
        }



    }


    private void detailsNote()
    {
        if(filename!=null)
        {
            new NoteManager(NoteActivity.this).details(filename);
        }

    }

    private void SaveNote(boolean show_toast)
    {

        if(filename == null)
        {

            note_id+=1;
            int l2=et_title.getText().length();
            if(l2>0)
            {

                filename = et_title.getText().toString() + ".txt";
                if(!note_type) {
                    orig= et_note.getText().toString();

                  f=  new NoteManager(NoteActivity.this).SaveNote1(filename, et_note.getText().toString(), show_toast, f, filename1);
                    filename1=filename;
                    Main2.adapter.notifyDataSetChanged();


                }
                else
                {
                    orig= et_note1.getText().toString();

                    f=new NoteManager(NoteActivity.this).SaveNote1(filename, et_note1.getText().toString(), show_toast, f, filename1);
                    filename1=filename;
                    Main2.adapter.notifyDataSetChanged();


                }


                Main2.adapter.notifyDataSetChanged();
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
Log.e("fd","NOTE TYPE SAVED IS "+note_type);
                editor.putBoolean(filename+"LAYOUT#$@",note_type);
                editor.apply();
                Log.e(TAG, "two");
            }
            else
            {
                Log.e(TAG,"One");
                Toast.makeText(this, "Cannot save an untitled Note.", Toast.LENGTH_LONG).show();
            }



        }
        else
        {
            String sa=et_title.getText().toString();
            Log.e(TAG, "three");
            if(!sa.equals(filename))
            {
                Log.e(TAG, "four");
                sa=sa+".txt" ;
                Log.e(TAG, "Filename changed is " + filename);

                Log.e(TAG, "OLD NOTE ID " + note_id);
//                Log.e("sd", "FILE DEL IS " + f.getName());
                new NoteManager(NoteActivity.this).deleteNotea(filename, false, f);
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

              /*  SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove(filename);
                editor.commit();*/
                if(reminderSet==true)
                {
                    Intent myIntent = new Intent(this, NoteActivity.class);
                    myIntent.putExtra("filename", filename);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Log.e(TAG, "OLD REMINDER SET");
                    Notification.Builder builder = new Notification.Builder(this);
                    builder.setContentTitle(et_title.getText().toString());
                    if(!note_type)
                    builder.setContentText(et_note.getText().toString());
                    else
                        builder.setContentText(et_note1.getText().toString());

                    builder.setSmallIcon(R.drawable.utilities_notepad_icon);
                    builder.setContentIntent(pendingIntent);
                    Log.e(TAG, "OLD TITLE is " + et_title.getText().toString());

                    Intent notificationIntent = new Intent(this, NotificationPublisher1.class);
                    notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION_ID, note_id);
                    notificationIntent.putExtra("filename",filename);

                    notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION, builder.build());
                    notificationIntent.putExtra("filename1",et_title.getText().toString());

                //    notificationIntent.putExtra("FileName", (Parcelable)f);


                    //
                    PendingIntent pi = PendingIntent.getBroadcast(this,note_id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                }
              sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                Log.e("fd", "NOTE TYPE SAVED IS " + note_type);
                editor.putBoolean(filename + "LAYOUT#$@", note_type);


                editor.apply();
                if(!note_type) {
                    orig= et_note.getText().toString();
                   f= new NoteManager(NoteActivity.this).SaveNote1(sa, et_note.getText().toString(), show_toast, f, filename1);
                    filename1=sa;

                    Main2.adapter.notifyDataSetChanged();

                }
                else {
                    orig= et_note1.getText().toString();

                   f= new NoteManager(NoteActivity.this).SaveNote1(sa, et_note1.getText().toString(), show_toast, f, filename1);
                    filename1=sa;

                    Main2.adapter.notifyDataSetChanged();


                }


                Log.e(TAG, "OLD NOTE ID " + note_id);

                filename=sa;
            }
            else
            {
                if(reminderSet==true)
                { Intent myIntent = new Intent(this, NoteActivity.class);
                    myIntent.putExtra("filename", filename);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    Log.e(TAG, "OLD REMINDER SET");
                    Notification.Builder builder = new Notification.Builder(this);
                    builder.setContentTitle(et_title.getText().toString());
                    if(!note_type)
                    builder.setContentText(et_note.getText().toString());
                    else
                        builder.setContentText(et_note1.getText().toString());
                    builder.setSmallIcon(R.drawable.utilities_notepad_icon);
                    builder.setContentIntent(pendingIntent);
                    Log.e(TAG, "OLD TITLE is " + et_title.getText().toString());
                 //   Log.e(TAG, "OLD TEXT is " + et_note.getText().toString());
                    mNotificationManager.notify(note_id, builder.build());
                    Intent notificationIntent = new Intent(this, NotificationPublisher1.class);
                    notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION_ID, note_id);
                    notificationIntent.putExtra("filename",filename);
                   // notificationIntent.putExtra("FileName", (Parcelable) f);
                    notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION, builder.build());
                    PendingIntent pi = PendingIntent.getBroadcast(this,note_id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);


                }
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                Log.e("fd", "NOTE TYPE SAVED IS " + note_type);
                editor.putBoolean(filename + "LAYOUT#$@", note_type);


                editor.commit();
                if(!note_type) {
                    orig= et_note.getText().toString();

                   f= new NoteManager(NoteActivity.this).SaveNote1(filename, et_note.getText().toString(), show_toast, f, filename1);
                    filename1=filename;

                    Main2.adapter.notifyDataSetChanged();

                }
                else {
                    orig= et_note1.getText().toString();

                   f= new NoteManager(NoteActivity.this).SaveNote1(filename, et_note1.getText().toString(),show_toast,f,filename1);
                    filename1=filename;

                    Main2.adapter.notifyDataSetChanged();

                }


            }


        }
        Date date1=new Date();
        String cx1;
        SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yyyy HH:mm");
        cx1 = format1.format(date1);
        lastModified.setText("Last Modified - "+cx1);
        filename = et_title.getText().toString() + ".txt";


    }
  /*  private boolean isAnotherFile(String file1)
    {

        if(file1!=null)
        {
            return new NoteManager(Note.this).isSameName(file1);
        }
        return false;
    }*/

    private void deleteNote()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Delete Note");
        alert.setMessage("Are you sure you want to delete the note?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//                Log.e("sd","FILE DEL IS "+f.getName());

                new NoteManager(NoteActivity.this).deleteNotea(filename, true,f);
                AlarmManager alarmManager4 = (AlarmManager)getSystemService(ALARM_SERVICE);
                Intent I = new Intent(getApplicationContext(),NotificationPublisher1.class);
                I.putExtra(NotificationPublisher1.NOTIFICATION_ID, note_id);
                Log.e(TAG, "RED NOTE id is " + note_id);

//        int jid=ReminderListActivity.filenames.indexOf(f);

                I.putExtra(NotificationPublisher1.NOTIFICATION, getNotification(et_note.getText().toString()));
                I.putExtra("filename", filename);
                I.putExtra("filename1", et_title.getText().toString());
                PendingIntent P = PendingIntent.getBroadcast(getApplicationContext(),note_id, I,PendingIntent.FLAG_UPDATE_CURRENT);
                //  PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                P.cancel();
                alarmManager4.cancel(P);

                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.remove(filename);
                editor.apply();
                if(f!=null)
                finish();

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
//super.onBackPressed();


            }
        });

        alert.show();




    }
    private void sendSaveAlert()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save Note");
        alert.setMessage("Save the changes made in the note?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

             SaveNote(true);
               /* Intent goToMainActivity = new Intent(getApplicationContext(),Main2.class);
                goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Will clear out your activity history stack till now
                startActivity(goToMainActivity);*/
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
    public Calendar getCalendarObj()
    {
        return cal;
    }
    public void setTime(int hr,int min)
    {
        //cal.setTime(Calendar.HOUR_OF_DAY,hr);
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);
    }
    public void setDate(int year,int month,int day)
    {
        //cal.setTime(Calendar.HOUR_OF_DAY,hr);
        cal.set(Calendar.YEAR,year);
        cal.set(Calendar.MONTH,month);
        cal.set(Calendar.DAY_OF_WEEK, day);
    }
    public void showDatePickerDialog()
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private void scheduleNotification(Notification notification, long delay)
    {
        //Refreshed as it gave IndexOutOfBounds Exception if reminder set immediately after saving a new note
        Main2.adapter.notifyDataSetChanged();
        Intent notificationIntent = new Intent(this, NotificationPublisher1.class);
        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION_ID, note_id);
        Log.e(TAG, "RED NOTE id is " + note_id);

//        int jid=ReminderListActivity.filenames.indexOf(f);

        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION, notification);
        notificationIntent.putExtra("filename", filename);
        notificationIntent.putExtra("filename1", et_title.getText().toString());
        notificationIntent.putExtra(et_title.getText().toString()+"Content*&^",et_note.getText().toString());

//        notificationIntent.putExtra("RemID",jid);

        // notificationIntent.putExtra("FileName", (Parcelable) f);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, note_id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        Log.e(TAG, "RED futureInMillis is " + futureInMillis);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        Toast.makeText(this,"Reminder is set.",Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"Reminder is set.",Toast.LENGTH_SHORT).show();
        ListViewAdapter.filenames.get(pos).reminderSet(true);
        reminderSet=true;
    }

    public Notification getNotification(String content)
    {

        Intent myIntent = new Intent(this, NoteActivity.class);

        myIntent.putExtra("filename", filename);
        Log.e(TAG,"RED NOTIFICATION FILENAME IS "+filename);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent,0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(et_title.getText().toString());
        Log.e(TAG, "RED NOTIFICATION TITLE IS " + et_title.getText().toString());
        builder.setContentText(content);
        Log.e(TAG, "RED CONTENT IS " + content);
        builder.setContentIntent(pendingIntent);
       // builder.setDeleteIntent(createOnDismissedIntent(getApplicationContext(), note_id));


        builder.setSmallIcon(R.drawable.utilities_notepad_icon);
       Notification n=builder.build();
        n.tickerText=et_title.getText().toString();
        n.icon=R.drawable.utilities_notepad_icon;
return n;
    }
    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {

        Intent I = new Intent(getApplicationContext(),CancelNotifiRcvr.class);
        PendingIntent P = PendingIntent.getBroadcast(getApplicationContext(), note_id, I, 0);
        return P;
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
    public void onTimeSet(int hourOfDay, int minute)
    {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
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
        Log.e("TAG","DAY OF THE WEEK "+calendar.get(Calendar.DAY_OF_WEEK));
        Log.e("TAG","MONTH OF THE WEEK "+calendar.get(Calendar.MONTH));
int month=calendar.get(Calendar.MONTH)+1;
String ti[]={"AM","PM"};



      //  SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yyyy HH:mm");
    //   String date = format1.format(calender);


        String date=calendar.get(Calendar.DATE)+"/"+month+"/"+calendar.get(Calendar.YEAR)+" "+days[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "+calendar.get(Calendar.HOUR_OF_DAY)+":"+calendar.get(Calendar.MINUTE)+" "+ti[calendar.get(Calendar.AM_PM)];
        Log.e(TAG, "RED DIFFERENCE IN TIME IS " + differ);
       // Log.e(TAG, "Something in note is " + et_note.getText().toString());
        if(differ<0)
            Toast.makeText(getApplicationContext(),"Unable to set Reminder in the past.",Toast.LENGTH_SHORT).show();
        else
        {
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putBoolean(filename + "REMINDER#$@", true);
            Log.e("sda", "FILMN 1 is " + filename);

            editor.putString(filename + "DATE*&^", date);
            editor.apply();
       //     f=  new NoteManager(NoteActivity.this).SaveNote1(filename, et_note.getText().toString(),false, f, filename1);
            Main2.adapter.notifyDataSetChanged();

            if(!note_type)
                scheduleNotification(getNotification(et_note.getText().toString()), differ);
            else
                scheduleNotification(getNotification(et_note1.getText().toString()), differ);


        }
    }

    public void onBackPressed()
    {
        if(!orig.equals(orig2))
            sendSaveAlert();
        else
            super.onBackPressed();
    }


}




