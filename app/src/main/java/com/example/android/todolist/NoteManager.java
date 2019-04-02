package com.example.android.todolist;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
public class NoteManager
{
    static final String TAG="asd";
    private Context context;
    public ArrayList<HashMap<String, String>> date_list;
    HashMap<String, String> date_map;
    private String path = Environment.getExternalStorageDirectory().toString()+"/Todo/";
    // if SDCARD THERE  - Environment.getExternalStorageDirectory().toString()+"/Todo/"



    public NoteManager(Context _context) {

        this.context = _context;
        date_list=new ArrayList<HashMap<String, String>>();
    }

    public NoteManager()
    {
        date_list=new ArrayList<HashMap<String, String>>();
    }

    public void CreateNewDirectory()
    {
        File mydir = new File(this.path);
      //  if(!mydir.exists())
            mydir.mkdirs();
        //else
          //  Log.d("error", "dir. already exists");
    }

    public void SaveNote(String sFileName,ArrayList<String> items,FileName f,String old_title)
    {
        Main2.newViewCond=false;
String content="";
        Log.e(TAG,"LUCY IN SAVE");
        try
        {
            File root = new File(this.path);
            if (!root.exists())
            {
                root.mkdirs();
            }
            File file = new File(root, sFileName);
            Log.e(TAG, "FileNN saved is " + sFileName);
//MAJOR CHANGES


            FileName filename= new FileName(sFileName);
            filename.setLongDate(file.lastModified());
       if(items.get(0).length()>0)
                filename.setShorttext(items.get(0));
            else
           filename.setShorttext("List item 1");

            SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(sFileName,"two");
            editor.putBoolean(sFileName+".*exists&*",true);
            editor.apply();
            FileWriter writer = new FileWriter(file);
            /*writer.append(sBody);
            writer.flush();
            writer.close();*/



            try
            {

                for(String str: items){
content+=str;
                    writer.append(str+"\n");
                }
                writer.flush();
                writer.close();
            }

            catch (IOException e)
            {
                e.printStackTrace();
            }

Log.e(TAG,"Here");
            Toast.makeText(this.context, "ToDo list Saved", Toast.LENGTH_LONG).show();

            String cond1=sharedpreferences.getString(old_title+".txt", "minus");
            Log.e("TAG","oldName in List Save is "+old_title);

            Log.e("TAG","LIST CONDITION  "+cond1);

            //NEW STUFF COMING THROUGH
            if(!cond1.equals("two"))
            {
                Log.e("TAG","Notified the TODO List");
                boolean isReminderSet=sharedpreferences.getBoolean(sFileName+"REMINDER#$@",false);
                filename.reminderSet(isReminderSet);
                filename.setContent(content);
                Main2.filenames.add(filename);
             //   Main2.data.add(filename);
                Main2.adapter.data.add(filename);
                Main2.adapter.notifyDataSetChanged();

            }
            else
            {
                Log.e("TAG","Notified the TODO List 2");

                try
                {
                    filename = Main2.filenames.get(Main2.filenames.indexOf(f));
                        filename.setShorttext(items.get(0));
                    filename.setName(sFileName);
                    filename.setContent(content);

                    Log.e("TAG","Notified the TODO List 3");
                    FileName g = Main2.filenames.get(Main2.filenames.indexOf(f));
                    g.setName(sFileName);
                    FileName g1 = Main2.adapter.data.get(Main2.filenames.indexOf(f));
                    g1.setName(sFileName);
                    Main2.adapter.notifyDataSetChanged();

                }
                catch(IndexOutOfBoundsException e)
                {
                    Log.e("sda","Exception Handled");
                }
                catch(NullPointerException e)
                {
                    // Handled if the filenames is null,i.e.,the first item is this list
                }



            }
            editor.putString(sFileName, "two");
            Log.e("sdasa", "Value of FILE IN COND1 is " + sFileName) ;
            editor.commit();
            //NEW STUFF ENDS

            String cond2=sharedpreferences.getString(sFileName, "minus");
            Log.e("TAG", "NOTIFIED THE END COND is " + cond2);
        /*  Intent i=new Intent(context,Main2.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);*/
        }
        catch(IOException e)
        {
            e.printStackTrace();
            //importError = e.getMessage();
            //iError();
        }

    }
    public FileName SaveNote1(String sFileName, String sBody,boolean showToast,FileName f1,String old)
    {
        Log.e("sad","COND sFileName is "+sFileName +" & old is "+old);
        FileName filename = null;
        try
        {
            File root = new File(this.path);
            if (!root.exists())
            {
                root.mkdirs();
            }
            Main2.newViewCond=false;


            File file = new File(root, sFileName);
            FileWriter writer = new FileWriter(file);
            writer.append(sBody);
            writer.flush();
            writer.close();
            String desc = new NoteManager().readNote1(sFileName);
            filename= new FileName(sFileName);
            filename.setContent(sBody);
            filename.setLongDate(file.lastModified());
            if(file.getName().endsWith(".3gp"))
                filename.setShorttext("Sound recording");
            else
                filename.setShorttext(desc);

            SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String cond1=sharedpreferences.getString(old, "minus");


            try
            {
                FileName g = Main2.filenames.get(Main2.filenames.indexOf(f1));
                g.setName(sFileName);
            }
            catch(IndexOutOfBoundsException e)
            {
            }
            catch(NullPointerException e)
            {
                // In case of when first note is created
            }
            Log.e("sdasa", "Value of COND1 is " + cond1) ;


            if(!cond1.equals("one"))
            {
                boolean isReminderSet=sharedpreferences.getBoolean(sFileName+"REMINDER#$@",false);
                filename.reminderSet(isReminderSet);

                Main2.filenames.add(filename);
                if(Main2.adapter==null)
                    Main2.adapter = new ListViewAdapter(context, R.layout.listview_item, Main2.filenames, 1);
                Main2.adapter.data.add(filename);

                Main2.adapter.notifyDataSetChanged();


            }
           else
            {

        try
        {
         filename = Main2.filenames.get(Main2.filenames.indexOf(f1));
            FileName filenmae2=Main2.adapter.data.get(Main2.filenames.indexOf(f1));
            if (file.getName().endsWith(".3gp"))
            {
                filename.setShorttext("Sound recording");
                filenmae2.setShorttext("Sound recording");
            }
            else {
                filename.setShorttext(desc);
                filenmae2.setShorttext(desc);
            }
            filename.setName(sFileName);
            filenmae2.setName(sFileName);

            Main2.adapter.notifyDataSetChanged();

        }
        catch(IndexOutOfBoundsException e)
        {
            Log.e("sda","Exception Handled");
        }

}

            SharedPreferences.Editor editor = sharedpreferences.edit();
            Log.e("TAG","sFileName in Note Save is "+sFileName);
            editor.putString(sFileName, "one");
            editor.putBoolean(sFileName+".*exists&*",true);
            Log.e("sdasa", "Value of FILE IN COND1 is " + sFileName) ;
            editor.commit();
            String cond2=sharedpreferences.getString(sFileName, "minus");
            Log.e("TAG", "COND2 is " + cond2);

            if(showToast)
            {
                Toast.makeText(this.context, "Note Saved", Toast.LENGTH_LONG).show();

            }
          /*  if(showToast)
            {
                Toast.makeText(this.context, "Note Saved", Toast.LENGTH_LONG).show();
                Intent i = new Intent(context, Main2.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }*/
        }
        catch(IOException e)
        {
            e.printStackTrace();
            //importError = e.getMessage();
            //iError();
        }

        return filename;
    }

