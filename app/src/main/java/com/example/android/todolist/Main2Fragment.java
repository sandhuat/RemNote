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
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 5/9/2016.
 */
public class Main2Fragment extends Fragment implements SearchView.OnQueryTextListener
{
    public static ArrayList<FileName> filenames;
    public static ArrayList<FileName> searchFiles;
    public static ListViewAdapter adapter;
    String akaron;
    public static ListView lv_filenames;
    static NoteManager nm;
    static Context context;
    final public int REQUEST_CODE_ASK_PERMISSIONS = 123;
     List<FileName> filteredModelList;

    private static final String TAG="asd";
    public static Main2Fragment newInstance() {
        return new Main2Fragment();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_main2, container, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
       // setContentView(R.layout.activity_main2);
        filenames=new ArrayList<FileName>();
        searchFiles=new ArrayList<FileName>();;
        nm = new NoteManager(getContext());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {


                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("Write Permission");
                alert.setMessage("Allow app access to create Notes and Lists?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);


                    }
                });

                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
//super.onBackPressed();
                        alert.show();

                    }
                });

                alert.show();


            } else
            {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 123);


            }


        }
        else
        {
            nm.CreateNewDirectory();
            filenames = nm.GetNotesList();
            adapter = new ListViewAdapter(getContext(), R.layout.listview_item, filenames, 1);
            lv_filenames = (ListView) getActivity().findViewById(R.id.lv_filename);
            lv_filenames.setAdapter(adapter);
            lv_filenames.setEmptyView(getActivity().findViewById(R.id.empty));
            registerForContextMenu(lv_filenames);
            lv_filenames.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                                {
                                                    @Override
                                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                                        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                        akaron = sharedpreferences.getString(filenames.get(position).getName(), "993");
                                                        Intent intent;
                                                        if (akaron.equals("two")) {
                                                            intent = new Intent(getActivity(), MainActivity.class);
                                                            intent.putExtra("filename", filenames.get(position).getName());
                                                            intent.putExtra("position",position);


                                                            startActivity(intent);
                                                        } else if (akaron.equals("one")) {
                                                            intent = new Intent(getActivity(), NoteActivity.class);
                                                            intent.putExtra("filename", filenames.get(position).getName());
                                                            intent.putExtra("position",position);
                                                            startActivity(intent);
                                                        }
                                                        else if (akaron.equals("three")) {
                                                            intent = new Intent(getActivity(), PlayerActivity.class);
                                                            intent.putExtra("filename", filenames.get(position).getName());
                                                            intent.putExtra("position",position);

                                                            startActivity(intent);
                                                        }


                                                    }

                                                }
            );
        }


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem item = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);
    }

    @Override
    public boolean onQueryTextChange(String query)
    {
         filteredModelList = filter(filenames, query);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    private List<FileName> filter(List<FileName> models, String query) {
        query = query.toLowerCase();

        final List<FileName> filteredModelList = new ArrayList<>();
        for (FileName model : models)
        {
            final String text = model.getName().toLowerCase();
            if (text.contains(query))
            {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
