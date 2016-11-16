package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.database.ApiContract;
import com.sanderp.bartrider.structure.Trip;
import com.sanderp.bartrider.structure.TripEstimate;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.QuickPlannerParser;
import com.sanderp.bartrider.xmlparser.RealTimeEstimateParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
            getTrips(params);
            getTripEstimates(params);
            return null;
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
    private void getTrips(String... params) throws XmlPullParserException, IOException {
        InputStream stream = null;
        QuickPlannerParser planner = new QuickPlannerParser();
        String plannerUrl = ApiContract.API_URL + "sched.aspx?cmd=depart&orig=" + params[0] +
                "&dest=" + params[1] + "&a=3&b=0&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Refreshing departure info...");
            stream = ApiConnection.downloadData(plannerUrl);
            trips = planner.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private void getTripEstimates(String... params) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RealTimeEstimateParser estimates = new RealTimeEstimateParser();
        String estimatesUrl = ApiContract.API_URL + "etd.aspx?cmd=etd&orig=" + params[0] +
                "&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Refreshing estimates info...");
            stream = ApiConnection.downloadData(estimatesUrl);
            tripEstimates = estimates.parse(stream, trips.get(0).getTripLegs().get(0).getTrainHeadStation());
            if (tripEstimates == null) Log.i(TAG, "Using quick planner time table");
            else updateTrips();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private void updateTrips() {
        DateFormat df = new SimpleDateFormat("h:mm a");
        Date date = new Date();
        Log.d(TAG, df.format(date));
        for (int i = 0; i < trips.size(); i++) {
            Trip t = trips.get(i);
            int minutesUntilTrain = tripEstimates.get(i).getMinutes();
            int tripMinutes = t.getTripTime();

            try {
                Date origTimeMin = df.parse(t.getOrigTimeMin());
                Date origEstArrival = new Date(date.getTime() + minutesUntilTrain * 60 * 1000L);
                Date destEstArrival = new Date(origEstArrival.getTime() + tripMinutes * 60 * 1000L);
                long diff = origEstArrival.getTime() - origTimeMin.getTime();
                long diffMinutes = diff / (60 * 1000) % 60;
                Log.d(TAG, "Planned: " + origTimeMin + " | " + diffMinutes + "min");
                if (diffMinutes >= 1) {
                    t.setOrigTimeMin(df.format(origEstArrival));
                    t.setDestTimeMin(df.format(destEstArrival));
                }
                Log.d(TAG, "Origin: " + minutesUntilTrain + "min | " + df.format(origEstArrival));
                Log.d(TAG, "Destination: " + minutesUntilTrain + "min | " + df.format(destEstArrival));
            } catch (ParseException e) {
                Log.e(TAG, "Invalid date was entered");
            }
        }
    }
}
