package com.example.android.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todolist.geofence.GeofenceMainActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ReminderDescActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener
{
    public static SwitchCompat switchCompat;
    public static SwitchCompat repeatSwitch;
    public static SwitchCompat prioritySwitch;
     int noteType;
    long differ;
    int position;
    public static TextView time_ED;
    public static TextView date_ED;
    Calendar calendar;
    boolean switchCompatCond=false;
    private static final String TAG="ReminderDescActivity";
    CardView cardView;
    String filename;
    public static RemoteViews notificationView;
    String AlarmSongSet;
    FragmentManager fm;
    int note_id;String noteContent;
    boolean note_type=false;
    AlarmManager alarmManager;
    boolean reminderSet=false;
    PendingIntent pendingIntent;
    List<String> categories;
    Spinner spinner,remTypeSpinner;
   boolean recentSelected=false;
    Cursor returnCursor;
    int nameIndex;
    String tone_selected;
    private boolean hasSpinnerFirst = true;
    ArrayList<String> remTypes;
    EditText remValues;
    long rem_diff=0;
    int remType=0;
boolean bufferCond;// Just for not issuing the scheduleNotification  again after I set repeatSwitch as false
    boolean priorityCond=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_desc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        switchCompat= (SwitchCompat) findViewById(R.id.reminderSwitch);
        repeatSwitch=(SwitchCompat) findViewById(R.id.snoozeSwitch);
        prioritySwitch=(SwitchCompat) findViewById(R.id.priority_switch);
        bufferCond=false;
        remValues=(EditText)findViewById(R.id.minute_val);
        SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
    //    rem_diff=sharedpreferences.getLong(filename+"RESET*$#time",0);
        remValues.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == remValues.getId())
                {
                    remValues.setCursorVisible(true);
                }
            }
        });


prioritySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.priority_switch:
                SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                if(isChecked)
                {
                    priorityCond=isChecked;

                }
                else {
                    priorityCond=isChecked;

                }
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.putBoolean(filename+"priority#$@",priorityCond);
                editor.apply();
                break;
        }

    }
});
        switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                switch (buttonView.getId())
                {
                    case R.id.reminderSwitch:
                        if(isChecked)
                        {
                            SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            remValues.setCursorVisible(false);

                            boolean wasReminderSet=sharedpreferences.getBoolean(filename+"REMINDER#$@",false);
                            if(!switchCompatCond)
                            {
                                remindNote();
                            }
                        }
                        else
                        {
                            SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            boolean wasReminderSet=sharedpreferences.getBoolean(filename+"REMINDER#$@",false);
                            editor.remove(filename+"REMINDER#$@");
                            editor.apply();
                            if(wasReminderSet)
                            {
                                Toast.makeText(getApplicationContext(),"Reminder removed",Toast.LENGTH_SHORT).show();
                            }
                            time_ED.setText("");
                            switchCompatCond=false;
                            date_ED.setText("Tap to set Reminder");
                        }
                        break;

                }

            }
        }
        );

        repeatSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();

                long newTime=sharedpreferences.getLong(filename+"New*&@Time",0);  // New time should be set of calendar when repeat is on and sets new time after repeat interval
                if(newTime>0)
                    calendar.setTimeInMillis(newTime);
                switch (buttonView.getId())
                {

                    case R.id.snoozeSwitch:
                        if(isChecked)
                        {

                            Calendar c=Calendar.getInstance();
                            long cmillis=c.getTimeInMillis();
                            Log.e("TAG","Calendar millis are "+calendar.getTimeInMillis()+" and c millis are "+c.getTimeInMillis());
                          boolean isRemSet= sharedpreferences.getBoolean(filename + "REMINDER#$@", false);

                            editor.putInt(filename+"repeatFREq%^",Integer.parseInt(remValues.getText().toString()));
                            editor.putInt(filename+"repeatType%^",remType);
                            editor.commit();

                            Log.e("TAG","DIFFERENCE IS "+differ);
                            differ=(calendar.getTimeInMillis()-cmillis);
                            if(Integer.parseInt(remValues.getText().toString())<1)
                            {
                                Toast.makeText(getApplicationContext(),"Set value for the repeat interval",Toast.LENGTH_SHORT).show();
                                bufferCond=true;
                                Log.e("TAG","Trace 2");

                                repeatSwitch.setChecked(false);
                            }

                            else if(differ>0)
                            {

                                if(remType==0)
                                {
                                    rem_diff=60*1000*Integer.parseInt(remValues.getText().toString());
                                }
                                else if(remType==1)
                                {
                                    rem_diff=60*60*1000*Integer.parseInt(remValues.getText().toString());

                                }
                                else if(remType==2)
                                {
                                    rem_diff=24*60*60*1000*Integer.parseInt(remValues.getText().toString());

                                }
                                else if(remType==3)
                                {
                                    rem_diff=365*24*60*60*1000*Integer.parseInt(remValues.getText().toString());

                                }
                                Log.e("TAG","REMY 1 is "+rem_diff);
                                scheduleNotification(getNotification(noteContent), calendar, rem_diff);
                            }
                            else if(!isRemSet)
                            {
                                // if Alarm is not set first
                                Toast.makeText(getApplicationContext(),"Set alarm first",Toast.LENGTH_SHORT).show();
                                Log.e("TAG","Trace 3");

                                repeatSwitch.setChecked(false);



                            }
                            else if(alarmManager!=null)
                            {
                                alarmManager.cancel(pendingIntent);
                                pendingIntent.cancel();
                            }
                            }


                        else
                        {
                            Calendar c=Calendar.getInstance();
                            differ=(calendar.getTimeInMillis()-c.getTimeInMillis());
                            Log.e("TAG","Buffer cond val is  "+bufferCond);
                            if(differ>0 && !bufferCond)
                            {
                                Log.e("TAG","REMY 2 is "+rem_diff);

                                scheduleNotification(getNotification(noteContent), calendar, 0);
                            }
                            else if(alarmManager!=null)
                            {
                                alarmManager.cancel(pendingIntent);
                                pendingIntent.cancel();
                            }

                            bufferCond=false;


                        }
                        break;
                }
            }
        });


        remValues.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
 // repeatSwitch.setChecked(false);
    Log.e("TAG","Trace 4");

}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable s) {
           remValues.setCursorVisible(false);


                // TODO Auto-generated method stub
            }
        });
        time_ED=(TextView)findViewById(R.id.time);
        date_ED=(TextView)findViewById(R.id.date);
        cardView=(CardView)findViewById(R.id.card_view);
        fm = getSupportFragmentManager();
        calendar = Calendar.getInstance();
        Intent i=getIntent();
        filename=i.getStringExtra("filename");
        note_id=i.getIntExtra("note_id",-1);
        position=i.getIntExtra("position",-1);
        Log.e("TAG","position in REmindDesc is "+position);
        noteType=i.getIntExtra("note_type",1);
        noteContent=i.getStringExtra("Content");
        boolean isRemSet1= sharedpreferences.getBoolean(filename + "REMINDER#$@", false);
        int numberInremValues=sharedpreferences.getInt(filename+"repeatFREq%^",0);
        boolean repeatOn= sharedpreferences.getBoolean(filename + "REPEA*&%T", false);
        priorityCond=sharedpreferences.getBoolean(filename + "priority#$@", false);
        AlarmSongSet=sharedpreferences.getString(filename+"%&SET*tone","");
        String mnb = sharedpreferences.getString(filename + "DATE*&^", "");
        prioritySwitch.setChecked(priorityCond);

        Log.e("TAG","number inRemValues is "+numberInremValues+" and the filename is "+ filename);



        int x= sharedpreferences.getInt(filename+"repeatFREq%^",0);
        Log.e("TAG","THe freq is "+x);
        remValues.setText(String.valueOf(x));
        int type=sharedpreferences.getInt(filename+"repeatType%^",0);
        if(type!=0)
            remTypeSpinner.setSelection(type);

        boolean isRepeatOn=sharedpreferences.getBoolean(filename+"REPEA*&%T",false);




        if(!mnb.equals("") && isRemSet1)
        {

            date_ED.setText(mnb);
            String time_da=sharedpreferences.getString(filename + "TIME*&^", "");
            time_ED.setText(time_da);
            switchCompatCond=true;

            switchCompat.setChecked(true);
        }
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                remindNote();
            }
        });
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categories = new ArrayList<String>();
        if(noteType!=3) {
            categories.add("Vibration");
          //  if (AlarmSongSet.length() > 0)
            //    categories.add(AlarmSongSet);
            //else
                categories.add("Custom Sound");
            categories.add("Narrate");
        }
        else
        {
            categories.add("Recording");

        }


        remTypes = new ArrayList<String>();
        remTypes.add("MIN");
        remTypes.add("HOUR");
        remTypes.add("DAY");
        remTypes.add("YEAR");
        remTypes.add("Custom");



        spinner = (Spinner) findViewById(R.id.tone_types);
        remTypeSpinner=(Spinner)findViewById(R.id.rem_types);
