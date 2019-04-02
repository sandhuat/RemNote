package com.example.android.todolist.geofence;

/**
 * Created by Dell on 5/21/2016.
 */

import android.Manifest;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.todolist.HashMapWrapper;
import com.example.android.todolist.HashMapWrapper2;
import com.example.android.todolist.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dell on 5/17/2016.
 */
public class GeofenceMainActivity21 extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    protected static final String TAG = "GeofenceMainActivity2";
    LatLng coord;
    /**
     * Provides the entry point to Google Play services.
     */
    private HashMap<String, ArrayList<String>> PLACESidsANDFILE;
    protected GoogleApiClient mGoogleApiClient;

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


    String filename;
    TextView radiusValue;
    int radius;
    Gson gson = new Gson();


    HashMapWrapper wrapper = new HashMapWrapper();
    Place mPlace;
    String tone_selected;
    boolean recentSelected=false;
    Cursor returnCursor;
    int nameIndex;
int hasGeofenceAdded;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_geofencemain21);
        PLACESidsANDFILE=new HashMap<String,ArrayList<String>>();
        // Get the UI widgets.

        hasGeofenceAdded=0;
        // Empty list for storing geofences.
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.e("Tag","Populate 1");
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_36dp);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });









        mGeofenceList = new ArrayList<Geofence>();





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
        mGeofencePendingIntent = null;

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        coord = bundle.getParcelable("LatLng");
        mPlace=bundle.getParcelable("Place");
        Log.e("TAG","The place ID is "+mPlace.getName());
        filename=getIntent().getStringExtra("1LOCS$#");
        radius=getIntent().getIntExtra("radiusGeo",-1);
       // hasGeofenceAdded=getIntent().getIntExtra("hasGeofenceAddedbefore",0);

       hasGeofenceAdded=sharedpreferences.getInt(filename+mPlace.getId()+"beenAddedBefore",0);
        ArrayList<LatLng> lonlat= new ArrayList<LatLng>();
        if(Constants.BAY_AREA_LANDMARKS.get(filename)!=null)
            lonlat=Constants.BAY_AREA_LANDMARKS.get(filename);
        mSharedPreferences = getSharedPreferences(Constants.SHARED_PREFERENCES_NAME,
                MODE_PRIVATE);
        if(hasGeofenceAdded==0) {
            lonlat.add(coord);

            Constants.BAY_AREA_LANDMARKS.put(filename, lonlat);

        // Retrieve an instance of the SharedPreferences object.


        // Get the value of mGeofencesAdded from SharedPreferences. Set to false as a default.
        mGeofencesAdded = mSharedPreferences.getBoolean(filename+mPlace.getId(), false);
        }
        // Get the geofences used. Geofence data is hard coded in this sample.
        populateGeofenceList();

        // Kick off the request to build GoogleApiClient.
        buildGoogleApiClient();


    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the LocationServices API.
     */
    protected synchronized void buildGoogleApiClient()
    {
        Log.e("Tag","Populate 2");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }




    @Override
    protected void onStart() {
        super.onStart();
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                final AlertDialog.Builder alert = new AlertDialog.Builder(this);

                alert.setTitle("Location Permission");
                alert.setMessage("Allow app access to phone location?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ActivityCompat.requestPermissions(GeofenceMainActivity21.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);


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
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);


            }


        }
        else

            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        addGeofencesBut();
        Log.i(TAG, "Connected to GoogleApiClient");
        Log.e("Tag","Populate 3");

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason.
        Log.i(TAG, "Connection suspended");

        // onConnected() will be called again automatically when the service reconnects
    }

    /**
     * Builds and returns a GeofencingRequest. Specifies the list of geofences to be monitored.
     * Also specifies how the geofence notifications are initially triggered.
     */
    private GeofencingRequest getGeofencingRequest()
    {
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

    /**
     * Adds geofences, which sets alerts to be notified when the device enters or exits one of the
     * specified geofences. Handles the success or failure results returned by addGeofences().
     */
    public void addGeofencesBut()
    {
        Log.e("Tag","Populate 5");

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
    }

    /**
     * Removes geofences, which stops further notifications when the device enters or exits
     * previously registered geofences.
     */
    public void removeGeofencesButtonHandler(View view) {
        Log.e("Tag","Populate 6");

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            // Remove geofences.
            LocationServices.GeofencingApi.removeGeofences(
                    mGoogleApiClient,
                    // This is the same pending intent that was used in addGeofences().
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().
        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            logSecurityException(securityException);
        }
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

        if (status.isSuccess())
        {
            // Update state and save in shared preferences.

            mGeofencesAdded = !mGeofencesAdded;
            Log.e("TAg","the mGeofencesAddedd val is "+mGeofencesAdded);
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putBoolean(filename+mPlace.getId(), true);
            editor.apply();

            // Update the UI. Adding geofences enables the Remove Geofences button, and removing
            // geofences enables the Add Geofences button.
          //  mGeofencesAdded=true;
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
            if(hasGeofenceAdded==1)
            {
                Toast.makeText(this,"Geofence settings saved",Toast.LENGTH_SHORT).show();
            }
            else
                if(mGeofencesAdded)
                    Toast.makeText(this,"Geofence added",Toast.LENGTH_SHORT).show();
           // Toast.makeText(this, getString( ? R.string.geofences_added : R.string.geofences_removed),
             //       Toast.LENGTH_SHORT
           // ).show();
            SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor1 = sharedPreferences.edit();
          editor1.putInt(filename+mPlace.getId()+"beenAddedBefore",1);
            editor.apply();

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
    private PendingIntent getGeofencePendingIntent()
    {
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
        int pos=mSharedPreferences.getInt(filename+mPlace.getId()+"position",-1);

        args.putParcelable(filename+"*&Place", (Parcelable) mPlace);
        intent.putExtra(filename+"bundle42", args);
        return PendingIntent.getService(this,pos, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * This sample hard codes geofence data. A real app might dynamically create geofences based on
     * the user's location.
     */
    public void populateGeofenceList()
    {
        Log.e("Tag","Populate 10");

        for (Map.Entry<String, ArrayList<LatLng>> entry : Constants.BAY_AREA_LANDMARKS.entrySet())
        {
            Log.e("TAG","THE names in BAY_AREA are "+entry.getValue());
            for(LatLng lt:entry.getValue())
            {
                String fileName_Here=entry.getKey();
                SharedPreferences sharedPreferences=getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
              int rad=sharedPreferences.getInt(filename+lt+"Geordis",-1);
                Log.e("TAG","LongLt in 21 is "+lt);
                if(rad==-1)
                {
                    rad=radius;
                }
                //filename+mPlace.getId()+"Geordis"
                Log.e("Tag","Radius values in 21 are "+rad);
                mGeofenceList.add(new Geofence.Builder()
                        // Set the request ID of the geofence. This is a string to identify this
                        // geofence.
                        .setRequestId(entry.getKey())
                        // Set the circular region of this geofence.
                        .setCircularRegion(
                                lt.latitude,
                                lt.longitude,
                                rad
                        )

                        // Set the expiration duration of the geofence. This geofence gets automatically
                        // removed after this period of time.
                        .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                        // Set the transition types of interest. Alerts are only generated for these
                        // transition. We track entry and exit transitions in this sample.
                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                                Geofence.GEOFENCE_TRANSITION_EXIT)

                        // Create the geofence.
                        .build());
            }
        }

        wrapper.setMyMap(Constants.BAY_AREA_LANDMARKS);
        String serializedMap = gson.toJson(wrapper);
        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("GEOfenceData",serializedMap);
        editor.apply();
    }





}