    public String readNote1(String filename)
    {
        StringBuilder text = new StringBuilder();

        try
        {
            //File sdcard = Environment.getExternalStorageDirectory() + "/Notes/";
            File file = new File(this.path, filename);
            //System.out.println("exception");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                //System.out.println("text : "+text+" : end");
                text.append('\n');

            }
        }
        catch (IOException e) {
            e.printStackTrace();
            //System.out.println("hello");

        }

        return text.toString();
    }

    public void deleteNotea(String sFileName,boolean a,FileName f)
    {
        File root = new File(this.path);
       // int pos=sFileName.indexOf(filenames)
        if(sFileName==null)
        {
            Toast.makeText(this.context,"Cannot delete an unsaved file.",Toast.LENGTH_SHORT).show();
        }
        else
        {

            if (!root.exists()) {
                root.mkdirs();
            }

            File file = new File(root, sFileName);

            if(!a)
            {
                file.delete();
                Log.e(TAG, "the file which is deleted is " + file.getName());
            }
            else
            {

                if (file.delete())
                {

                    Main2.filenames.remove(f);
                    Main2.adapter.data.remove(f);
                    Main2.adapter.notifyDataSetChanged();

                    Log.e(TAG, "the file which is deleted is " + file.getName());
                    int len=file.getName().length();
                    Toast.makeText(this.context, file.getName().substring(0, len - 4) + " deleted!", Toast.LENGTH_LONG).show();
SharedPreferences sharedPreferences=context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(sFileName+"REMINDER#$@");
                    editor.remove(sFileName+"REMINDER#$@tone");
                    editor.remove(sFileName+".*exists&*");
                    editor.commit();

                    //  Intent i = new Intent(context, Main2.class);
                    //context.startActivity(i);
                }
                else

                {
                    Toast.makeText(this.context, "Unable to delete " + file.getName(), Toast.LENGTH_LONG).show();

                }
            }}

    }

    public void deleteAll()
    {
        File dir=new File(path);
        if(dir!=null)
        {
            File[] filenames=dir.listFiles();
            for(File tempf:filenames)
            {
                tempf.delete();
            }
        }

    }

    public String readNote(String filename)
    {
        StringBuilder text = new StringBuilder();

        try {
            //File sdcard = Environment.getExternalStorageDirectory() + "/Notes/";
            File file = new File(this.path, filename);
            //System.out.println("exception");

            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                //System.out.println("text : "+text+" : end");
                text.append('\n');
            } }
        catch (IOException e) {
            e.printStackTrace();
            //System.out.println("hello");

        }

        return text.toString();
    }

    public ArrayList<FileName> GetNotesList()
    {

        File f = new File(this.path);

        File file[] = f.listFiles();
//        Log.e(TAG,"LUCY FILE LENGTH IS "+file.length);

        ArrayList<FileName> filenames = new ArrayList<FileName>();
        for (int i=0; i < file.length; i++)
        {
           String desc = new NoteManager().readNote1(file[i].getName());
            String text="" ;


            SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
           String akaron= sharedpreferences.getString(file[i].getName(), "993");
      if(akaron.equals("one"))
         text = readNote(file[i].getName());
            else if(akaron.equals("two"))
              text = readItems(file[i].getName());
            else
               text="Sound Recording";

            FileName filename = new FileName(file[i].getName());
            filename.setContent(text);
            filename.setLongDate(file[i].lastModified());
            if(file[i].getName().endsWith(".3gp"))
                filename.setShorttext("Sound recording");
            else
                filename.setShorttext(desc);
            Log.e(TAG,"LUCY FILE NAME IS "+file[i].getName());
            filenames.add(filename);
        }

        return filenames;
    }

    private String readItems(String filename)
    {
ArrayList<String> items;
        String content="";
        Log.e(TAG,"LUCY ITEMS FILE NAME - "+filename);
        File todoFile = new File(Environment.getExternalStorageDirectory().toString()+"/Todo/",filename);
        try {
            Log.e(TAG,"LUCY ITEMS IN THE FILE ARE - "+ FileUtils.readLines(todoFile));
            items = new ArrayList<String>(FileUtils.readLines(todoFile));

            Log.e(TAG,"LUCY ITEMS SIZE  - "+items.size());
        }
        catch (IOException e)
        {
            items = new ArrayList<String>();
        }
        for(int h=0;h<items.size();h++)
        {
          content+=items.get(h);
        }
        return content;
    }
    public ArrayList<FileName> GetNotesList2()
    {

        File f = new File(this.path);
        File file[] = f.listFiles();
        Log.e(TAG,"LUCY FILE LENGTH IS "+file.length);
        SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        boolean isReminderSet;

        ArrayList<FileName> filenames = new ArrayList<FileName>();
        for (int i=0; i < file.length; i++)
        {
            String desc = new NoteManager().readNote1(file[i].getName());
            FileName filename = new FileName(file[i].getName());
            filename.setLongDate(file[i].lastModified());
            filename.setShorttext(desc);

            isReminderSet=sharedpreferences.getBoolean(file[i].getName()+"REMINDER#$@",false);
            Log.e(TAG, "FILEEE NAME IN NOTE MANAGER IS " + file[i].getName());
            if(isReminderSet)
            {
                filenames.add(filename);
                editor.putInt(file[i].getName() + "RemindPOSIT", filenames.indexOf(filename));
                editor.apply();
            }
        }

        return filenames;
    }

    public ArrayList<FileName> addNote(ArrayList<FileName> arr,FileName d)
    {
       arr.add(d);
        return arr;
    }

    public boolean isSameName(String file)
    {
        File f = new File(this.path);
        File files[] = f.listFiles();
        for (int i=0; i < files.length; i++)
        {
            if(file.equals(files[i].getName()))
            {
                return true;
            }


        }
return false;

    }
    public void details(String fileName)
    {
        File root = new File(this.path);
        if(fileName==null)
        {
            Toast.makeText(this.context,"Cannot delete an unsaved file.",Toast.LENGTH_SHORT).show();
        }
        else
        {

            if (!root.exists())
            {
                root.mkdirs();
            }

            File file = new File(root, fileName);
            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.layout_details);
            dialog.setTitle("Details of the file");
            TextView name = (TextView) dialog.findViewById(R.id.heading);
            TextView name2=(TextView)dialog.findViewById(R.id.name);
            TextView size = (TextView) dialog.findViewById(R.id.size);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");


            name.setText("Name: " + file.getName());
            name2.setText("Last Modified: "+sdf.format(file.lastModified()));
            size.setText("Path: "+file.getAbsolutePath());
            dialog.show();
        }

    }
    public File getNoteFile(String filename)
    {


            //File sdcard = Environment.getExternalStorageDirectory() + "/Notes/";
            File file = new File(this.path, filename);
            //System.out.println("exception");


        return file;
    }



}
