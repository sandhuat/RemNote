package com.example.android.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
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
import android.text.method.KeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements DatePickerFragment.DatePickerListener, TimePickerFragment.TimePickerListener
{
    private ListView myList;
    long differ;
    private MyAdapter myAdapter;
    ArrayList<String> orig,orig2;
    ArrayList<String> checkBoxArr2;
    private String filename = null;
    static int instanceCounter = 0;
    private EditText et_note;
    String first_item="";
    FragmentManager fm;
    String combine="";
Calendar calendar;
    private EditText et_title;
    public ArrayList myItems = new ArrayList<ListItem>();
    private NoteManager nm;
    private Bundle extras;     FileName f;
    TextView todo_date;
    int pos;
    int note_id = 0;String old_title="";
    private ArrayList<String> items;
    Calendar cal;
    private ArrayList<String> isChecked1;
    ImageButton del1;
    ImageButton adda;
    String name_init;
    private static final String TAG="fsd";
    TextView lastModified;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instanceCounter++;
        del1 = (ImageButton) findViewById(R.id.del_note);
        todo_date=(TextView)findViewById(R.id.date23);
        myList = (ListView) findViewById(R.id.MyList);
        View footerView =  ((LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.footer_add, null, false);
        myList.addFooterView(footerView);
        adda = (ImageButton) footerView.findViewById(R.id.add_btn);
        et_title=(EditText)findViewById(R.id.title);
        note_id=instanceCounter;
        extras = getIntent().getExtras();
        name_init=et_title.getText().toString();
        calendar = Calendar.getInstance();
        fm = getSupportFragmentManager();
        nm=new NoteManager(MainActivity.this);
        myList.setItemsCanFocus(true);
        orig=new ArrayList<String>();
        orig2=new ArrayList<String>();
        isChecked1=new ArrayList<String>();
        checkBoxArr2=new ArrayList<String>();
        lastModified=(TextView)findViewById(R.id.date23);

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
            pos=extras.getInt("position");
          f=Main2.adapter.filenames.get(pos);
            long d=f.getLongDate();
            Date date=new Date(d);
            String cx;
            SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yyyy HH:mm");
            cx = format1.format(date);
            todo_date.setText("Last Modified - " + cx);
            int len=filename.length();
            nm = new NoteManager(MainActivity.this);
            //String text = nm.readNote(filename);
            readItems(filename);
            if(items.size()>0) {
                first_item = items.get(0);
                for(int i=0;i<items.size();i++)
                {
                    orig.add(items.get(i));
                    orig2.add(items.get(i));
                }
            }

            et_title.setText(filename.substring(0, len - 4));
            old_title=filename.substring(0, len - 4);
        }


        if(items==null)
        {
            items = new ArrayList<String>();
            /* ListItem listItem = new ListItem();
            listItem.caption = "";
            listItem.checkn=false;
            myItems.add(listItem.caption);*/
        }
        else {
            SharedPreferences prefs = this.getSharedPreferences(filename, Context.MODE_PRIVATE);
           /* try {
                Set<String> set = prefs.getStringSet(filename, null);
                Log.e(TAG,"isChecked SET RETRIEVED "+set.size());
                isChecked1 = new ArrayList<String>();
                for (String str : set)
                    isChecked1.add(str);
                Log.e(TAG, "LUCY ITEMS SIZE3  - " + isChecked1.size());

                Log.e(TAG,"LUCY ITEMS GOT "+set.size());
            } catch (NullPointerException e) {
                Log.e(TAG, "No Worries");
            }*/
            isChecked1 = getStringArrayPref(this,filename.substring(0,filename.length()-4));
            for(int i=0;i<isChecked1.size();i++)
            {
                checkBoxArr2.add(i,isChecked1.get(i));
            }
            Log.e(TAG,"FER RETRIEVED "+isChecked1.size());
            Log.e(TAG,"FER RETRIEVED "+isChecked1);

        }
        Log.e(TAG,"LUCY ITEMS SIZE2  - "+items.size());
        myAdapter = new MyAdapter(items);

        myList.setAdapter(myAdapter);
        adda.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Log.e(TAG,"ITEMSS before added - "+items);
           /* items.add("");
                Log.e(TAG, "ITEMSS after added - " + items);
                isChecked1.add("false");
                myAdapter.notifyDataSetChanged();*/

                myAdapter.addp();
            }
        });

    }

    public static void setStringArrayPref(Context context, String key, ArrayList<String> values)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        JSONArray a = new JSONArray();
        for (int i = 0; i < values.size(); i++) {
            a.put(values.get(i));
        }
        if (!values.isEmpty())
        {
            editor.putString(key, a.toString());
        }
        else
        {
            editor.putString(key, null);
        }
        editor.commit();
    }

    public static ArrayList<String> getStringArrayPref(Context context, String key) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String json = prefs.getString(key, null);
        ArrayList<String> urls = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String url = a.optString(i);
                    urls.add(url);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return urls;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_todo, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_save:
                SaveNote();
                return true;
            case R.id.action_delete:
                deleteNote();
                return true;
            case R.id.action_save2:
                SaveNote();
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

            case R.id.action_narrate:
                narrateList();
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

    public void remindNote1()
    {
        Intent i=new Intent(this,ReminderDescActivity.class);
        i.putExtra("filename",filename);
        i.putExtra("note_id",note_id);
        i.putExtra("note_type",2);
        i.putExtra("position",pos);
        if(orig2.get(0).length()>0)
        i.putExtra("Content",orig2.get(0).toString());
        else
            i.putExtra("Content","");


        startActivity(i);
    }
    public void narrateList()
    {
        SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // this giving me buttload of probs.
       /* AudioManager am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);*/
        String word="Narration of List begins";

        for(int k=0;k<orig2.size();k++)
        {
            int count=k+1;
          word+="Item "+count+" is ";
            word+=orig2.get(k);

        }
        Intent ttsIntent =new Intent(MainActivity.this, SpeechService.class);
        ttsIntent.putExtra("SAY",word+".List ends");
        getApplicationContext().startService(ttsIntent);
      /*  if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {

         /*   for(int k=0;k<orig2.size();k++)
            {
                engine.playSilentUtterance (1000,TextToSpeech.QUEUE_ADD,null);
                int count=k+1;
                String word="Item "+count+" is ";
                word+=orig2.get(k);
                engine.speak(word, TextToSpeech.QUEUE_ADD, null,null);

            }
            engine.playSilentUtterance (1000,TextToSpeech.QUEUE_ADD,null);

            engine.speak("Reached the end of the list.", TextToSpeech.QUEUE_ADD, null,null);


        }
        else
        {

            for(int k=0;k<orig.size();k++)
            {
                engine.playSilence (1000,TextToSpeech.QUEUE_ADD,null);
                int count=k+1;
                String word="Item "+count+" is ";
                word+=orig.get(k);
                engine.speak(word, TextToSpeech.QUEUE_ADD, null);

            }*/

        //}
    }
    public void saveToDrive()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("List Backup");
        alert.setMessage("Are you sure you want to back up the list on Google Drive?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton) {
                Intent i = new Intent(getApplicationContext(), CreateFolderActivity.class);
                i.putExtra("title", et_title.getText().toString());
                int k=0;
                for(k=0;k<orig.size();k++)
                {
                    combine+="\n";
                    combine+=orig.get(k);
                }
                i.putExtra("content",combine);

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
    private void sendSaveAlert()
    {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Save Note");
        alert.setMessage("Save the changes made in the list?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
Log.e("TAG","TR1");
                SaveNote();
                boolean cond=orig.equals(orig2);

                Log.e("TAG","CHECKING CONDITION IS "+cond);
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
        Log.e("TAG", "The filename in SHARE is " + filename);
        File file_to_transfer = new File(Environment.getExternalStorageDirectory() +"/Todo/"+filename);
        String text="";
        for(int i=0;i<items.size();i++)
        {
            int k=i+1;
            text+="\n"+k+". ";
            text+=items.get(i);

        }
        intent.putExtra(Intent.EXTRA_TEXT, filename.substring(0, filename.length() - 4) + " - " + text);
        startActivity(Intent.createChooser(intent, "Share ToDo List"));
    }

    private void deleteNote()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Delete Note");
        alert.setMessage("Are you sure you want to delete the note?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                new NoteManager(MainActivity.this).deleteNotea(filename, true,f);
                finish();
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();




    }
    private void SaveNote()
    {
        Date date1=new Date();
        String cx1;
        SimpleDateFormat format1 = new SimpleDateFormat("dd MMM yyyy HH:mm");
        cx1 = format1.format(date1);

        if (filename == null)
        {
            Log.e(TAG, "JINGA");
            note_id += 1;
            int l2 = et_title.getText().length();
Log.e("TAG","TR 2");
            if (l2 > 0)
            {
                Log.e(TAG, "JINGA 1.1");
                filename = et_title.getText().toString() + ".txt";
                setStringArrayPref(getApplicationContext(), filename.substring(0, filename.length() - 4), isChecked1);
//                Log.e("TAG", "THE FIRSST ITEM IS " + myItems.get(0));
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                Log.e("TAG","TR 3");
                try {
                    if (isChecked1.get(0).equals("true") && !myItems.get(0).equals("")) {

                        editor.putBoolean(filename + "CHECKED#$@", true);

                    }
                    else
                    {
                        editor.putBoolean(filename + "CHECKED#$@", false);
                    }
                    editor.commit();
                }
                catch(IndexOutOfBoundsException e)
                {
                    Log.e("TAG","EXCEPTION HANDLED");//exception comes up when myItems has no elements i.e., there is no list item
                }
                if(myItems.size()!=0)
                {
                    new NoteManager(MainActivity.this).SaveNote(filename, myItems,f,old_title);
                    lastModified.setText("Last Modified - "+cx1);
                    orig.clear();

                    for(int i=0;i<orig2.size();i++)
                    {
                        orig.add(i,orig2.get(i));
                    }
                    Log.e("TAG","TR 4");


                }
                else
                    Toast.makeText(getApplicationContext(),"Add an item to save the list",Toast.LENGTH_SHORT).show();

                /*if(Main2.filenames.contains(filename))
                {
Log.e(TAG, "FREAKING CONTAINS ");

                }*/

            }
            else
            {
                Log.e(TAG, "JINGA 1.2");
                Log.e("TAG","TR 5");

                Toast.makeText(this, "Cannot save an untitled List.", Toast.LENGTH_LONG).show();
            }
        }

        else
        {
            String sa=et_title.getText().toString();
            Log.e(TAG,"JINGA2");
            Log.e("TAG","TR 6.1 sa is "+sa);
            Log.e("TAG","TR 6.1 filename is "+filename);
            sa=sa+".txt";
            if(!sa.equals(filename))
            {
                Log.e("TAG","TR 6");


                new NoteManager(MainActivity.this).deleteNotea(filename, false,f);

                Log.e(TAG, "JINGA 2.1");
                setStringArrayPref(this, sa.substring(0, sa.length() - 4), isChecked1);
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                try {
                    if (isChecked1.get(0).equals("true") && !myItems.get(0).equals("")) {

                        editor.putBoolean(filename + "CHECKED#$@", true);

                    } else {
                        editor.putBoolean(filename + "CHECKED#$@", false);
                    }
                }
                catch(IndexOutOfBoundsException e)
                {
                    Log.e(TAG,"Exception handled!!!");
                }

                editor.commit();
                if(myItems.size()!=0)
                {
                    new NoteManager(MainActivity.this).SaveNote(sa, myItems, f, old_title);
                    lastModified.setText("Last Modified - "+cx1);
                    orig.clear();
                    for(int i=0;i<orig2.size();i++)
                    {
                        orig.add(i,orig2.get(i));
                    }

                }
                    else
                Toast.makeText(getApplicationContext(),"Add an item to save the list",Toast.LENGTH_SHORT).show();

                filename=sa;
            }
            else
            {
                Log.e(TAG, "JINGA 2.2");
                Log.e("TAG","TR 7");

                //Log.e(TAG,"File going in preferences "+filename.substring(0,filename.length() - 4));
                setStringArrayPref(this, filename.substring(0, filename.length() - 4), isChecked1);
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedpreferences.edit();
                try {
                    if (isChecked1.get(0).equals("true") && !myItems.get(0).equals("")) {

                        editor.putBoolean(filename + "CHECKED#$@", true);

                    } else {
                        editor.putBoolean(filename + "CHECKED#$@", false);
                    }
                }
                catch(IndexOutOfBoundsException e)
                {
                    Log.e(TAG,"Exception handled!!!");
                }
                editor.commit();
                Log.e("TAG","TR 8");

                if(myItems.size()!=0)
                {
                    new NoteManager(MainActivity.this).SaveNote(filename, myItems, f, old_title);
                    lastModified.setText("Last Modified - "+cx1);
                    orig.clear();
                    for(int i=0;i<orig2.size();i++)
                    {
                        orig.add(i,orig2.get(i));
                        Log.e("TAG","TR 9");

                    }
                    boolean cond=orig.equals(orig2);
                    Log.e("TAG","TR 10 - The arrays are equal - "+cond);

                }
                else
                    Toast.makeText(getApplicationContext(),"Enter atleast one list item to save the list",Toast.LENGTH_SHORT).show();

            }


        }
       /* SharedPreferences prefs=this.getSharedPreferences(filename,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=prefs.edit();

        Set<String> set = new HashSet<String>();
        Log.e(TAG,"isChecked SIZED BEFORE "+isChecked1.size());
        Log.e(TAG,"isChecked DATA "+isChecked1);

        set.addAll(isChecked1);
        Log.e(TAG, "isChecked SET SAVED " + set);
        Log.e(TAG,"isChecked SET SAVED "+set.size());

        if(isChecked1.size()>1)
        Log.e(TAG, "LUCY CONDITION " + isChecked1.get(0) +" and "+isChecked1.get(1));
        edit.putStringSet(filename, set);
        edit.commit();*/


    }

    boolean compareLists(ArrayList<String> a, ArrayList<String> b)
    {
        if(a.size()!=b.size() || (a.size()==0 && b.size()!=0) || (b.size()==0 && a.size()!=0))
        {
            return false;
        }
        else
        {
            for(int i=0;i<a.size();i++)
            {
                if(!a.get(i).equals(b.get(i)))
                    return false;

            }
        }
        return true;
    }
    private void readItems(String filename)
    {

        Log.e(TAG,"LUCY ITEMS FILE NAME - "+filename);
        File todoFile = new File(Environment.getExternalStorageDirectory().toString()+"/Todo/",filename);
        try {
            Log.e(TAG,"LUCY ITEMS IN THE FILE ARE - "+FileUtils.readLines(todoFile));
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
            Log.e(TAG,"LUCY ITEMS SIZE  - "+items.size());
        }
        catch (IOException e)
        {
            items = new ArrayList<String>();
        }
    }
    private void save()
    {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Overwrite Note");
        alert.setMessage("Are you sure you want to overwrite the note?");
        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Canceled.
            }
        });
        alert.show();
    }

    private void writeItems(ArrayList<String> myItems,String filename) {

        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try {
            FileUtils.writeLines(todoFile, myItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public class MyAdapter extends BaseAdapter {
        private LayoutInflater mInflater;



        public MyAdapter(ArrayList<String> items)  {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            myItems=items;
            notifyDataSetChanged();
        }

        public void addp()
        {

            isChecked1.add("false");
            myItems.add("");
            orig2.add("");
            notifyDataSetChanged();
        }


        public int getCount()
        {
            return myItems.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }
        public void removeItem(int position) {
            //(myItems.getItem(position)).setCaption("");
            myItems.remove(position);
            Log.e(TAG, "Before REMOVAL, checkbox is " + isChecked1);
            isChecked1.remove(position);
            Log.e(TAG, "After REMOVAL, checkbox is " + isChecked1);
            notifyDataSetChanged();
            // writeItems(myItems,filename);
        }


        public View getView(final int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder = null;
            View v;

            Log.e(TAG, "isCheckednm position in parameter " + position);
            if (convertView == null )
            {
                v=newView(parent,position);
                Log.e(TAG, "isCheckednm position when NULL " + position);


            }
            else
            {
                // If position and id of set do not match, this view needs to be re-created, not recycled
                if (((ViewHolder) convertView.getTag()).getId() != position)
                {
                    //convertView.get
                    v = newView(parent, position);
                }
                else
                {
                    // Use previous item if not null
                    v = convertView;

                    // Get holderholder = (ViewHolder) v.getTag();
                }
            }



            if ((holder == null) || (holder.readPopulateFlag()))
            {
                bindView(position, v);
            }
            //writeItems(myItems,filename);
            return v;
        }


        private View newView(ViewGroup parent, int position)
        {
            // Getting view somehow...
            LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View inflatedView = inflater.inflate(R.layout.item, parent, false);
            ViewHolder holder = new ViewHolder();
            holder.del12 = (ImageButton) inflatedView.findViewById(R.id.del_note);
            holder.caption = (EditText) inflatedView.findViewById(R.id.ItemCaption);
            Log.e(TAG, "isChecked checkbox value is " + isChecked1.get(position));
            holder.mCheckBox=(CheckBox)inflatedView.findViewById(R.id.checkBox1);
            inflatedView.setTag(holder);
            return inflatedView;
        }
        private void bindView(final int position, View inflatedView) {

            final ViewHolder holder = (ViewHolder) inflatedView.getTag();
            holder.setId(position);
            final int[] count_edittext = {0};
            holder.del12.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                        /*View parentRow = (View) v.getParent();
                        ListView listView = (ListView) parentRow.getParent();
                        final int position1 = listView.getPositionForView(parentRow);*/
                    int position1 = (Integer) v.getTag();
                    myAdapter.removeItem(position1);


                    Log.e(TAG, "REMOVED POSITION is " + position1);

                }

            });

            holder.del12.setTag(position);
            holder.del12.setId(position);

            //Fill EditText with the value you have in data source
            Log.e(TAG, "The DATA IN EDITTEXT IS " + myItems.get(position).toString());


            holder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    holder.rePopulateFlag = true;
                    try {
                        if (isChecked)
                        {

                            holder.caption.setHint("Uncheck to enter data");
                            Log.e(TAG, "isChecked before listen" + isChecked1 + " and the position is " + position);
                            isChecked1.set(position, "true");
                            Log.e(TAG, "isChecked after listen" + isChecked1);

                            if(count_edittext[0] >0 || holder.caption.getText().length()>0)
                            {
                               Log.e("fsd","STRIKE 1");
                                    holder.caption.setPaintFlags(holder.caption.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                                    holder.caption.setKeyListener(null);

                            }
                            else if(count_edittext[0] ==0 )
                            {

                                 holder.caption.setPaintFlags(holder.caption.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                               // holder.caption.setKeyListener((KeyListener) holder.caption.getTag());
                                holder.caption.setKeyListener(null);
                            }
                        }
                        else
                        {
                            holder.caption.setHint("Enter Data");
                            isChecked1.set(position, "false");
                            if(count_edittext[0] >0)
                            {
                                Log.e("fsd", "STRIKE 2");
                                holder.caption.setPaintFlags(holder.caption.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                holder.caption.setKeyListener(null);
                            }else if(count_edittext[0] ==0) {
                                holder.caption.setPaintFlags(holder.caption.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                                holder.caption.setKeyListener((KeyListener) holder.caption.getTag());
                            } }
                    }
                    catch(IndexOutOfBoundsException e)
                    {
                        Log.e(TAG, "Resolved");
                    }

                }
            });
            // holder.caption.setText("");
            holder.caption.setId(position);
            holder.caption.setText(myItems.get(position).toString());
            holder.caption.setTag(holder.caption.getKeyListener());

            //we need to update adapter once we finish with editing
            holder.caption.setOnFocusChangeListener(new OnFocusChangeListener() {
                public void onFocusChange(View v, boolean hasFocus)
                {
                    if (!hasFocus)
                    {
                        holder.del12.setVisibility(View.VISIBLE);
                        int position = v.getId();
                        EditText Caption = (EditText) v;
                        Log.e(TAG,"CAPPTION EDITTEXT IS "+Caption.getText());

                        //myItems.get(position).caption = Caption.getText().toString();
                        try {
                            myItems.set(position, Caption.getText().toString());
                        } catch (ArrayIndexOutOfBoundsException e) {
                            Log.e(TAG, "Handled");
                        } catch (IndexOutOfBoundsException e) {
                            Log.e(TAG, "Handled");
                        }

                    }
                    else
                    {
                        holder.del12.setVisibility(View.INVISIBLE);
                        Log.e("SDA","HAVING THE FOCUSE is "+holder.del12.getId());
                    }
                }
            }
            );
      holder.caption.addTextChangedListener(new TextWatcher() {
          @Override
          public void afterTextChanged(Editable mEdit) {
              Log.e("SDA", "HAVING THE FOCUSE2 is " + holder.del12.getId());

              Log.e("SDA", "JAKEN2");
              int position = holder.getId();
              if(holder.caption.hasFocus())
              holder.del12.setVisibility(View.INVISIBLE);
              orig2.set(position, mEdit.toString());
              Log.e(TAG,"The position in orig is "+position);
              try
              {
                  myItems.set(position, mEdit.toString());
              } catch (ArrayIndexOutOfBoundsException e) {
                  Log.e(TAG, "Handled");
              } catch (IndexOutOfBoundsException e) {
                  Log.e(TAG, "Handled");
              }
          }

          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          public void onTextChanged(CharSequence s, int start, int before, int count) {
             // holder.del12.setVisibility(View.INVISIBLE);
              count_edittext[0] =count;

          }
      });

            try
            {
                Log.e(TAG,"THE POSITIONS ARE "+position);
                if (isChecked1.get(position).equals("true"))
                {
                    Log.e(TAG, "WEND POSITION IS " + position);
                    holder.mCheckBox.setChecked(true);
                    Log.e(TAG, "LUCY CONDI TRUE " + position);
                    if(count_edittext[0] >0 || holder.caption.getText().length()>0) {
                        holder.caption.setPaintFlags(holder.caption.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.caption.setKeyListener(null);
                    }
                    else if(count_edittext[0] ==0) {
                        holder.caption.setPaintFlags(holder.caption.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        holder.caption.setKeyListener((KeyListener) holder.caption.getTag());
                    }
                }
                else
                {

                    holder.mCheckBox.setChecked(false);
                    //holder.caption.setPaintFlags(holder.caption.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                    Log.e(TAG, "LUCY CONDI FALSE " + isChecked1);
                    Log.e(TAG, "LUCY CONDI FALSE " + position);
                }

            }
            catch(IndexOutOfBoundsException e)
            {
                Log.e(TAG,"Resolved");
            }




        }


    }

    class ViewHolder {
        EditText caption;
        ImageButton del12;
        CheckBox mCheckBox;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        int id;

        boolean rePopulateFlag;
        public CheckBox getCheckBox() {
            return mCheckBox;
        }
        public void setCheckBox(CheckBox a)
        {
            mCheckBox=a;
        }
        public boolean readPopulateFlag()
        {
            return rePopulateFlag;
        }
    }

    class ListItem {
        String caption;
        boolean checkn;
        public String getCaption()
        {
            return caption;
        }
    }





    public void onBackPressed()
    {
        Log.e("TAG","IN back");
        for(int i=0;i<checkBoxArr2.size();i++)
        {
            Log.e("TAG"," orig "+i+" is "+checkBoxArr2.get(i));
        }
        for(int i=0;i<isChecked1.size();i++)
        {
            Log.e("TAG"," orig-2 "+i+" is "+isChecked1.get(i));
        }
        Log.e("TAG","EQUAL or NOT - "+compareLists(checkBoxArr2,isChecked1));
        if(filename!=null)
            setStringArrayPref(getApplicationContext(),  filename.substring(0, filename.length() - 4), isChecked1);

        if(!compareLists(orig,orig2))
            sendSaveAlert();
        else
            super.onBackPressed();
       // super.onBackPressed();
       // super.finish();

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
        cal.set(Calendar.DAY_OF_WEEK,day);
    }
    public void showDatePickerDialog()
    {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    private void scheduleNotification(Notification notification, long delay)
    {

        Intent notificationIntent = new Intent(this, NotificationPublisher1.class);
        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION_ID, note_id);
        notificationIntent.putExtra("filename1",et_title.getText().toString());
        Log.e(TAG,"RED NOTE id is "+note_id);
        notificationIntent.putExtra(NotificationPublisher1.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this,note_id, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        Log.e(TAG, "RED futureInMillis is " + futureInMillis);
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
        Toast.makeText(this,"Reminder is set.",Toast.LENGTH_SHORT).show();
        //reminderSet=true;
    }

    public Notification getNotification(String content)
    {
        Intent myIntent = new Intent(this, MainActivity.class);
        myIntent.putExtra("filename", filename);
        Log.e(TAG,"RED NOTIFICATION FILENAME IS "+filename);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,note_id, myIntent,0);
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle(et_title.getText().toString());
        Log.e(TAG, "RED NOTIFICATION TITLE IS " + et_title.getText().toString());
        builder.setContentText(content);
        Log.e(TAG, "RED CONTENT IS " + content);
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.utilities_notepad_icon);
        return builder.build();

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
        String ti[]={"AM","PM"};
        int month=calendar.get(Calendar.MONTH)+1;
        String date=calendar.get(Calendar.DATE)+"/"+month+"/"+calendar.get(Calendar.YEAR)+" "+days[calendar.get(Calendar.DAY_OF_WEEK)-1]+" "+calendar.get(Calendar.HOUR)+":"+calendar.get(Calendar.MINUTE)+" "+ti[calendar.get(Calendar.AM_PM)];
        Log.e(TAG,"RED DIFFERENCE IN TIME IS "+differ);
//        Log.e(TAG,"Something in note is "+et_note.getText().toString());
        if(differ<0)
            Toast.makeText(getApplicationContext(),"Unable to set reminder in the past.",Toast.LENGTH_SHORT).show();
            else
            {
            SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean(filename+"REMINDER#$@",true);
            Main2.adapter.notifyDataSetChanged();
            editor.putString(filename + "DATE*&^", date);
            editor.commit();
            scheduleNotification(getNotification(first_item), differ);
            }
    }
}