// Create an ArrayAdapter using the string array and a default spinner layout




        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);


        int spinner_type=sharedpreferences.getInt(filename+"REMINDER#$@tone",-1);
        if(spinner_type!=-1)
        {
            hasSpinnerFirst=false;
            if(spinner_type==2) {
                hasSpinnerFirst=true;
                spinner.setSelection(0);
            }
else
            spinner.setSelection(spinner_type-1);
        }
        else
        {
            hasSpinnerFirst=true;

            spinner.setSelection(0);
        }



        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, remTypes);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        remTypeSpinner.setAdapter(adapter2);
           // remTypeSpinner.setSelection(4);


   /*     if(isRemSet1 && numberInremValues>0)
        {
            Log.e("TAG","Reminder SEET");
            if(numberInremValues>0)
                remValues.setText(String.valueOf(numberInremValues));
            repeatSwitch.setChecked(true);
            Log.e("TAG","REP ON COND IS "+repeatOn);

            Log.e("TAG","REM DIFF IS "+rem_diff);
        }*/



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                remValues.setCursorVisible(false);

                 //   repeatSwitch.setChecked(false);
                    Log.e("TAG","Trace 1");



                Log.e("TAG","SONG SELECTED IS "+hasSpinnerFirst);
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String item = parent.getItemAtPosition(position).toString();
                final String sName=tone_selected;
             /*   switch(item)
                {

                    case "Vibration":

                        editor.putInt(filename+"REMINDER#$@tone",1);
                        Log.e("TAG","ALarm Tone for the file is "+filename);
                        editor.commit();
                        break;
                   // case "Custom Sound":

                     //   break;
                    case "Narrate":
                        editor.putInt(filename+"REMINDER#$@tone",3);
                        editor.commit();
                        break;

                    default:
                     if(!recentSelected) {
                         Intent intent_upload1 = new Intent();
                         intent_upload1.setType("audio/*");
                         intent_upload1.setAction(Intent.ACTION_GET_CONTENT);
                         startActivityForResult(intent_upload1, 1);

                     }
                        else
                     {
                         recentSelected=false;
                     }
                }*/
