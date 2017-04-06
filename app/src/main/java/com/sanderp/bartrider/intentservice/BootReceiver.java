package com.sanderp.bartrider.intentservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = "BootReceiver";

    public static final long DEFAULT_INTERVAL = 5;

    @Override
    public void onReceive(Context context, Intent intent) {
//        Log.d(TAG, "onReceive()");
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent advisoryIntent = new Intent(context, AdvisoryUpdateService.class);
        PendingIntent advisoryUpdate = PendingIntent.getService(context, -1, advisoryIntent, PendingIntent.FLAG_UPDATE_CURRENT);

//        Log.i(TAG, "Starting advisory update repeating alarm...");
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), DEFAULT_INTERVAL * 1000, advisoryUpdate);
    }
}
