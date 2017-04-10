package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.TripOverviewActivity;
import com.sanderp.bartrider.adapter.TripAdapter;
import com.sanderp.bartrider.structure.Trip;
import com.sanderp.bartrider.structure.TripEstimate;
import com.sanderp.bartrider.utility.ApiContract;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.QuickPlannerParser;
import com.sanderp.bartrider.xmlparser.RealTimeParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of the AsyncTask to download real-time data from the BART Station API.
 */
public class RealTimeAsyncTask extends AsyncTask<String, Void, List<TripEstimate>> {
    private static final String TAG = "RealTimeAsyncTask";

    private AsyncTaskResponse delegate;

    public RealTimeAsyncTask(AsyncTaskResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<TripEstimate> doInBackground(String... params) {
        try {
            List<Trip> trips = getSchedule(params);
            getRealTimeEstimates(trips);
        } catch (IOException e) {
            Log.d(TAG, "Failed to refresh");
        } catch (XmlPullParserException e) {
            Log.d(TAG, "XML parser failed");
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<TripEstimate> result) {
        delegate.processFinish(result);
    }

    private List<Trip> getSchedule(String... params) throws XmlPullParserException, IOException {
        InputStream stream = null;
        QuickPlannerParser planner = new QuickPlannerParser();
        String plannerUrl = ApiContract.API_URL + "sched.aspx?cmd=depart"
                + "&orig=" + params[0]
                + "&dest=" + params[1]
                + "&a=3&b=0"
                + "&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Parsing trip schedule...");
            stream = ApiConnection.downloadData(plannerUrl);
            return planner.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private List<TripEstimate> getRealTimeEstimates(List<Trip> trips) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RealTimeParser estimates = new RealTimeParser();

        Log.i(TAG, "Parsing real-time estimates...");
        for (Trip.TripLeg tripLeg: trips.get(0).getTripLegs()) {
            String url = ApiContract.API_URL + "etd.aspx?cmd=etd"
                    + "&orig=" + tripLeg.getOrigin()
                    + "&key=" + ApiContract.API_KEY;
            try {
                stream = ApiConnection.downloadData(url);
                return estimates.parse(stream, tripLeg.getTrainHeadStation());
            } finally {
                if (stream != null) {
                    stream.close();
                }
            }
        }
        return null;
    }
}