if(hasSpinnerFirst)
{
    switch (position) {
        case 0:
            if(noteType!=3) {
                editor.putInt(filename + "REMINDER#$@tone", 1);

                Log.e("TAG", "ALarm Tone for the file is " + filename);
                editor.commit();
            }
            break;

        case 1:
if(item.equals("Custom Sound")) {
    Intent intent_upload1 = new Intent();
    intent_upload1.setType("audio/*");
    Log.e("TAG", "SELECTION AUDIO");
    intent_upload1.setAction(Intent.ACTION_GET_CONTENT);
    startActivityForResult(intent_upload1, 1);
}
            else
{
    editor.putInt(filename + "REMINDER#$@tone", 2);
    editor.commit();
}

            break;

        case 2:
            editor.putInt(filename + "REMINDER#$@tone", 3);
            editor.commit();
            break;
    }
}
                else
{
    hasSpinnerFirst=true;
}

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

               // SharedPreferences.Editor editor = sharedPreferences.edit();
                //editor.putInt(filename+"REMINDER#$@tone",1);

                //editor.commit();
            }
        });




        remTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                     @Override
                                                     public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                         SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                         SharedPreferences.Editor editor = sharedPreferences.edit();
                                                         switch(position)
                                                         {
                                                             case 0:
                                                                 editor.putInt(filename + "REMINDER#$@Freq", 0);
                                                                 editor.commit();
                                                                 Log.e("TAG","REMS 1 is "+rem_diff);
                                                                 remType=0; // to put in shared preferences for the min,hr type

                                                                 break;

                                                             case 1:
                                                                 editor.commit();
                                                                 Log.e("TAG","REMS 2 is "+rem_diff);

                                                                 remType=1;
                                                                 break;
                                                             case 2:
                                                                 editor.putInt(filename + "REMINDER#$@Freq", 2);
                                                                 editor.commit();
                                                                 Log.e("TAG","REMS 3 is "+rem_diff);

                                                                 remType=2;
                                                                 break;
                                                             case 3:
                                                                 editor.putInt(filename + "REMINDER#$@Freq", 3);
                                                                 editor.commit();
                                                                 remType=3;
                                                                 break;
                                                         }
                                                     }

                                                     @Override
                                                     public void onNothingSelected(AdapterView<?> parent) {

                                                     }
                                                 }
        );

        if(isRepeatOn)
            repeatSwitch.setChecked(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_remind_desc, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_reset:
              resetAdapter();
                return true;

            case R.id.action_loc_remind:
                Intent i=new Intent(this, GeofenceMainActivity.class);
                i.putExtra("LOC&^%",filename);
                i.putExtra("position",position);
                startActivity(i);
                return true;


            case R.id.action_loc_remind1:
                Intent i1=new Intent(this, GeofenceMainActivity.class);
                i1.putExtra("LOC&^%",filename);
                i1.putExtra("position",position);

                startActivity(i1);
                return true;





            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void resetAdapter()
    {
        categories.set(1,"Custom Sound");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        hasSpinnerFirst=true;

        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(filename+"%&SET*tone");
        editor.remove(filename+"custom4*$tone");
        editor.commit();
        Toast.makeText(getApplicationContext(),"Select a new  Alarm Tone",Toast.LENGTH_SHORT).show();
        spinner.setAdapter(adapter);

    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data){

        if(requestCode == 1)
        {

            if(resultCode == RESULT_OK){
                String filePath=null;

                //the selected audio.
                Uri uri = data.getData();
                 returnCursor = getContentResolver().query(uri, null, null, null, null);
                    nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                Log.e("TAG","Name Index is "+nameIndex);
                returnCursor.moveToFirst();
                 categories.set(1,returnCursor.getString(nameIndex));
                 tone_selected=returnCursor.getString(nameIndex);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
                spinner.setAdapter(adapter);
                spinner.setSelection(1);
                hasSpinnerFirst=false;
                //  String path = _getRealPathFromURI(getApplicationContext(),uri);
               Log.e("TAG","The mp3 filename is "+returnCursor.getString(nameIndex));
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt(filename+"REMINDER#$@tone",2);
                editor.putString(filename+"%&SET*tone",returnCursor.getString(nameIndex));
                editor.putString(filename+"custom4*$tone",uri.toString());
                Log.e("TAG","ALarm Tone for the file is "+filename);
                recentSelected=true;
                editor.commit();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }






    public void remindNote()
    {
        SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String akaron = sharedpreferences.getString(filename, "993");
        boolean isReminderSet= sharedpreferences.getBoolean(filename + "REMINDER#$@", false);
        String mnb = sharedpreferences.getString(filename + "DATE*&^", "");
        String mnb2 = sharedpreferences.getString(filename + "TIME*&^", "");


        if(isReminderSet)
        {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Override Reminder");
            alert.setMessage("The reminder is already set for " + mnb+" "+mnb2 + ".\nDo you want to remove the reminder ? ");
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


        String date=calendar.get(Calendar.DATE)+"/"+month+"/"+calendar.get(Calendar.YEAR)+" "+days[calendar.get(Calendar.DAY_OF_WEEK)-1];
        String timeMag=calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+" "+ti[calendar.get(Calendar.AM_PM)];

        Log.e(TAG, "RED DIFFERENCE IN TIME IS " + differ);
        // Log.e(TAG, "Something in note is " + et_note.getText().toString());
        if(differ<0)
            Toast.makeText(getApplicationContext(),"Unable to set Reminder in the past.",Toast.LENGTH_SHORT).show();
        else
        {
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            date_ED.setText(date);
            time_ED.setText(timeMag);
            switchCompatCond=true;
            switchCompat.setChecked(true);
            editor.putBoolean(filename + "REMINDER#$@", true);
            Log.e("sda", "FILMN 1 is " + filename);
            switchCompatCond=true;
            editor.putString(filename + "DATE*&^", date);
            editor.putString(filename+"TIME*&^",timeMag);
            editor.commit();
            //     f=  new NoteManager(NoteActivity.this).SaveNote1(filename, et_note.getText().toString(),false, f, filename1);
            Main2.adapter.notifyDataSetChanged();

            Log.e("TAG","REMY 3 is "+rem_diff);
if(noteType!=3)
                scheduleNotification(getNotification(noteContent), calendar,rem_diff);
            else
               scheduleNotification(getNotification(noteContent), calendar,rem_diff);


        }
    }


    public Notification getRecordingNotification(String content)
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
       builder.setContentTitle(filename);
        //Log.e(TAG, "RED NOTIFICATION TITLE IS " + tv.getText().toString());
        builder.setContentText("Sound Recording");
        Log.e(TAG, "RED CONTENT IS " + content);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.microphone);
        builder.setDeleteIntent(pendingIntent1);
        notificationView = new RemoteViews(getPackageName(), R.layout.notification_buttons);
        notificationView.setTextViewText(R.id.rec_name, filename);
        notificationView.setImageViewResource(R.id.pause_notif,R.drawable.ic_pause_white_36dp);
        Notification n= builder.build();

        n.tickerText=filename;
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

    private void scheduleNotification(Notification notification, Calendar setObj,long reset)
    {
        //Refreshed as it gave IndexOutOfBounds Exception if reminder set immediately after saving a new note
        Main2.adapter.notifyDataSetChanged();
        Intent notificationIntent = new Intent(this, NotificationPublisher1.class);
        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION_ID, note_id);
        Log.e(TAG, "RED NOTE id is " + note_id);

//        int jid=ReminderListActivity.filenames.indexOf(f);

        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION, notification);
        notificationIntent.putExtra("filename", filename);


        notificationIntent.putExtra("filename1",filename.substring(0, filename.length() - 4));
        notificationIntent.putExtra(filename.substring(0, filename.length() - 4)+"Type*@", noteType);
        notificationIntent.putExtra(filename.substring(0, filename.length() - 4)+"position%$", position);
     //   if(noteType==3)
       //     notificationIntent.putExtra("path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDo/" + filename);


//        notificationIntent.putExtra("RemID",jid);

        // notificationIntent.putExtra("FileName", (Parcelable) f);
           Calendar c=Calendar.getInstance();
        pendingIntent = PendingIntent.getBroadcast(this, note_id, notificationIntent,0);
       // long futureInMillis = SystemClock.elapsedRealtime() + delay;

    //    Log.e(TAG, "RED futureInMillis is " + futureInMillis);
        Log.e(TAG, "RED reset is " + reset);

        alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.ELAPSED_REALTIME, futureInMillis, pendingIntent);
        SharedPreferences sharedpreferences =getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        if(reset>0)
        {
Log.e("TAG","The reset value is greater and val is "+reset);
            editor.putBoolean(filename+"REPEA*&%T",true);
            Log.e("RE","RESET TIME IS "+reset);
            editor.putLong(filename+"RESET*$#time",reset);

            editor.commit();
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setObj.getTimeInMillis(), reset, pendingIntent);
        }
        else
        {
            Log.e("TAG","The reset value is lesser and val is "+reset);

            editor.remove(filename+"REPEA*&%T");

            editor.remove(filename+"RESET*$#time");
            editor.commit();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,setObj.getTimeInMillis(), pendingIntent);
            }
            else
            {
                alarmManager.set(AlarmManager.RTC_WAKEUP,setObj.getTimeInMillis(), pendingIntent);

            }

        }

