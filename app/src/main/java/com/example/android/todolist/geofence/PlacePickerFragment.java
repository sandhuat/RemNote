package com.example.android.todolist.geofence;/*
* Copyright 2015 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.android.todolist.HashMapWrapper2;
import com.example.android.todolist.R;
import com.example.android.todolist.geofence.cardstream.Card;
import com.example.android.todolist.geofence.cardstream.CardStream;
import com.example.android.todolist.geofence.cardstream.CardStreamFragment;
import com.example.android.todolist.geofence.cardstream.OnCardClickListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample demonstrating the use of {@link PlacePicker}.
 * This sample shows the construction of an {@link Intent} to open the PlacePicker from the
 * Google Places API for Android and select a {@link Place}.
 *
 * This sample uses the CardStream sample template to create the UI for this demo, which is not
 * required to use the PlacePicker API. (Please see the Readme-CardStream.txt file for details.)
 *
 * @see com.google.android.gms.location.places.ui.PlacePicker.IntentBuilder
 * @see com.google.android.gms.location.places.ui.PlacePicker
 * @see com.google.android.gms.location.places.Place
 */
public class PlacePickerFragment extends Fragment implements OnCardClickListener,GoogleApiClient.OnConnectionFailedListener,SearchView.OnQueryTextListener,PlaceSelectionListener {

    private static final String TAG = "PlacePickerSample";

    private CardStreamFragment mCards = null;

    // Buffer used to display list of place types for a place
    private final StringBuffer mPlaceTypeDisplayBuffer = new StringBuffer();

    // Tags for cards
    private static final String CARD_INTRO = "INTRO";
    private static final String CARD_PICKER = "PICKER";
    private static final String CARD_DETAIL = "DETAIL";
      HashMap<String, String> placeIdsAndNames;

    String filename;
    Place placeIntent;
    int count=0;
    Place myPlace;
    /**
     * Action to launch the PlacePicker from a card. Identifies the card action.
     */
    private static final int ACTION_PICK_PLACE = 1;

    /**
     * Request code passed to the PlacePicker intent to identify its result when it returns.
     */
    private static final int REQUEST_PLACE_PICKER = 1;

    public HashMap<String, ArrayList<String>> PLACESidsANDFILE;

    private static final int GOOGLE_API_CLIENT_ID = 0;

    protected GoogleApiClient mGoogleApiClient;
    private static final int CHECK_DETAILS = 2;

int position;
    CardStreamFragment stream;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        filename = args.getString("LOC*#@");
        position=args.getInt("position");
        Gson gson = new Gson();
        PLACESidsANDFILE=new HashMap<String,ArrayList<String>>();
        placeIdsAndNames = new HashMap<String, String>();
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), GOOGLE_API_CLIENT_ID /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String nb=sharedpreferences.getString("PlaceIdHashMap","");
        if(nb.length()!=0)
        {
            HashMapWrapper2 wrapper = gson.fromJson(nb, HashMapWrapper2.class);
             PLACESidsANDFILE= wrapper.getMyMap();
        }
        for (Map.Entry<String, ArrayList<String>> entry : PLACESidsANDFILE.entrySet())
        {
            Log.e("TAG","Streax KJ"+entry.getKey());
            Log.e("TAG","Streax filename"+filename);

            if(entry.getKey().equals(filename.substring(0, filename.length() - 4)))
            {
//Place mPlace= getPlaceById(entry.getValue());
                ArrayList<String> serc=new ArrayList<String>();
                serc=entry.getValue();
                for(String s:serc)
                {
                    PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,s);

                    placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                }

            }
        }
    }
