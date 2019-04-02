package com.example.android.todolist;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dell on 5/18/2016.
 */
public class HashMapWrapper2 {

    public HashMap<String, ArrayList<String>> getMyMap() {
        return myMap;
    }

    public void setMyMap(HashMap<String, ArrayList<String>> myMap) {
        this.myMap = myMap;
    }

    private HashMap<String,ArrayList<String>> myMap;


}
