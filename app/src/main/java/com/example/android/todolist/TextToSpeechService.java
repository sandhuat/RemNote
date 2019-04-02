package com.example.android.todolist;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Dell on 5/6/2016.
 */
public  class TextToSpeechService extends Service implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {


    public static TextToSpeech mTts;
    private String spokenText;

    @Override
    public void onCreate() {
        mTts = new TextToSpeech(this, this);
        super.onCreate();
        // mTts.setSpeechRate((float) 0.8);
        // This is a good place to set spokenText
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = mTts.setLanguage(Locale.UK);
                     Log.e("TAG","SUCCESS 1");
            AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
            am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TAG", "Language is not available.");
            }
            else {

                    spokenText+=".."+spokenText;

                mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
                stopSelf();
               /* String[] array_of_sentences = spokenText.split("(?<=[.!?])\\s*");
                for (String s : array_of_sentences) {
                    //System.out.println(s);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        mTts.speak(s, TextToSpeech.QUEUE_ADD, null, null);
                        mTts.playSilentUtterance(5000, TextToSpeech.QUEUE_ADD, null);

                    } else {

                        mTts.speak(s, TextToSpeech.QUEUE_ADD, null);
                        mTts.playSilence(5000, TextToSpeech.QUEUE_ADD, null);

                    }

                }
                stopSelf();*/
            }

        }
        else
        {
            Log.e("TAG", "Could not initialize TextToSpeech.");
        }
    }


    @Override
    public void onUtteranceCompleted(String uttId) {
        stopSelf();
    }

    @Override
    public void onDestroy() {
        if (mTts != null) {
            mTts.stop();
            mTts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        spokenText = intent.getStringExtra("SAY");
        Log.e("TAG", "Spoken Text is " + spokenText);

        Context context = getApplicationContext();
      /*  TtsProviderFactory ttsProviderImpl = TtsProviderFactory.getInstance();
        if (ttsProviderImpl != null) {
            ttsProviderImpl.init(context);
            ttsProviderImpl.say(spokenText);


            */




        return START_STICKY;


    }
    @Override
    public void onStart(Intent intent, int startId) {


        spokenText = intent.getStringExtra("SAY");

        mTts.speak(spokenText, TextToSpeech.QUEUE_FLUSH, null);
        stopSelf();

        Log.e("TAg", "onstart_service");
        super.onStart(intent, startId);
    }
}
