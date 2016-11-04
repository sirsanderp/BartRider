package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.structure.Departure;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.QuickPlannerDepartureParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of the AsyncTask to download data from the BART Schedule API.
 */
public class QuickPlannerDepartureAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "QuickPlannerDepartureAsyncTask";

    private AsyncTaskResponse delegate;
    private List<Departure> departures;

    public QuickPlannerDepartureAsyncTask(AsyncTaskResponse delegate) {
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
        QuickPlannerDepartureParser parser = new QuickPlannerDepartureParser();

        Log.i(TAG, "Refreshing departure info...");
        try {
            stream = ApiConnection.downloadData(bartUrl);
            departures = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return null;
    }
}
