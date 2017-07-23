package com.sanderp.bartrider.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanderp.bartrider.R;
import com.sanderp.bartrider.pojo.quickplanner.QuickPlannerPojo;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Asynchronously requests quick planner departure data from the BART API for the specified trip.
 */
public class QuickPlannerService extends IntentService {
    private static final String TAG = "QuickPlannerService";

    public static final String ORIG = "origin";
    public static final String DEST = "destination";
    public static final String RESULT = "result";

    private static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

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
            QuickPlannerPojo pojo = null;
            try {
                pojo = getSchedule(intent.getStringExtra(ORIG), intent.getStringExtra(DEST));
            } catch (IOException e) {
                Log.e(TAG, "Input stream failed.");
                e.printStackTrace();
            } finally {
                Intent localIntent = new Intent(Constants.Broadcast.QUICK_PLANNER_SERVICE)
                        .putExtra(RESULT, pojo);
                Log.i(TAG, "Sending broadcast...");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            }
        }
    }

    private QuickPlannerPojo getSchedule(String origAbbr, String destAbbr) throws IOException {
        InputStream stream = null;
        String url = Constants.Bart.API_URL + "sched.aspx?cmd=depart"
                + "&orig=" + origAbbr
                + "&dest=" + destAbbr
                + "&a=4&b=2"
                + "&key=" + getResources().getString(R.string.bartApiKey)
                + "&json=y";
        try {
            Log.i(TAG, "Getting trip schedules...");
            stream = Utils.getUrlStream(url);
            return mapper.readValue(stream, QuickPlannerPojo.class);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}