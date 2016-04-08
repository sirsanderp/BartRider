package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.structure.Departure;
import com.sanderp.bartrider.utility.BartApiConnection;
import com.sanderp.bartrider.xmlparser.BartDepartureParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of the AsyncTask to download data from the BART Schedule API.
 */
public class BartDepartureSyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "BartDepartureSyncTask";

    private AsyncTaskResponse delegate;
    private List<Departure> departures;

    public BartDepartureSyncTask(AsyncTaskResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... bartUrl) {
        try {
            return refreshDepartures(bartUrl[0]);
        } catch (IOException e) {
            return "Failed to refresh";
        } catch (XmlPullParserException e) {
            return "XML parser failed";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(departures);
    }

    /**
     * Creates the stream for AsyncTask
     */
    private String refreshDepartures(String bartUrl) throws XmlPullParserException, IOException {
        InputStream stream = null;
        BartDepartureParser parser = new BartDepartureParser();

        Log.i(TAG, "Refreshing departure info...");
        try {
            stream = BartApiConnection.downloadData(bartUrl);
            departures = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return null;
    }
}
