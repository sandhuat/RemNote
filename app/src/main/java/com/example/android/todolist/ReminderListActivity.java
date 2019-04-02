package com.example.android.todolist;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialize.util.KeyboardUtil;

import java.util.ArrayList;

public class ReminderListActivity extends AppCompatActivity
{
    public static ArrayList<FileName> filenames1;
    private com.mikepenz.materialdrawer.Drawer result = null;
    public static RemListAdapter adapter;
    String akaron;
    ListView lv_filenames1;
    FileName f;


    private static final String TAG="asd";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        Log.e("TAG", "SHIIT3333!!!!!!!!!!");
        NoteManager nm = new NoteManager(getApplicationContext());
        nm.CreateNewDirectory();

        filenames1 = nm.GetNotesList2();
        Log.e(TAG, "THe filenames are " + filenames1);
//MAJOR CHANGES

        adapter = new RemListAdapter(this, R.layout.listview_item, filenames1,2);
        lv_filenames1 = (ListView) findViewById(R.id.lv_filename);
        lv_filenames1.setAdapter(adapter);
        lv_filenames1.setEmptyView(findViewById(R.id.empty));
        registerForContextMenu(lv_filenames1);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(5),
                        new PrimaryDrawerItem().withName(R.string.title_note).withIcon(FontAwesome.Icon.faw_edit).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.title_todo).withIcon(FontAwesome.Icon.faw_list).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.title_reminders).withIcon(R.drawable.ic_alarm_black_18dp).withIdentifier(4)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable)
                        {
                            String name = ((Nameable)drawerItem).getName().getText(ReminderListActivity.this);
                            //toolbar.setTitle(name);
                        }

                        if (drawerItem != null){
                            int selectedScren = (int) drawerItem.getIdentifier();
                            switch (selectedScren){
                                case 1:
                                //   startActivity(new Intent(ReminderListActivity.this, Main2.class));
                                    break;
                                case 5:
                                 //   startActivity(new Intent(ReminderListActivity.this, Main2.class));
                                    break;
                                case 2:
                                    //go the editor screen
                                    startActivity(new Intent(ReminderListActivity.this, NoteActivity.class));
                                    break;
                                case 3:
                                    startActivity(new Intent(ReminderListActivity.this,MainActivity.class));
                                    // Toast.makeText(ReminderListActivity.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    startActivity(new Intent(ReminderListActivity.this,ReminderListActivity.class));
                                    break;
                            }
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        KeyboardUtil.hideKeyboard(ReminderListActivity.this);

                    }

                    @Override
                    public void onDrawerClosed(View view) {

                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withFireOnInitialOnClick(true)
                .withSavedInstance(savedInstanceState)
                .build();
        if (savedInstanceState == null){
            result.setSelection(1);
        }


        lv_filenames1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                                @Override
                                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                    SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                    akaron = sharedpreferences.getString(filenames1.get(position).getName(), "993");

                                                    Intent intent;
                                                    if (akaron.equals("two")) {
                                                        intent = new Intent(ReminderListActivity.this, MainActivity.class);
                                                        intent.putExtra("filename", filenames1.get(position).getName());
                                                        intent.putExtra("position",position);


                                                        startActivity(intent);
                                                    } else if (akaron.equals("one")) {
                                                        intent = new Intent(ReminderListActivity.this, NoteActivity.class);
                                                        intent.putExtra("filename", filenames1.get(position).getName());
                                                        intent.putExtra("position",position);

                                                        startActivity(intent);
                                                    }


                                                }

                                            }
        );




    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Select The Action");
        menu.add(0, v.getId(), 0, "Open");//groupId, itemId, order, title
        menu.add(0, v.getId(), 0, "Delete");
        menu.add(0, v.getId(), 0, "Details");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = info.position;

        if(item.getTitle()=="Open"){
            Intent intent = new Intent(ReminderListActivity.this, MainActivity.class);

            intent.putExtra("filename", filenames1.get(pos).getName());


            startActivity(intent);
        }
        else if(item.getTitle()=="Delete"){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Delete Note");
            alert.setMessage("Are you sure you want to delete the note?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    f=ListViewAdapter.filenames.get(pos);
                    new NoteManager(ReminderListActivity.this).deleteNotea(filenames1.get(pos).getName(),true,f);
                }
            });

            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // Canceled.
                }
            });

            alert.show();
        }
        else if(item.getTitle()=="Details")
        {
            new NoteManager(ReminderListActivity.this).details(filenames1.get(pos).getName());
        }
        else{
            return false;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminder, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.



        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed()
    {
        Main2.adapter.notifyDataSetChanged();
        super.onBackPressed();
        super.finish();

    }

}

