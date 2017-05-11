package com.sanderp.bartrider.intentservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.sanderp.bartrider.pojo.realtimeetd.RealTimeEtdPojo;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;

import java.io.IOException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class RealTimeEtdService extends IntentService {
    private static final String TAG = "RealTimeEtdService";

    public static final String ORIG = "origin";
    public static final String HEAD = "head";
    public static final String RESULT = "result";

    private static DynamoDBMapper mapper;

    public RealTimeEtdService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mapper = new DynamoDBMapper(Utils.getDynamoDbClient(this));
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            try {
                RealTimeEtdPojo pojo = getRealTimeEtd(intent.getStringExtra(ORIG), intent.getStringExtra(HEAD));
                Intent localIntent = new Intent(Constants.Broadcast.REAL_TIME_ETD_SERVICE)
                        .putExtra(RESULT, pojo);
                Log.d(TAG, "Sending broadcast from service.");
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
            } catch (IOException e) {
                Log.d(TAG, "Input stream failed.");
                e.printStackTrace();
            }
        }
    }

    private RealTimeEtdPojo getRealTimeEtd(String origAbbr, String headAbbr) throws IOException {
        Log.i(TAG, "Getting real-time etd...");
        Log.d(TAG, origAbbr + " -> " + headAbbr);
        return mapper.load(RealTimeEtdPojo.class, origAbbr, headAbbr);
    }
}
