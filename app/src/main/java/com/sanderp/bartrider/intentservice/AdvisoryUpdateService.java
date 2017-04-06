package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.utility.ApiContract;
import com.sanderp.bartrider.xmlparser.AdvisoryParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class AdvisoryUpdateService extends IntentService {
    private static final String TAG = "AdvisoryUpdateService";

    public static final int NOTIFICATION_ID = 101;

    private List<String> advisories;
    private NotificationManager mNotificationManager;

    public AdvisoryUpdateService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                getAdvisories();
            } catch (XmlPullParserException e) {
                Log.d(TAG, "Failed to refresh");
            } catch (IOException e) {
                Log.d(TAG, "XML parser failed");
            }
        }
    }

    private void getAdvisories() throws XmlPullParserException, IOException {
        InputStream stream = null;
        AdvisoryParser parser = new AdvisoryParser();
        String url = ApiContract.API_URL + "bsa.aspx?cmd=bsa&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Parsing advisories...");
            stream = ApiConnection.downloadData(url);
            advisories = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        StringBuilder advisory = new StringBuilder();
        for (String s : advisories) {
            advisory.append(s + "\n\n");
        }
        postAdvisoryNotification(advisory.toString().trim());
    }

    private void postAdvisoryNotification(String advisory) {
        if (!advisory.equals("No delays reported.")) {
            Notification notification = new NotificationCompat.Builder(this)
                    .setContentTitle("BART Advisory")
                    .setContentText(advisory)
                    .setSmallIcon(R.drawable.ic_bart_rider_24dp)
                    .build();
            mNotificationManager.notify(NOTIFICATION_ID, notification);
        }
    }
}
