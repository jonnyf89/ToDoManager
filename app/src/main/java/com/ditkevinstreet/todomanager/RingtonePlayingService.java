package com.ditkevinstreet.todomanager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationBuilderWithBuilderAccessor;
import android.util.Log;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * Created by Admin on 21/11/2017.
 */
//TODO Additional Functionality 2.2  - Alarm and notification
public class RingtonePlayingService extends Service {
    private static final String TAG = "Lab-UserInterface";

    MediaPlayer media_song;
    int startId;
    private boolean isRunning;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "logMessage onStartCommand: Entered onStart in RingtonePlayingService");

        //fetch the extra string values
        String state = intent.getExtras().getString("extra");

        Log.d(TAG, "logMessage Ringtone state: extra is " + state);

        //notification
            //set up notification service
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            //set up an intent which goes to ToDoManagerActivity(main)
        Intent intentMain = new Intent(this.getApplicationContext(), ToDoManagerActivity.class);
            //set up a pending Intent
        PendingIntent pendingIntentMain = PendingIntent.getActivity(this, 0, intentMain, 0);


            //make notification parameters
        Notification notificationPopup = new Notification.Builder(this)
                .setContentTitle("Alarm is ringing")
                .setContentText("Click me to silence")
                .setContentIntent(pendingIntentMain)
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.happy_background)
                .build();

         //set up the notification call command
        notificationManager.notify(0, notificationPopup);

        //convert extra strings from intent to start ids, 0 or 1
        assert state != null;
        switch (state) {
            case "alarm on":
                startId = 1;
                break;
            case "alarm off":
                startId = 0;
                break;
            default:
                startId = 0;
                break;
        }

        //if alarm is not sounding, and alarm gets set
        if(!this.isRunning && startId == 1) {
            Log.d(TAG, "logMessage: alarm set");

            //create media player
            media_song = MediaPlayer.create(this, R.raw.twilight_piano);
            media_song.start();

            this.isRunning = true;
            this.startId = 0;//do we need this?

        }
        //if alarm is sounding and user presses stop alarm
        else if (this.isRunning && startId == 0) {
            Log.d(TAG, "logMessage : alarm silenced");
            //stop the ringtone
            media_song.stop();
            media_song.reset();

            this.isRunning = false;
            this.startId = 0;
        
        }
        //if user presses stop alarm but alarm isnt sounding
        else if (!this.isRunning && startId ==0) {
            Log.d(TAG, "logMessage : alarm silenced but isnt sounding");

            this.isRunning = false;
            this.startId = 0;

        }
        //if alarm is sounding while an alarm is set
        else if (this.isRunning && startId == 1) {
            Log.d(TAG, "logMessage: alarm set(while another sounds)");
            //create media player
            media_song = MediaPlayer.create(this, R.raw.twilight_piano);
            media_song.start();

            this.isRunning = true;
            this.startId = 0;//do we need this?

        }
        //just in case
        else{
            Log.d(TAG, "logMessage : reached last else somehow");
        }


        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        //not sure about this toast, or this method, come back to it
        Toast.makeText(this, "logMessage : on Destroy called", Toast.LENGTH_LONG).show();
        Log.d(TAG, "logMessage : onDestroy: Entered on Destroy in RingtonePlayingService");
    }

}
