package com.example.android.todolist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.todolist.geofence.Constants;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Dell on 5/18/2016.
 */

    public class WholeScreenAlarmActivity extends Activity
 {
    TextView tv1;
Place mPlace;String placeId;LatLng placeLatLng;
    String filename;
     boolean isNotGeoLocReminder;
    HashMap<String,ArrayList<String>>  PLACESidsANDFILE;  Bundle bundle;
    Button seeNote;int noteType;int position;
    int alarmTone;
    Intent ttsIntent;
     String fullPlaceName;//Intent ttsService;
    Vibrator vib;
     String tone;String placeName;TextView locName;
     Intent musicIntent;
     Intent ttsService;

     @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
            setContentView(R.layout.activity_whole_screen_alarm);
            filename=getIntent().getStringExtra("LOC_FIN");
            isNotGeoLocReminder=getIntent().getBooleanExtra("NotGEOLoc",false);
            locName=(TextView)findViewById(R.id.place_name);
            position=getIntent().getIntExtra("position",-1);
            Log.e("TAG","Filewnam is "+filename);
            seeNote=(Button)findViewById(R.id.gotoNote);
            noteType=getIntent().getIntExtra("NoteType",1);
            alarmTone=getIntent().getIntExtra("alarmTone",0);
Log.e("TAG","ALARM TONE IN WHOLE SCREEN is "+alarmTone);

         bundle = getIntent().getParcelableExtra(filename+"bundle42&");
            Log.e("TAG","Bundle is "+bundle);
          //  mPlace=bundle.getParcelable(filename+"*&*Place");
            placeId= getIntent().getStringExtra(filename + "**id");
            Log.e("TAG","placeIIDD is "+placeId);
              fullPlaceName=getIntent().getStringExtra(filename + "placeName");
            if(!isNotGeoLocReminder)
            {
                placeName = fullPlaceName.substring(0, Math.min(fullPlaceName.length(), 20));
                if (placeName.length() > 0) {
                    placeName = "Near " + placeName + ".";
                    locName.setText(placeName);
                }
            }
            tv1=(TextView)findViewById(R.id.name_note);
            tv1.setText(filename);
if(alarmTone==0)
{
    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

    tone=sharedpreferences.getString(filename+placeId,"");
    if(tone.length()==0)
        alarmTone=1;
    else if(tone.equals("Vibration"))
        alarmTone=1;
    else if(tone.equals("Narrate"))
    {
        alarmTone=5;
    }
    else
        alarmTone=4;
}

            if(alarmTone==1)
            {
              vib = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);

                long[] pattern = {0, 1000, 1000,2000,1000,1000};
                vib.vibrate(pattern, 0);
            }
            else if(alarmTone==2)
            {
                String tone_uri;
                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
             if(noteType!=3)
              tone_uri =sharedpreferences.getString(filename+".txtcustom4*$tone","");
                else
              tone_uri =sharedpreferences.getString(filename+".3gpcustom4*$tone","");


              //  playSound(getApplicationContext(),Uri.parse(tone_uri));
                musicIntent=new Intent(getApplicationContext(), MediaNotificationService.class);
                musicIntent.putExtra("filename",filename);
                musicIntent.putExtra("note_id",position);
                musicIntent.putExtra("content","NoNotificationReqd$%##%");
                musicIntent.putExtra("position",position);
                musicIntent.putExtra("song",tone_uri);
                Log.e("TAG","TAG ONE");
                getApplicationContext().startService(musicIntent);

            }
            else if(alarmTone==3)
            {
                String say="Reminding you of "+filename+".Reminding you of "+filename+".Reminding you of "+filename+".Reminding you of "+filename+".Reminding you of "+filename;
               ttsIntent =new Intent(WholeScreenAlarmActivity.this, SpeechService.class);
               ttsIntent.putExtra("SAY",say);
                getApplicationContext().startService(ttsIntent);
            }
            else if(alarmTone==4)
            {


             //   playSound(getApplicationContext(),Uri.parse(tone));

                SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                musicIntent=new Intent(getApplicationContext(), MediaNotificationService.class);
                musicIntent.putExtra("filename",filename);
                musicIntent.putExtra("note_id",position);
                musicIntent.putExtra("content","NoNotificationReqd$%##%");
                musicIntent.putExtra("position",position);
                tone =sharedpreferences.getString(filename+placeId+"custom4*$tone","");

                musicIntent.putExtra("song",tone);
                Log.e("TAG","TAG ONE");
                getApplicationContext().startService(musicIntent);
            }
            else if(alarmTone==5)
            {
                String say="Reminding you of "+filename+"in the vicinity of "+fullPlaceName+".Reminding you of "+filename+"in the vicinity of "+fullPlaceName+".Reminding you of "+filename+"in the vicinity of "+fullPlaceName+".Reminding you of "+filename+"in the vicinity of "+fullPlaceName+".Reminding you of "+filename+"in the vicinity of "+fullPlaceName;


                    String[] array_of_sentences = say.split("(?<=[.!?])\\s*");





                 ttsService=new Intent(WholeScreenAlarmActivity.this, SpeechService.class);
                ttsService.putExtra("SAY",say);
                //Log.e("TAG","BEFORE TTS begins");
                getApplicationContext().startService(ttsService);
            }

          // placeId=mPlace.getId();
           // placeLatLng=mPlace.getLatLng();
            final RippleBackground rippleBackground=(RippleBackground)findViewById(R.id.content);
            ImageView imageView=(ImageView)findViewById(R.id.centerImage);
            rippleBackground.startRippleAnimation();
            Button stopAlarm = (Button) findViewById(R.id.stopAlarm);
            stopAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View arg0)
                {
                   // mMediaPlayer.stop();
                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    Gson gson = new Gson();
// Removal of corresponding data in placeIdsANDFIle HashMap begins
                    if(!isNotGeoLocReminder)
                    {

                        editor.remove(filename+placeId+"exists");
                        editor.apply();

                        double longitud=Double.parseDouble(getIntent().getStringExtra(filename+"&lon$"));
                        double lati=Double.parseDouble(getIntent().getStringExtra(filename+"&lat$"));
                        placeLatLng=new LatLng(lati,longitud);
                        PLACESidsANDFILE = new HashMap<String, ArrayList<String>>();
                        String nx = sharedpreferences.getString("PlaceIdHashMap", "");
                        if (nx.length() != 0) {
                            Gson gson1 = new Gson();

                            HashMapWrapper2 wrapper1 = gson1.fromJson(nx, HashMapWrapper2.class);
                            PLACESidsANDFILE = wrapper1.getMyMap();

                        }
                        ArrayList<String> placeIDs = new ArrayList<String>();
                        placeIDs = PLACESidsANDFILE.get(filename);

                        Iterator<String> iter = placeIDs.iterator();
                        while (iter.hasNext()) {
                            String str = iter.next();

                            if (str.equals(placeId))
                                iter.remove();
                        }
                      /*  for (String s : placeIDs)
                        {
                            if (s.equals(placeId))
                                placeIDs.remove(s);
                        }*/


                        PLACESidsANDFILE.put(filename, placeIDs);

                        HashMapWrapper2 wrapper1 = new HashMapWrapper2();

                        wrapper1.setMyMap(PLACESidsANDFILE);

                        String serializedMap = gson.toJson(wrapper1);

                        SharedPreferences.Editor editor1 = sharedpreferences.edit();
                        editor1.putString("PlaceIdHashMap", serializedMap);
// Removal of corresponding data in placeIdsANDFIle HashMap ends


                        //Removal of corresponding data in GEOfenceData begins HashMap ends

                        String nb = sharedpreferences.getString("GEOfenceData", "");
                        if (nb.length() != 0) {
                            HashMapWrapper wrapper = gson.fromJson(nb, HashMapWrapper.class);
                            Constants.BAY_AREA_LANDMARKS = wrapper.getMyMap();
                        }
                        ArrayList<LatLng> lonlat = new ArrayList<LatLng>();
                        if (Constants.BAY_AREA_LANDMARKS.get(filename) != null)
                            lonlat = Constants.BAY_AREA_LANDMARKS.get(filename);
                        Log.e("TAG", "placeLatLng latitude is " + placeLatLng.latitude);
                        Log.e("TAG", "placeLatLng longitude is " + placeLatLng.longitude);


                        Iterator<LatLng> iter1 = lonlat.iterator();
                        while (iter1.hasNext()) {
                            LatLng str = iter1.next();

                            if (str.equals(placeLatLng))
                                iter1.remove();
                        }
                      /*  for (LatLng x : lonlat)
                        {
                            Log.e("TAG", "x latitude is " + x.latitude);
                            Log.e("TAG", "x longitude is " + x.longitude);
                            if (x.equals(placeLatLng))
                            {
                                Log.e("TAG", "LATLONG MATCH");
                                lonlat.remove(x);
                            }
                        }*/

                        Constants.BAY_AREA_LANDMARKS.put(filename, lonlat);

                        HashMapWrapper wrapper12 = new HashMapWrapper();

                        wrapper12.setMyMap(Constants.BAY_AREA_LANDMARKS);

                        String saz = gson.toJson(wrapper12);

                        editor1.putString("GEOfenceData", saz);
                        editor1.apply();
                    }
                    if(alarmTone==1)
                    {
                        vib.cancel();
                    }
                    else if(alarmTone==2 || alarmTone==4)
                    {
                    //    mMediaPlayer.stop();
                      //  mMediaPlayer.release();
                       // mMediaPlayer = null;
                        getApplicationContext().stopService(musicIntent);
                    }
                    else if(alarmTone==3)
                    {
                        getApplicationContext().stopService(ttsIntent);


                    }
                    else if(alarmTone==5)
                    {
                     getApplicationContext().stopService(ttsService);
                       // engine.stop();
                        //engine.shutdown();

                    }
                    finish();
                }
            });
            seeNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent;
                    if(!isNotGeoLocReminder)
                    {
                        SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = sharedpreferences.edit();
                        Gson gson = new Gson();
                        editor.remove(filename+placeId+"exists");
                        editor.remove(filename+placeId+"beenAddedBefore");

                        editor.apply();

                        double longitud=Double.parseDouble(getIntent().getStringExtra(filename+"&lon$"));
                        double lati=Double.parseDouble(getIntent().getStringExtra(filename+"&lat$"));
                        placeLatLng=new LatLng(lati,longitud);
                        PLACESidsANDFILE = new HashMap<String, ArrayList<String>>();
                        String nx = sharedpreferences.getString("PlaceIdHashMap", "");
                        if (nx.length() != 0) {
                            Gson gson1 = new Gson();

                            HashMapWrapper2 wrapper1 = gson1.fromJson(nx, HashMapWrapper2.class);
                            PLACESidsANDFILE = wrapper1.getMyMap();

                        }
                        ArrayList<String> placeIDs = new ArrayList<String>();
                        placeIDs = PLACESidsANDFILE.get(filename);

                        Iterator<String> iter = placeIDs.iterator();
                        while (iter.hasNext()) {
                            String str = iter.next();

                            if (str.equals(placeId))
                                iter.remove();
                        }
                      /*  for (String s : placeIDs)
                        {
                            if (s.equals(placeId))
                                placeIDs.remove(s);
                        }*/


                        PLACESidsANDFILE.put(filename, placeIDs);

                        HashMapWrapper2 wrapper1 = new HashMapWrapper2();

                        wrapper1.setMyMap(PLACESidsANDFILE);

                        String serializedMap = gson.toJson(wrapper1);

                        SharedPreferences.Editor editor1 = sharedpreferences.edit();
                        editor1.putString("PlaceIdHashMap", serializedMap);
// Removal of corresponding data in placeIdsANDFIle HashMap ends


                        //Removal of corresponding data in GEOfenceData begins HashMap ends

                        String nb = sharedpreferences.getString("GEOfenceData", "");
                        if (nb.length() != 0) {
                            HashMapWrapper wrapper = gson.fromJson(nb, HashMapWrapper.class);
                            Constants.BAY_AREA_LANDMARKS = wrapper.getMyMap();
                        }
                        ArrayList<LatLng> lonlat = new ArrayList<LatLng>();
                        if (Constants.BAY_AREA_LANDMARKS.get(filename) != null)
                            lonlat = Constants.BAY_AREA_LANDMARKS.get(filename);
                        Log.e("TAG", "placeLatLng latitude is " + placeLatLng.latitude);
                        Log.e("TAG", "placeLatLng longitude is " + placeLatLng.longitude);


                        Iterator<LatLng> iter1 = lonlat.iterator();
                        while (iter1.hasNext()) {
                            LatLng str = iter1.next();

                            if (str.equals(placeLatLng))
                                iter1.remove();
                        }
                      /*  for (LatLng x : lonlat)
                        {
                            Log.e("TAG", "x latitude is " + x.latitude);
                            Log.e("TAG", "x longitude is " + x.longitude);
                            if (x.equals(placeLatLng))
                            {
                                Log.e("TAG", "LATLONG MATCH");
                                lonlat.remove(x);
                            }
                        }*/

                        Constants.BAY_AREA_LANDMARKS.put(filename, lonlat);

                        HashMapWrapper wrapper12 = new HashMapWrapper();

                        wrapper12.setMyMap(Constants.BAY_AREA_LANDMARKS);

                        String saz = gson.toJson(wrapper12);

                        editor1.putString("GEOfenceData", saz);
                        editor1.apply();
                    }

                    if(alarmTone==1)
                    {
                        vib.cancel();
                    }
                    else if(alarmTone==2 || alarmTone==4)
                    {
                        //    mMediaPlayer.stop();
                        //  mMediaPlayer.release();
                        // mMediaPlayer = null;
                        getApplicationContext().stopService(musicIntent);
                    }
                    else if(alarmTone==3)
                    {
                        getApplicationContext().stopService(ttsIntent);


                    }
                    else if(alarmTone==5)
                    {
                        getApplicationContext().stopService(ttsService);
                        // engine.stop();
                        //engine.shutdown();

                    }
                    SharedPreferences sharedpreferences = getApplicationContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                    if(!isNotGeoLocReminder)
                    position=sharedpreferences.getInt(filename+placeId+"position",-1);
                    if(noteType==1)
                    { intent=new Intent(WholeScreenAlarmActivity.this,NoteActivity.class);
                    intent.putExtra("filename",filename+".txt");}
                    else if(noteType==2)
                    {   intent=new Intent(WholeScreenAlarmActivity.this,MainActivity.class);
                    intent.putExtra("filename",filename+".txt");}
                       else
                    {
                        intent=new Intent(WholeScreenAlarmActivity.this,PlayerActivity.class);
                        intent.putExtra("filename",filename+".3gp");
                    }

                    intent.putExtra("position",position);
                    //mMediaPlayer.stop();
                    //mMediaPlayer.release();
                    //mMediaPlayer=null;
                    try {
                        if (musicIntent != null)
                            getApplicationContext().stopService(musicIntent);
                    }
                    catch(NullPointerException e){

                    }
                    if(!isNotGeoLocReminder) {
                        SharedPreferences.Editor editor = sharedpreferences.edit();

                        editor.remove(filename + placeId + "exists");
                        editor.apply();
                    }


                    startActivity(intent);
                    finish();

                }
            });


           // playSound(this, getAlarmUri());
        }


    @Override
    public void onBackPressed()
    {

    }



        //Get an alarm sound. Try for an alarm. If none set, try notification,
        //Otherwise, ringtone.
        private Uri getAlarmUri() {
            Uri alert = RingtoneManager
                    .getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alert == null) {
                alert = RingtoneManager
                        .getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (alert == null) {
                    alert = RingtoneManager
                            .getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
            return alert;
        }






   /* private void playSound(Context context, Uri alert)
    {
        mMediaPlayer = new MediaPlayer();
        try
        {
            mMediaPlayer.setDataSource(context, alert);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            if(audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0)
            {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.prepare();
                mMediaPlayer.setLooping(true);
                mMediaPlayer.start();
            }
        }
        catch (IOException e)
        {
            System.out.println("OOPS");
        }
    }*/

    }

