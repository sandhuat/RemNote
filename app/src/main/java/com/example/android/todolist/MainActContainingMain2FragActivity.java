package com.example.android.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.Nameable;
import com.mikepenz.materialize.util.KeyboardUtil;

public class MainActContainingMain2FragActivity extends AppCompatActivity {
    private com.mikepenz.materialdrawer.Drawer result = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_act_containing_main2_frag);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
                            String name = ((Nameable)drawerItem).getName().getText(MainActContainingMain2FragActivity.this);
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
                                    startActivity(new Intent(MainActContainingMain2FragActivity.this, NoteActivity.class));
                                    break;
                                case 3:
                                    startActivity(new Intent(MainActContainingMain2FragActivity.this,MainActivity.class));
                                    // Toast.makeText(Main2.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                                    break;
                                case 4:
                                    startActivity(new Intent(MainActContainingMain2FragActivity.this,RecordActivity.class));
                                    // Toast.makeText(Main2.this, "Settings Clicked", Toast.LENGTH_SHORT).show();
                                    break;
                                case 5:
                                    startActivity(new Intent(MainActContainingMain2FragActivity.this, ReminderListActivity.class));

                                    break;
                            }
                        }
                        return false;
                    }
                })
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {
                        KeyboardUtil.hideKeyboard(MainActContainingMain2FragActivity.this);

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
        if(savedInstanceState == null)
        {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, Main2Fragment.newInstance())
                    .commit();
        }

    }

}
