package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanderp.bartrider.R;
import com.sanderp.bartrider.pojo.advisory.AdvisoryPojo;
import com.sanderp.bartrider.pojo.advisory.Bsa;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.PrefContract;
import com.sanderp.bartrider.utility.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AdvisoryService extends IntentService {
    private static final String TAG = "AdvisoryService";

    public static final int NOTIFICATION_ID = 101;

    private NotificationManager mNotificationManager;
    private SharedPreferences sharedPrefs;

    private static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true)
                .setDateFormat(new SimpleDateFormat("EEE MMM dd yyyy KK:mm a zzz", Locale.US));
    }

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
                postAdvisoryNotification(advisoryText(getAdvisories()));
            } catch (IOException e) {
                Log.d(TAG, "Input stream failed.");
                e.printStackTrace();
            }
        }
    }

    private AdvisoryPojo getAdvisories() throws IOException {
        InputStream stream = null;
        String url = Constants.Api.URL + "bsa.aspx?cmd=bsa"
                + "&key=" + Constants.Api.KEY
                + "&json=y";
        try {
            Log.i(TAG, "Getting advisories...");
            Log.d(TAG, mapper.toString());
            stream = Utils.getUrlStream(url);
            return mapper.readValue(stream, AdvisoryPojo.class);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private String advisoryText(AdvisoryPojo pojo) {
        StringBuilder advisory = new StringBuilder();
//        advisory.append("As of " + pojo.getRoot().getBsa().get(0).getPosted() + "\n\n");
        for (Bsa bsa : pojo.getRoot().getBsa()) {
            advisory.append(bsa.getDescription().getCdataSection() + "\n\n");
        }
        return advisory.toString().trim();
    }

    private void postAdvisoryNotification(String advisory) {
        String defaultAdvisory = getResources().getString(R.string.default_advisory);
        String prevAdvisory = sharedPrefs.getString(PrefContract.PREV_ADVISORY, defaultAdvisory);
        Log.d(TAG, advisory + " | " + prevAdvisory);
        if (!advisory.equals(prevAdvisory) && !advisory.equals(defaultAdvisory)) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("BART Advisory")
                    .setContentText(advisory)
                    .setSmallIcon(R.drawable.ic_bart_rider_24dp)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(advisory))
                    .build();
            mNotificationManager.notify(NOTIFICATION_ID, notification);
            sharedPrefs.edit().putString(PrefContract.PREV_ADVISORY, advisory).apply();
        }
    }
}
