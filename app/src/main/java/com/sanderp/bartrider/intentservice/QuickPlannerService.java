package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sanderp.bartrider.structure.Trip;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;
import com.sanderp.bartrider.xmlparser.QuickPlannerParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class QuickPlannerService extends IntentService {
    private static final String TAG = "QuickPlannerService";

    public static final String ORIG = "origin";
    public static final String DEST = "destination";
    public static final String RESULT = "result";

    public QuickPlannerService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                List<Trip> tripSchedules = getSchedule(intent.getStringExtra(ORIG), intent.getStringExtra(DEST));
                Intent localIntent = new Intent(Constants.Broadcast.QUICK_PLANNER_SERVICE)
                        .putExtra(RESULT, (Serializable) tripSchedules);
                Log.d(TAG, "Sending broadcast from service.");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } catch (XmlPullParserException e) {
                Log.d(TAG, "Failed to refresh");
            } catch (IOException e) {
                Log.d(TAG, "XML parser failed");
            }
        }
    }

    private List<Trip> getSchedule(String origAbbr, String destAbbr) throws XmlPullParserException, IOException {
        InputStream stream = null;
        QuickPlannerParser planner = new QuickPlannerParser();
        String url = Constants.Api.URL + "sched.aspx?cmd=depart"
                + "&orig=" + origAbbr
                + "&dest=" + destAbbr
                + "&a=3&b=0"
                + "&key=" + Constants.Api.KEY;
        try {
            Log.i(TAG, "Parsing trip schedules...");
            stream = Utils.getUrlStream(url);
            return planner.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}