package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.utility.ApiContract;
import com.sanderp.bartrider.structure.Trip;
import com.sanderp.bartrider.structure.TripEstimate;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.QuickPlannerParser;

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
            return getSchedule(params[0], params[1]);
        } catch (IOException e) {
            Log.e(TAG, "Failed to refresh.");
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XML parser failed.");
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Trip> result) {
        delegate.processFinish(result);
    }

    private List<Trip> getSchedule(String origAbbr, String destAbbr) throws XmlPullParserException, IOException {
        InputStream stream = null;
        QuickPlannerParser planner = new QuickPlannerParser();
        String url = ApiContract.API_URL + "sched.aspx?cmd=depart"
                + "&orig=" + origAbbr
                + "&dest=" + destAbbr
                + "&a=3&b=0"
                + "&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Parsing trip schedule...");
            stream = ApiConnection.downloadData(url);
            return planner.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
