package com.example.android.todolist.geofence;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todolist.CircleView;
import com.example.android.todolist.HashMapWrapper;
import com.example.android.todolist.HashMapWrapper2;
import com.example.android.todolist.HoloCircleSeekBar;
import com.example.android.todolist.R;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dell on 5/17/2016.
 */
public class GeofenceMainActivity2 extends AppCompatActivity {

    protected static final String TAG = "GeofenceMainActivity2";
    LatLng coord;
    /**
     * Provides the entry point to Google Play services.
     */
    private HashMap<String, ArrayList<String>> PLACESidsANDFILE;

    /**
     * The list of geofences used in this sample.
     */
    protected ArrayList<Geofence> mGeofenceList;

    /**
     * Used to keep track of whether geofences were added.
     */
    private boolean mGeofencesAdded;

    /**
     * Used when requesting to add or remove geofences.
     */
    private PendingIntent mGeofencePendingIntent;

    /**
     * Used to persist application state about whether geofences were added.
     */
    private SharedPreferences mSharedPreferences;

    // Buttons for kicking off the process of adding or removing geofences.
    private Button mAddGeofencesButton;
    String filename;
    Spinner tone_typeSpinner;
    ArrayList<String> categories;
    TextView radiusValue;
    int radius;
    Gson gson = new Gson();
    private HoloCircleSeekBar circSeek;
    int position;


    HashMapWrapper wrapper = new HashMapWrapper();
    Place mPlace;
    CircleView circView;
    String tone_selected;
    boolean hasSpinnerFirst=true;
boolean recentSelected=false;
    Cursor returnCursor;
    int nameIndex;
    Bundle bundle;

    int hasGeoenceBeenAdded;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofence_main2);
        PLACESidsANDFILE=new HashMap<String,ArrayList<String>>();
        // Get the UI widgets.
        mAddGeofencesButton = (Button) findViewById(R.id.add_geofences_button);
        tone_typeSpinner = (Spinner) findViewById(R.id.tone_types1);
        radiusValue=(TextView)findViewById(R.id.dist_val);
        // Empty list for storing geofences.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        hasGeoenceBeenAdded=0;
        circSeek = (HoloCircleSeekBar) findViewById(R.id.picker);
Log.e("Tag","Populate 1");
        radius=100;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });




        circSeek.setOnSeekBarChangeListener(new HoloCircleSeekBar.OnCircleSeekBarChangeListener()
        {

            @Override
            public void onProgressChanged(HoloCircleSeekBar seekBar, int progress, boolean fromUser) {
                progress+=1;
                int x=100+(progress-1)*49;
                radius=x;
                double y=x/1000.0;
                double circRad=40+(progress-1)*3.4;
                double val_show=Math.round(y * 100D) / 100D;
                radiusValue.setText(String.valueOf(val_show)+" km");
                circSeek.text=String.valueOf(val_show);
            }

            @Override
            public void onStartTrackingTouch(HoloCircleSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(HoloCircleSeekBar seekBar) {

            }


        });
        SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);


        bundle = getIntent().getParcelableExtra("bundle");
        coord = bundle.getParcelable("LatLng");
        mPlace=bundle.getParcelable("Place");
        Log.e("TAG","The place ID is "+mPlace.getName());
        filename=getIntent().getStringExtra("1LOCS$#");
        position=getIntent().getIntExtra("position",-1);
        radius=  sharedPreferences.getInt(filename+mPlace.getLatLng()+"Geordis",50);
        if(radius!=50)
        {
            float progress1=(radius-100)/49;
            hasGeoenceBeenAdded=1;
            circSeek.setValue(progress1);

            double val_show=Math.round(radius/1000.0 * 100D) / 100D;
            radiusValue.setText(String.valueOf(val_show)+" km");
            circSeek.text=String.valueOf(val_show);

        }

        String choiceTone=sharedPreferences.getString(filename+mPlace.getId(),"");
