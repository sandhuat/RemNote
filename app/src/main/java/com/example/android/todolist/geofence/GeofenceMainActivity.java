package com.example.android.todolist.geofence;/*
* Copyright 2013 The Android Open Source Project
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


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.example.android.todolist.R;
import com.example.android.todolist.activities.SampleActivityBase;
import com.example.android.todolist.geofence.cardstream.CardStream;
import com.example.android.todolist.geofence.cardstream.CardStreamFragment;
import com.example.android.todolist.geofence.cardstream.CardStreamState;
import com.example.android.todolist.geofence.cardstream.OnCardClickListener;
import com.example.android.todolist.geofence.cardstream.StreamRetentionFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;


public class GeofenceMainActivity extends SampleActivityBase implements CardStream
{
    public static final String TAG = "GeofenceMainActivity";
    public static final String FRAGTAG = "PlacePickerFragment";

    private CardStreamFragment mCardStreamFragment;

    private StreamRetentionFragment mRetentionFragment;
    String filename="";
    private static final String RETENTION_TAG = "retention";

    private static final int REQUEST_SELECT_PLACE = 1000;
    int position;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(37.398160, -122.180831), new LatLng(37.430610, -121.972090));



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.geofence_main1);

        FragmentManager fm = getSupportFragmentManager();
        PlacePickerFragment fragment = (PlacePickerFragment) fm.findFragmentByTag(FRAGTAG);
        Bundle args = new Bundle();
        args.putString("LOC*#@", getIntent().getExtras().getString("LOC&^%"));
        position= getIntent().getIntExtra("position",-1);
        args.putInt("position",position);
        if (fragment == null)
        {
            FragmentTransaction transaction = fm.beginTransaction();
            fragment = new PlacePickerFragment();
            fragment.setArguments(args);
            transaction.add(fragment, FRAGTAG);
            transaction.commit();
        }


        // Use fragment as click listener for cards, but must implement correct interface
        if (!(fragment instanceof OnCardClickListener))
        {
            throw new ClassCastException("PlacePickerFragment must " +
                    "implement OnCardClickListener interface.");
        }
      OnCardClickListener clickListener = (OnCardClickListener) fm.findFragmentByTag(FRAGTAG);

        mRetentionFragment = (StreamRetentionFragment) fm.findFragmentByTag(RETENTION_TAG);
        if (mRetentionFragment == null) {
            mRetentionFragment = new StreamRetentionFragment();
            fm.beginTransaction().add(mRetentionFragment, RETENTION_TAG).commit();
        } else {
            // If the retention fragment already existed, we need to pull some state.
            // pull state out
           CardStreamState state = mRetentionFragment.getCardStream();

            // dump it in CardStreamFragment.
            mCardStreamFragment = (CardStreamFragment) fm.findFragmentById(R.id.fragment_cardstream);
            mCardStreamFragment.restoreState(state, clickListener);
        }
    }




    public CardStreamFragment getCardStream() {
        if (mCardStreamFragment == null) {
            mCardStreamFragment = (CardStreamFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment_cardstream);
        }
        return mCardStreamFragment;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
          CardStreamState state = getCardStream().dumpState();
        mRetentionFragment.storeCardStream(state);
    }


}
