package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.database.ApiContract;
import com.sanderp.bartrider.structure.Trip;
import com.sanderp.bartrider.structure.TripEstimate;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.QuickPlannerParser;
import com.sanderp.bartrider.xmlparser.RealTimeParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of the AsyncTask to download data from the BART Schedule API.
 */
public class QuickPlannerAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "QuickPlannerAsyncTask";

    private AsyncTaskResponse delegate;
    private List<Trip> trips;
    private List<TripEstimate> tripEstimates;

    public QuickPlannerAsyncTask(AsyncTaskResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return refreshDepartures(params);
        } catch (IOException e) {
            return "Failed to refresh";
        } catch (XmlPullParserException e) {
            return "XML parser failed";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(trips);
    }

    /**
     * Creates the stream for AsyncTask
     */
    private String refreshDepartures(String... params) throws XmlPullParserException, IOException {
        InputStream stream = null;
        QuickPlannerParser planner = new QuickPlannerParser();
        RealTimeParser estimates = new RealTimeParser();

        String plannerUrl = ApiContract.API_URL + "sched.aspx?cmd=depart&orig=" + params[0] +
                "&dest=" + params[1] + "&a=3&b=0&key=" + ApiContract.API_KEY;
        String estimatesUrl = ApiContract.API_URL + "etd.aspx?cmd=etd&orig=" + params[0] +
                "&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Refreshing departure info...");
            stream = ApiConnection.downloadData(plannerUrl);
            trips = planner.parse(stream);
            Log.i(TAG, "Refreshing estimates info...");
            stream = ApiConnection.downloadData(estimatesUrl);
            tripEstimates = estimates.parse(stream, trips.get(0).getTripLegs().get(0).getTrainHeadStation());
            if (tripEstimates == null) Log.i(TAG, "Using quick planner time table");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return null;
    }
}
