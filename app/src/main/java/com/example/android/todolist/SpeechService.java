package com.example.android.todolist;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;

import java.util.Locale;

/**
 * Created by Dell on 5/23/2016.
 */
public class SpeechService extends Service implements TextToSpeech.OnInitListener {



    private TextToSpeech tts;
    private String word, meaning;
    private boolean isInit;
    private Handler handler;
    String text;

    @Override
    public void onCreate()
    {
        super.onCreate();
        tts = new TextToSpeech(getApplicationContext(), this);
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.removeCallbacksAndMessages(null);
        text = intent.getStringExtra("SAY");
        //word = intent.getStringExtra(SpeechService.EXTRA_WORD);
       // meaning = intent.getStringExtra(SpeechService.EXTRA_MEANING);

        if (isInit) {
            speak();
        }

        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                stopSelf();
            }
        }, 15*1000);

        return SpeechService.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        if (tts != null)
        {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                speak();
                isInit = true;
            }
        }
    }

    private void speak()
    {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            //tts.speak(meaning, TextToSpeech.QUEUE_ADD, null);
        }
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}