boolean cond=sharedpreferences.getBoolean(filename+"REPEA*&%T",false);
        Log.e("TAG","The reset value REP COND is "+cond);

       /* if(reset>0) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis, reset, pendingIntent);
            Log.e("TAG","SNOOZED");
        }
        else
        {


        }*/
       // alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, futureInMillis,3000,pendingIntent);

        Toast.makeText(this,"Reminder is set.",Toast.LENGTH_SHORT).show();
        switchCompatCond=true;

        //  Toast.makeText(this,"Reminder is set.",Toast.LENGTH_SHORT).show();
        ListViewAdapter.filenames.get(position).reminderSet(true);
        reminderSet=true;
    }
    public Notification getNotification(String content)
    {
        Intent myIntent = null;
        if(noteType==1)
        myIntent= new Intent(this, NoteActivity.class);
        else if(noteType==2)
            myIntent= new Intent(this, MainActivity.class);
           else
            myIntent= new Intent(this, PlayerActivity.class);
        if(noteType==3)

        {
            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(filename+"REMINDER#$@tone",2);

            editor.putString(filename+"%&SET*tone", Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDo/" + filename);
          //  editor.putString(filename+"custom4*$tone",uri.toString());
            editor.putString(filename+"custom4*$tone", Environment.getExternalStorageDirectory().getAbsolutePath() + "/ToDo/" + filename);

            editor.apply();
        }


        myIntent.putExtra("filename", filename);
        myIntent.putExtra("position",position);
        Log.e(TAG,"RED NOTIFICATION FILENAME IS "+filename);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent,0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(filename.substring(0, filename.length() - 4));
        //Log.e(TAG, "RED NOTIFICATION TITLE IS " + et_title.getText().toString());
        builder.setContentText(content);
        Log.e(TAG, "RED CONTENT IS " + content);
        builder.setContentIntent(pendingIntent);

        builder.setSmallIcon(R.drawable.utilities_notepad_icon);


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

