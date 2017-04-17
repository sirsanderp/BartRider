package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sanderp.bartrider.structure.TripEstimate;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;
import com.sanderp.bartrider.xmlparser.RealTimeParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RealTimeService extends IntentService {
    private static final String TAG = "RealTimeService";

    public static final String ORIG = "origin";
    public static final String HEAD = "trainHeadStation";
    public static final String RESULT = "result";

    public RealTimeService() {
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
                List<TripEstimate> tripEstimates = getRealTimeEstimates(intent.getStringExtra(ORIG), intent.getStringExtra(HEAD));
                Intent localIntent = new Intent(Constants.Broadcast.REAL_TIME_SERVICE)
                        .putExtra(RESULT, (Serializable) tripEstimates);
                Log.d(TAG, "Sending broadcast from service.");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } catch (XmlPullParserException e) {
                Log.d(TAG, "Failed to refresh");
            } catch (IOException e) {
                Log.d(TAG, "XML parser failed");
            }
        }
    }

    private List<TripEstimate> getRealTimeEstimates(String origAbbr, String trainHeadStation) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RealTimeParser estimates = new RealTimeParser();

        Log.i(TAG, "Parsing real-time estimates...");
        String url = Constants.Api.URL + "etd.aspx?cmd=etd"
                + "&orig=" + origAbbr
                + "&key=" + Constants.Api.KEY;
        try {
            stream = Utils.getUrlStream(url);
            return estimates.parse(stream, trainHeadStation);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}