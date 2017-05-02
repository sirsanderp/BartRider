package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanderp.bartrider.pojo.realtime.RealTimePojo;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RealTimeService extends IntentService {
    private static final String TAG = "RealTimeService";

    public static final String ORIG = "origin";
    public static final String RESULT = "result";

    private static final ObjectMapper mapper;
    static {
        mapper = new ObjectMapper()
                .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
    }

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
                RealTimePojo pojo = getRealTimeEstimates(intent.getStringExtra(ORIG));
                Intent localIntent = new Intent(Constants.Broadcast.REAL_TIME_SERVICE)
                        .putExtra(RESULT, pojo);
                Log.d(TAG, "Sending broadcast from service.");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } catch (IOException e) {
                Log.d(TAG, "Input stream failed.");
                e.printStackTrace();
            }
        }
    }

    private RealTimePojo getRealTimeEstimates(String origAbbr) throws IOException {
        InputStream stream = null;
        String url = Constants.Api.URL + "etd.aspx?cmd=etd"
                + "&orig=" + origAbbr
                + "&key=" + Constants.Api.KEY
                + "&json=y";
        try {
            Log.i(TAG, "Getting real-time estimates...");
            stream = Utils.getUrlStream(url);
            return mapper.readValue(stream, RealTimePojo.class);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}