String toneHere=sharedPreferences.getString(filename+mPlace.getId()+"ToneHere","");
Log.e("TAG","Choice tone here is "+choiceTone);
        categories = new ArrayList<String>();
        categories.add("Vibration");
        if(choiceTone.length()!=0 && !(choiceTone.equals("Vibration") || choiceTone.equals("Narrate")||choiceTone.equals("Custom Sounf")))
        categories.add(choiceTone);
        else {
            if(toneHere.length()!=0)
                categories.add(toneHere);
             else
            categories.add("Custom Sound");
        }
        categories.add("Narrate");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        tone_typeSpinner.setAdapter(adapter);
        mGeofenceList = new ArrayList<Geofence>();

        mAddGeofencesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Log.e("TAG","LongLt in 2 is "+mPlace.getLatLng());

                editor.putBoolean(filename+mPlace.getId()+"exists",true);
                editor.putInt(filename+mPlace.getLatLng()+"Geordis",radius);
                Log.e("TAG","geofence, Position is "+position);
                editor.putInt(filename+mPlace.getId()+"position",position);

                editor.apply();
                Intent i1=new Intent(GeofenceMainActivity2.this,GeofenceMainActivity21.class);

                i1.putExtra("1LOCS$#",filename);
                i1.putExtra("bundle", bundle);
                i1.putExtra("radiusGeo",radius);
                i1.putExtra("hasGeofenceAddedbefore",hasGeoenceBeenAdded);
                finish();
                startActivity(i1);
            }
        });
        tone_typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {

                //   repeatSwitch.setChecked(false);
                Log.e("TAG","Trace 1");

                Log.e("TAG","spinner First condition is "+hasSpinnerFirst);

                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String item = parent.getItemAtPosition(position).toString();

                if(hasSpinnerFirst)
                {
                    switch (position)
                    {
                        case 0:
                            Log.e("TAG","spinner First In Vibration");

                            editor.putString(filename + mPlace.getId(),"Vibration");
                            Log.e("TAG", "ALarm Tone for the file is " + filename);
                            editor.apply();
                            break;

                        case 1:
                            Log.e("TAG","spinner First In Custom Sound");

                            if(item.equals("Custom Sound")) {
                                Intent intent_upload1 = new Intent();
                                intent_upload1.setType("audio/*");
                                Log.e("TAG", "SELECTION AUDIO");
                                intent_upload1.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(intent_upload1, 1);
                            }
                            break;
                        case 2:
                            editor.putString(filename + mPlace.getId(),"Narrate");
                            Log.e("TAG","spinner First In Narrate");
                            editor.apply();



                            break;

                    }
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



        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nb=sharedpreferences.getString("GEOfenceData","");
        if(nb.length()!=0)
        {
            HashMapWrapper wrapper = gson.fromJson(nb, HashMapWrapper.class);
            Constants.BAY_AREA_LANDMARKS = wrapper.getMyMap();
        }
String nx=sharedpreferences.getString("PlaceIdHashMap","");
        if(nx.length()!=0)
        {
            Gson gson1 = new Gson();

            HashMapWrapper2 wrapper = gson1.fromJson(nx, HashMapWrapper2.class);
            PLACESidsANDFILE = wrapper.getMyMap();

        }

        // Initially set the PendingIntent used in addGeofences() and removeGeofences() to null.


        String choiceTone1=sharedpreferences.getString(filename+mPlace.getId(),"");
        Log.e("TAG","The choiceTone here is "+choiceTone1);
        if(choiceTone1.length()!=0)
        {

            if(choiceTone1.equals("Vibration"))
            {
                hasSpinnerFirst=true;

                tone_typeSpinner.setSelection(0);
            }
            else if(choiceTone1.equals("Narrate"))
            {
                hasSpinnerFirst=true;

                tone_typeSpinner.setSelection(2);

            }
            else if(!choiceTone1.equals("Custom Sound"))
            {
              //  hasSpinnerFirst=false;
                tone_typeSpinner.setSelection(1);

            }

        }



            ArrayList<LatLng> lonlat= new ArrayList<LatLng>();
        if(Constants.BAY_AREA_LANDMARKS.get(filename)!=null)
            lonlat=Constants.BAY_AREA_LANDMARKS.get(filename);
        lonlat.add(coord);

        Constants.BAY_AREA_LANDMARKS.put(filename, lonlat);
        // Retrieve an instance of the SharedPreferences object.
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);

        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(filename+mPlace.getId(), false);
        setButtonsEnabledState();
        // Get the geofences used. Geofence data is hard coded in this sample.



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
                returnCursor.moveToFirst();
                categories.set(1,returnCursor.getString(nameIndex));
                tone_selected=returnCursor.getString(nameIndex);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
// Specify the layout to use when the list of choices appears
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

// Apply the adapter to the spinner
                tone_typeSpinner.setAdapter(adapter);
                tone_typeSpinner.setSelection(1);
                hasSpinnerFirst=false;
                //  String path = _getRealPathFromURI(getApplicationContext(),uri);
                Log.e("TAG","The mp3 filename is "+returnCursor.getString(nameIndex));
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(filename+mPlace.getId(),tone_selected);
                editor.putString(filename+mPlace.getId()+"ToneHere",tone_selected);
                editor.putString(filename+mPlace.getId()+"custom4*$tone",uri.toString());


                // editor.putString(filename+"%&SET*tone",returnCursor.getString(nameIndex));
               // editor.putString(filename+"custom4*$tone",uri.toString());
                Log.e("TAG","ALarm Tone for the file is "+filename);
                recentSelected=true;
                editor.commit();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_geofence, menu);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case R.id.action_reset:
                resetAdapter();
                return true;






            default:
                return super.onOptionsItemSelected(item);
        }
    }











    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        Log.e("Tag","Populate 4");

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);

        // Return a GeofencingRequest.
        return builder.build();
    }




    private void logSecurityException(SecurityException securityException) {
        Log.e(TAG, "Invalid location permission. " +
                "You need to use ACCESS_FINE_LOCATION with geofences", securityException);
    }

    /**
     * Runs when the result of calling addGeofences() and removeGeofences() becomes available.
     * Either method can complete successfully or with an error.
     *
     * Since this activity implements the {@link ResultCallback} interface, we are required to
     * define this method.
     *
     * @param status The Status returned through a PendingIntent when addGeofences() or
     *               removeGeofences() get called.
     */
    public void onResult(Status status) {
        Log.e("Tag","Populate 7");

        if (status.isSuccess()) {
            // Update state and save in shared preferences.

            mGeofencesAdded = !mGeofencesAdded;
            Log.e("TAg","the mGeofencesAddedd val is "+mGeofencesAdded);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(filename+mPlace.getId(), mGeofencesAdded);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
            setButtonsEnabledState();
if(mGeofencesAdded)
{
    //mean a new place has been added
    Log.e("Tag","Populate 8");

    ArrayList<String> listCorrespondFilename=new ArrayList<String>();
    if(PLACESidsANDFILE.get(filename)!=null)
    listCorrespondFilename=PLACESidsANDFILE.get(filename);

    listCorrespondFilename.add(mPlace.getId());
    PLACESidsANDFILE.put(filename,listCorrespondFilename);
    Gson gson = new Gson();
    HashMapWrapper2 wrapper = new HashMapWrapper2();

    wrapper.setMyMap(PLACESidsANDFILE);

    String serializedMap = gson.toJson(wrapper);
    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

    SharedPreferences.Editor editor1 = sharedpreferences.edit();
    editor1.putString("PlaceIdHashMap",serializedMap);

    editor1.apply();
}

            Toast.makeText(
                    this,
                    getString(mGeofencesAdded ? R.string.geofences_added :
                            R.string.geofences_removed),
                    Toast.LENGTH_SHORT
            ).show();
        } else {
            // Get the status code for the error and log it using a user-friendly message.
            String errorMessage = GeofenceErrorMessages.getErrorString(this,
                    status.getStatusCode());
            Log.e(TAG, errorMessage);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        Log.e("Tag","Populate 9");

        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null)
        {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().

        intent.putExtra("LOC**",filename);
        Bundle args = new Bundle();

        args.putParcelable(filename+"*&Place", (Parcelable) mPlace);
        intent.putExtra(filename+"bundle42", args);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */


    /**
     * Ensures that only one button is enabled at any time. The Add Geofences button is enabled
     * if the user hasn't yet added geofences. The Remove Geofences button is enabled if the
     * user has added geofences.
     */
    private void setButtonsEnabledState() {
        Log.e("Tag","Populate 11");
if(hasGeoenceBeenAdded==1)
{
mGeofencesAdded=false;
}
        if (mGeofencesAdded)
        {

            mAddGeofencesButton.setEnabled(true);
          //  mRemoveGeofencesButton.setEnabled(true);
        }
        else
        {
            mAddGeofencesButton.setEnabled(true);
          //  mRemoveGeofencesButton.setEnabled(false);
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
        editor.remove(filename+mPlace.getId());
        editor.remove(filename+mPlace.getId()+"ToneHere");


        editor.commit();
        Toast.makeText(getApplicationContext(),"Select a new  Alarm Tone",Toast.LENGTH_SHORT).show();
        tone_typeSpinner.setAdapter(adapter);

    }

}

