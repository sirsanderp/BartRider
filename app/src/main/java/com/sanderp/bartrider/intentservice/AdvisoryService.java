package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.PrefContract;
import com.sanderp.bartrider.utility.Utils;
import com.sanderp.bartrider.xmlparser.AdvisoryParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AdvisoryService extends IntentService {
    private static final String TAG = "AdvisoryService";

    public static final int NOTIFICATION_ID = 101;

    private NotificationManager mNotificationManager;
    private SharedPreferences sharedPrefs;

    public AdvisoryService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        sharedPrefs = getSharedPreferences(PrefContract.PREFS_NAME, 0);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                String result = advisoryText(getAdvisories());
                postAdvisoryNotification(result);
            } catch (XmlPullParserException e) {
                Log.d(TAG, "Failed to refresh");
            } catch (IOException e) {
                Log.d(TAG, "XML parser failed");
            }
        }
    }

    private List<String> getAdvisories() throws XmlPullParserException, IOException {
        InputStream stream = null;
        AdvisoryParser parser = new AdvisoryParser();
        String url = Constants.Api.URL + "bsa.aspx?cmd=bsa&key=" + Constants.Api.KEY;
        try {
            Log.i(TAG, "Parsing advisories...");
            stream = Utils.getUrlStream(url);
            return parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private String advisoryText(List<String> advisories) {
        StringBuilder advisory = new StringBuilder();
        for (String s : advisories) {
            advisory.append(s + "\n\n");
        }
        return advisory.toString().trim();
    }

    private void postAdvisoryNotification(String advisory) {
        String defaultAdvisory = getResources().getString(R.string.default_advisory);
        String prevAdvisory = sharedPrefs.getString(PrefContract.PREV_ADVISORY, defaultAdvisory);
        Log.d(TAG, advisory + " | " + prevAdvisory);
        if (!advisory.equals(prevAdvisory)) {
            String alertText = advisory;
            if (advisory.equals(defaultAdvisory)) alertText = "Delays have ended. Trains are running as scheduled.";
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("BART Advisory")
                    .setContentText(alertText)
                    .setSmallIcon(R.drawable.ic_bart_rider_24dp)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(alertText))
                    .build();
            mNotificationManager.notify(NOTIFICATION_ID, notification);
            sharedPrefs.edit().putString(PrefContract.PREV_ADVISORY, advisory).apply();
        }
    }
}
