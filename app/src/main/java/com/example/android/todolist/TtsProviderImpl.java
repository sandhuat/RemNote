package com.example.android.todolist;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by Dell on 5/20/2016.
 */
public class TtsProviderImpl extends TtsProviderFactory implements TextToSpeech.OnInitListener {

    private TextToSpeech tts;
    Context mContext;

    public void init(Context context) {
       // if (tts == null) {
            tts = new TextToSpeech(context, this);
        int result = tts.setLanguage(Locale.US);

        mContext=context;
        //}
    }

    @Override
    public void say(String sayThis)
    {
        AudioManager am = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        int amStreamMusicMaxVol = am.getStreamMaxVolume(am.STREAM_MUSIC);
        am.setStreamVolume(am.STREAM_MUSIC, amStreamMusicMaxVol, 0);
        Log.e("TAG","in Tts provider say is "+sayThis);
        String[] array_of_sentences = sayThis.split("(?<=[.!?])\\s*");
        for (String s : array_of_sentences) {
            //System.out.println(s);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                tts.speak(s, TextToSpeech.QUEUE_ADD, null, null);
                tts.playSilentUtterance(5000, TextToSpeech.QUEUE_ADD, null);

            }
            else
            {

                tts.speak(s, TextToSpeech.QUEUE_ADD, null);
                tts.playSilence(5000, TextToSpeech.QUEUE_ADD, null);

            }

        }
        shutdown();
    }

    @Override
    public void onInit(int status) {
        Locale loc = new Locale("en", "", "");

        if (tts.isLanguageAvailable(loc) >= TextToSpeech.LANG_AVAILABLE) {
            tts.setLanguage(loc);
        }
        if (status == TextToSpeech.SUCCESS) {
            Log.e("TAG","SUCESS IN TTS");
        }
    }

    public void shutdown() {
        tts.shutdown();
    }}
