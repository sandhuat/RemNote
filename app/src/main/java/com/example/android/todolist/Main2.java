package com.example.android.todolist;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialize.util.KeyboardUtil;

import java.util.ArrayList;

public class Main2 extends AppCompatActivity implements SearchView.OnQueryTextListener
{
   public static ArrayList<FileName> filenames;
    private com.mikepenz.materialdrawer.Drawer result = null;
    public static ListViewAdapter adapter;
    String akaron;
    public static ListView lv_filenames;
    static NoteManager nm;
    static Context context;
    final public int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static boolean newViewCond=false;
    Animation animation = null;


    private static final String TAG="asd";
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        filenames=new ArrayList<FileName>();
        nm = new NoteManager(getApplicationContext());
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_in);
        animation.setDuration(400);
        context=getApplicationContext();
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            {


                final AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("Write Permission");
                alert.setMessage("Allow app access to create Notes and Lists?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ActivityCompat.requestPermissions(Main2.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);


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
            else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);


            }


        }
        else
        {
            initializeListView();

        }
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withActionBarDrawerToggle(true)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.title_home).withIcon(FontAwesome.Icon.faw_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName(R.string.title_note).withIcon(FontAwesome.Icon.faw_edit).withIdentifier(2),
                        new PrimaryDrawerItem().withName(R.string.title_todo).withIcon(FontAwesome.Icon.faw_list).withIdentifier(3),
                        new PrimaryDrawerItem().withName(R.string.title_activity_record).withIcon(R.drawable.ic_mic_black_36dp).withIdentifier(4),

        new PrimaryDrawerItem().withName(R.string.title_reminders).withIcon(R.drawable.ic_alarm_black_18dp).withIdentifier(5)

                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem drawerItem) {
                        if (drawerItem != null && drawerItem instanceof Nameable){
                            String name = ((Nameable)drawerItem).getName().getText(Main2.this);
                            //toolbar.setTitle(name);
                        }

                        if (drawerItem != null){
                            int selectedScren = (int) drawerItem.getIdentifier();
                            switch (selectedScren){
                                case 1:
                                    //go to List of Notes
                                    break;
                                case 2:
                                    //go the editor screen
                                    startActivity(new Intent(Main2.this, NoteActivity.class));
                                    break;
                                case 3:
                                    startActivity(new Intent(Main2.this,MainActivity.class));
                                   // Toast.makeText(Main2.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    startActivity(new Intent(Main2.this,RecordActivity.class));
                                    // Toast.makeText(Main2.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                                    break;
                                case 5:
                                    startActivity(new Intent(Main2.this, ReminderListActivity.class));

                                    break;
                            }
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        KeyboardUtil.hideKeyboard(Main2.this);

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







    }

public  void initializeListView()
{
    nm.CreateNewDirectory();
    filenames = nm.GetNotesList();
    adapter = new ListViewAdapter(this, R.layout.listview_item, filenames, 1);
    lv_filenames = (ListView) findViewById(R.id.lv_filename);
    Log.e("TAG","VALUE OF adapter is "+adapter);
    lv_filenames.setAdapter(adapter);
    lv_filenames.setEmptyView(findViewById(R.id.empty));

    lv_filenames.setTextFilterEnabled(true);

    registerForContextMenu(lv_filenames);
    lv_filenames.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                        {
                                            @Override
                                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                SharedPreferences sharedpreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                akaron = sharedpreferences.getString(filenames.get(position).getName(), "993");
                                                Intent intent;
                                                if (akaron.equals("two")) {
                                                    intent = new Intent(Main2.this, MainActivity.class);
                                                    intent.putExtra("filename", filenames.get(position).getName());
                                                    intent.putExtra("position",position);


                                                    startActivity(intent);
                                                } else if (akaron.equals("one")) {
                                                    intent = new Intent(Main2.this, NoteActivity.class);
                                                    intent.putExtra("filename", filenames.get(position).getName());
                                                    intent.putExtra("position",position);
                                                    startActivity(intent);
                                                }
                                                else if (akaron.equals("three")) {
                                                    intent = new Intent(Main2.this, PlayerActivity.class);
                                                    intent.putExtra("filename", filenames.get(position).getName());
                                                    intent.putExtra("position",position);

                                                    startActivity(intent);
                                                }


                                            }

                                        }
    );
    lv_filenames.setLayoutAnimation(new LayoutAnimationController(animation));
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
            Intent intent = new Intent(Main2.this, MainActivity.class);

            intent.putExtra("filename", filenames.get(pos).getName());


            startActivity(intent);
        }
        else if(item.getTitle()=="Delete"){

            AlertDialog.Builder alert = new AlertDialog.Builder(this);

            alert.setTitle("Delete Note");
            alert.setMessage("Are you sure you want to delete the note?");
            alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    FileName f=ListViewAdapter.filenames.get(pos);

                    SharedPreferences sharedpreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    akaron= sharedpreferences.getString(filenames.get(pos).getName(), "993");
                    if(akaron.equals("three") && pos==0)
                        newViewCond=true;
                    else
                        newViewCond=false;
                    new NoteManager(Main2.this).deleteNotea(filenames.get(pos).getName(),true,f);

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
            new NoteManager(Main2.this).details(filenames.get(pos).getName());
        }
        else{
            return false;
        }
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        int searchImgId = android.support.v7.appcompat.R.id.search_button; // I used the explicit layout ID of searchview's ImageView
        ImageView v = (ImageView) searchView.findViewById(searchImgId);
        v.setImageResource(R.drawable.ic_search_white_36dp);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        switch (item.getItemId())
        {
            case R.id.action_add:
                Intent intent = new Intent(Main2.this, NoteActivity.class);
                Log.e(TAG, "Creating");
                startActivity(intent);
                return true;
            case R.id.action_addq:
                Intent intentq = new Intent(Main2.this, MainActivity.class);
                Log.e(TAG, "Creating");
                startActivity(intentq);
                return true;


            case R.id.action_add2:
                Intent intent2 = new Intent(Main2.this, MainActivity.class);
                Log.e(TAG, "Creating");

                startActivity(intent2);

                return true;
            case R.id.action_record:
                Intent intent32 = new Intent(Main2.this, RecordActivity.class);
                Log.e(TAG, "Creating");

                startActivity(intent32);

                return true;
            case R.id.action_add23:
                Intent intent3 = new Intent(Main2.this, NoteActivity.class);
                Log.e(TAG, "Creating");

                startActivity(intent3);

                return true;
            case R.id.action_delall:
                new NoteManager(Main2.this).deleteAll();

                return true;


            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode) {

            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission Granted
                    initializeListView();

                }
                else
                {
                    // Permission Denied

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        super.finish();

    }


    @Override
    public boolean onQueryTextSubmit(String query) {
       return false;
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (TextUtils.isEmpty(newText)) {
            lv_filenames.clearTextFilter();
            adapter.resetData();
        }
        else
        {
            adapter.getFilter().filter(newText.toString());
        }
        return true;
    }
}

