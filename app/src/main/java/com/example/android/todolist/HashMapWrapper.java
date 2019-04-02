package com.example.android.todolist;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dell on 5/18/2016.
 */
public class HashMapWrapper {


    public HashMap<String, ArrayList<LatLng>> getMyMap() {
        return myMap;
    }

    public void setMyMap(HashMap<String,  ArrayList<LatLng>> myMap) {
        this.myMap = myMap;
    }

    private HashMap<String,ArrayList<LatLng>> myMap;
        // getter and setter for 'myMap'
    }
