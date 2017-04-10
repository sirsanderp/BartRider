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
            return getRealTimeEstimates(params[0], params[1]);
        } catch (IOException e) {
            Log.e(TAG, "Failed to refresh.");
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XML parser failed.");
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<TripEstimate> result) {
        delegate.processFinish(result);
    }

    private List<TripEstimate> getRealTimeEstimates(String origAbbr, String trainHeadStation) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RealTimeParser estimates = new RealTimeParser();

        Log.i(TAG, "Parsing real-time estimates...");
        String url = ApiContract.API_URL + "etd.aspx?cmd=etd"
                + "&orig=" + origAbbr
                + "&key=" + ApiContract.API_KEY;
        try {
            stream = ApiConnection.downloadData(url);
            return estimates.parse(stream, trainHeadStation);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
