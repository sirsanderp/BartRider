package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.utility.ApiContract;
import com.sanderp.bartrider.structure.Trip;
import com.sanderp.bartrider.structure.TripEstimate;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.QuickPlannerParser;
import com.sanderp.bartrider.xmlparser.RealTimeParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Implementation of the AsyncTask to download quick planner data from the BART Schedule API.
 */
public class QuickPlannerAsyncTask extends AsyncTask<String, Void, List<Trip>> {
    private static final String TAG = "QuickPlannerAsyncTask";

    private AsyncTaskResponse delegate;

    public QuickPlannerAsyncTask(AsyncTaskResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected List<Trip> doInBackground(String... params) {
        try {
            return getTrips(params);
        } catch (IOException e) {
            Log.d(TAG, "Failed to refresh");
        } catch (XmlPullParserException e) {
            Log.d(TAG, "XML parser failed");
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trip> result) {
        delegate.processFinish(result);
    }

    /**
     * Creates the stream for AsyncTask
     */
    private List<Trip> getTrips(String... params) throws XmlPullParserException, IOException {
        InputStream stream = null;
        QuickPlannerParser planner = new QuickPlannerParser();
        String plannerUrl = ApiContract.API_URL + "sched.aspx?cmd=depart&orig=" + params[0] +
                "&dest=" + params[1] + "&a=3&b=0&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Refreshing departure info...");
            stream = ApiConnection.downloadData(plannerUrl);
            return planner.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private void updateTrips(List<Trip> trips, List<TripEstimate> tripEstimates) {
        DateFormat df = new SimpleDateFormat("h:mm a");
        Date date = new Date();
        Log.d(TAG, df.format(date));
        for (int i = 0; i < trips.size(); i++) {
            Trip t = trips.get(i);
            int minutesUntilTrain = tripEstimates.get(i).getMinutes();
            int tripMinutes = t.getTripTime();
            try {
                Date origTime = df.parse(t.getOrigTimeMin());
                Date origEstArrival = new Date(date.getTime() + minutesUntilTrain * 60 * 1000L);
                Date destEstArrival = new Date(origEstArrival.getTime() + tripMinutes * 60 * 1000L);
                long diff = origEstArrival.getTime() - origTime.getTime();
                long diffMinutes = diff / (60 * 1000) % 60;
                Log.d(TAG, "Planned: " + df.format(origTime) + " | " + diffMinutes + " min");
                if (diffMinutes >= 1) {
                    t.setOrigTimeMin(df.format(origEstArrival));
                    t.setDestTimeMin(df.format(destEstArrival));
//                    // I don't know if parts of the leg are delayed...
//                    for (Trip.TripLeg l : t.getTripLegs()) {
//                        Date legOrigTime = df.parse(l.getOrigTimeMin());
//                        Date newLegOrigTime = new Date(legOrigTime.getTime() + diffMinutes * 60 * 1000L);
//                        Date legDestTime = df.parse(l.getDestTimeMin());
//                        Date newLegDestTime = new Date(legDestTime.getTime() + diffMinutes * 60 * 1000L);
//                        l.setOrigTimeMin(df.format(newLegOrigTime));
//                        l.setDestTimeMin(df.format(newLegDestTime));
//                    }
                }
                Log.d(TAG, "Origin: " + minutesUntilTrain + " min | " + df.format(origEstArrival));
                Log.d(TAG, "Destination: " + minutesUntilTrain + " min | " + df.format(destEstArrival));
            } catch (ParseException e) {
                Log.e(TAG, "Invalid date was entered");
            }
        }
    }
}
