package com.example.android.todolist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by Dell on 3/30/2016.
 */

public class MediaButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
        /* handle media button intent here by reading contents */
        /* of EXTRA_KEY_EVENT to know which key was pressed    */

            KeyEvent ke = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (ke.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Toast.makeText(context, "BUTTON PRESSED!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}