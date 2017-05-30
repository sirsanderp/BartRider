package com.sanderp.bartrider.intentservice;

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
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
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
            try {
                QuickPlannerPojo pojo = getSchedule(intent.getStringExtra(ORIG), intent.getStringExtra(DEST));
                Intent localIntent = new Intent(Constants.Broadcast.QUICK_PLANNER_SERVICE)
                        .putExtra(RESULT, pojo);
                Log.d(TAG, "Sending broadcast from service.");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } catch (IOException e) {
                Log.d(TAG, "Input stream failed.");
                e.printStackTrace();
            }
        }
    }

    private QuickPlannerPojo getSchedule(String origAbbr, String destAbbr) throws IOException {
        InputStream stream = null;
        String url = Constants.Bart.API_URL + "sched.aspx?cmd=depart"
                + "&orig=" + origAbbr
                + "&dest=" + destAbbr
                + "&a=3&b=2"
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