public void buildACard(Place place)
{
    Card c = new Card.Builder(this,place.getName().toString())
            .setTitle(getString(R.string.empty))
            .setDescription(getString(R.string.empty))
            .setPlaceId(place.getId())
            .addAction("Check Details",CHECK_DETAILS,Card.ACTION_NEUTRAL)
            .build(getActivity());



    getCardStream().addCard(c, true);

}


    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess())
            {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);
            final CharSequence name = place.getName();
            final CharSequence address = place.getAddress();
            final CharSequence phone = place.getPhoneNumber();
            final String placeId = place.getId();
            final LatLng location = place.getLatLng();
            placeIdsAndNames.put(name.toString(),placeId);
            buildACard(place);

            // Update data on card.
            getCardStream().getCard(place.getName().toString())
                    .setTitle(name.toString())
                    .setDescription(getString(R.string.detail_text, placeId, address, phone, ""));

            // Print data to debug log
            Log.d(TAG, "Place selected: " + placeId + " (" + name.toString() + ")");

            // Show the card.
            getCardStream().showCard(place.getName().toString());




            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };


    private ResultCallback<PlaceBuffer> mSendPlaceCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            // Get the Place object from the buffer.
              myPlace = places.get(0);

            Place frozen = myPlace.freeze();


//putInAnotherPlace(myPlace);

           Log.e("TAg","In callback place name is "+myPlace.getName());

            places.release();
            Bundle args = new Bundle();
            args.putParcelable("LatLng", frozen.getLatLng());

            args.putParcelable("Place", (Parcelable) frozen);

            Intent i=new Intent(getActivity(), GeofenceMainActivity2.class);
            i.putExtra("1LOCS$#",filename.substring(0, filename.length() - 4));
            i.putExtra("position",position);
            Log.e("TAG","the position in nj"+position);
            i.putExtra("bundle", args);
          //  getActivity().finish();
            getActivity().finish();

            startActivity(i);

            //    goToNext();

        }
    };




    @Override
    public void onResume() {
        super.onResume();

        // Check if cards are visible, at least the picker card is always shown.
       stream = getCardStream();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {


                final AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

                alert.setTitle("Location Permission");
                alert.setMessage("Allow app access to phone location?");
                alert.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);


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
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 123);


            }


        }
        else {
            permissionRedundancy();
        }

    }


    public void permissionRedundancy()
    {
        if (stream.getVisibleCardCount() < 1) {
            // No cards are visible, sample is started for the first time.
            // Prepare all cards and show the intro card.
            initialiseCards();
            // Show the picker card and make it non-dismissible.
            getCardStream().showCard(CARD_PICKER, false);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        switch (requestCode) {

            case 123:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    // Permission Granted
                    permissionRedundancy();

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
    public void onCardClick(int cardActionId, String cardTag) {
        if (cardActionId == ACTION_PICK_PLACE) {
            // BEGIN_INCLUDE(intent)
            /* Use the PlacePicker Builder to construct an Intent.
            Note: This sample demonstrates a basic use case.
            The PlacePicker Builder supports additional properties such as search bounds.


             */




            try
            {
                PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                Intent intent = intentBuilder.build(getActivity());
                // Start the Intent by requesting a result, identified by a request code.
                startActivityForResult(intent, REQUEST_PLACE_PICKER);


                // Hide the pick option in the UI to prevent users from starting the picker
                // multiple times.
                showPickAction(false);

            }
            catch (GooglePlayServicesRepairableException e)
            {
                GooglePlayServicesUtil
                        .getErrorDialog(e.getConnectionStatusCode(), getActivity(), 0);
            }
            catch (GooglePlayServicesNotAvailableException e)
            {
                Toast.makeText(getActivity(), "Google Play Services is not available.",
                        Toast.LENGTH_LONG)
                        .show();
            }

            // END_INCLUDE(intent)
        }
        else if (cardActionId == CHECK_DETAILS)
        {


           String placeIDD= getCardStream().getCard(cardTag).getIdd();
            Log.e("TAG","CARD TAG IS "+cardTag+" and place id is "+placeIDD);
          PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient,placeIDD);
            placeResult.setResultCallback(mSendPlaceCallback);





        }
    }

    /**
     * Extracts data from PlacePicker result.
     * This method is called when an Intent has been started by calling
     * {@link #startActivityForResult(android.content.Intent, int)}. The Intent for the
     * {@link com.google.android.gms.location.places.ui.PlacePicker} is started with
     * {@link #REQUEST_PLACE_PICKER} request code. When a result with this request code is received
     * in this method, its data is extracted by converting the Intent data to a {@link Place}
     * through the
     * {@link com.google.android.gms.location.places.ui.PlacePicker#getPlace(android.content.Intent,
     * android.content.Context)} call.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // BEGIN_INCLUDE(activity_result)
        if (requestCode == REQUEST_PLACE_PICKER)
        {
            // This result is from the PlacePicker dialog.

            // Enable the picker option
            showPickAction(true);

            if (resultCode == Activity.RESULT_OK) {
                /* User has picked a place, extract data.
                   Data is extracted from the returned intent by retrieving a Place object from
                   the PlacePicker.
                 */
                final Place place = PlacePicker.getPlace(data, getActivity());


                /* A Place object contains details about that place, such as its name, address
                and phone number. Extract the name, address, phone number, place ID and place types.
                 */
                final CharSequence name = place.getName();
                final CharSequence address = place.getAddress();
                final CharSequence phone = place.getPhoneNumber();
                final String placeId = place.getId();
                final LatLng location = place.getLatLng();
                String attribution = PlacePicker.getAttributions(data);
                if(attribution == null){
                    attribution = "";
                }

                // Update data on card.
                getCardStream().getCard(CARD_DETAIL)
                        .setTitle(name.toString())
                        .setDescription(getString(R.string.detail_text, placeId, address, phone, attribution));

                // Print data to debug log
                Log.d(TAG, "Place selected: " + placeId + " (" + name.toString() + ")");

                // Show the card.
                getCardStream().showCard(CARD_DETAIL);
                Bundle args = new Bundle();
                args.putParcelable("LatLng", location);

                args.putParcelable("Place", (Parcelable) place);

                Intent i=new Intent(getActivity(), GeofenceMainActivity2.class);
                i.putExtra("1LOCS$#",filename.substring(0, filename.length() - 4));
                i.putExtra("position",position);

                i.putExtra("bundle", args);

                startActivity(i);


            } else {
                // User has not selected a place, hide the card.
                getCardStream().hideCard(CARD_DETAIL);
            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
        // END_INCLUDE(activity_result)
    }

    /**
     * Initializes the picker and detail cards and adds them to the card stream.
     */
    private void initialiseCards() {
        // Add picker card.
        Card c = new Card.Builder(this, CARD_PICKER)
                .setTitle(getString(R.string.pick_title))
                .setDescription(getString(R.string.pick_text))
                .addAction(getString(R.string.pick_action), ACTION_PICK_PLACE, Card.ACTION_NEUTRAL)
                .setLayout(R.layout.card_google)
                .build(getActivity());
        getCardStream().addCard(c, false);


        // Add detail card.
        c = new Card.Builder(this, CARD_DETAIL)
                .setTitle(getString(R.string.empty))
                .setDescription(getString(R.string.empty))
                .build(getActivity());
        getCardStream().addCard(c, false);

        // Add and show introduction card.
       /* c = new Card.Builder(this, CARD_INTRO)
                .setTitle(getString(R.string.intro_title))
                .setDescription(getString(R.string.intro_message))
                .build(getActivity());
        getCardStream().addCard(c, true);*/
    }

    /**
     * Sets the visibility of the 'Pick Action' option on the 'Pick a place' card.
     * The action should be hidden when the PlacePicker Intent has been fired to prevent it from
     * being launched multiple times simultaneously.
     * @param show
     */
    private void showPickAction(boolean show){
        mCards.getCard(CARD_PICKER).setActionVisibility(ACTION_PICK_PLACE, show);
    }

    /**
     * Returns the CardStream.
     * @return
     */
    private CardStreamFragment getCardStream() {
        if (mCards == null) {
            mCards = ((CardStream) getActivity()).getCardStream();
        }
        return mCards;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(getActivity(), "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
        getActivity().finish();

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {

        }
        else
        {

        }
        return true;
    }

    @Override
    public void onPlaceSelected(Place place) {


    }

    @Override
    public void onError(Status status) {

    }

}
