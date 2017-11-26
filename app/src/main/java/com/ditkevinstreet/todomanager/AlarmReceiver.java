package com.ditkevinstreet.todomanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Admin on 21/11/2017.
 */

//TODO Additional Functionality 2.2  - Alarm

public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "Lab-UserInterface";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "logMessage : onReceive: Entered alarm receiver");

        //fetch extra strings from the intent
        String state = intent.getExtras().getString("extra");
        Log.e(TAG, "logMessage : In alarm receiver, state is: " + state);

        //create intent to ringtone service
        Intent serviceIntent = new Intent(context, RingtonePlayingService.class);

        //pass extra string from toDoManagerActivity to the RingtonePlayingService
        serviceIntent.putExtra("extra", state);

        //start ringtone service
        context.startService(serviceIntent);

    }
}
