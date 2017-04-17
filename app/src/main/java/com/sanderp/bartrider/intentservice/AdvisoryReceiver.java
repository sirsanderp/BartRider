package com.sanderp.bartrider.intentservice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

public class AdvisoryReceiver extends BroadcastReceiver {
    private static final String TAG = "AdvisoryReceiver";

    public static final long DEFAULT_INTERVAL = 5;

    @Override
    public void onReceive(Context context, Intent intent) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent advisoryIntent = new Intent(context, AdvisoryService.class);
        PendingIntent advisoryUpdate = PendingIntent.getService(context, -1, advisoryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(), DEFAULT_INTERVAL * 60 * 1000, advisoryUpdate);
    }
}
