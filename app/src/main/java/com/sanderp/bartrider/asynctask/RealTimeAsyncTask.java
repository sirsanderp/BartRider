package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.utility.ApiContract;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.RealTimeParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Sander Peerna on 11/10/2016.
 */

public class RealTimeAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "RealTimeAsyncTask";

    private AsyncTaskResponse delegate;

    public RealTimeAsyncTask(AsyncTaskResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return getRealTimeEstimates(params);
        } catch (IOException e) {
            return "Failed to refresh";
        } catch (XmlPullParserException e) {
            return "XML parser failed";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

    /**
     * Creates the stream for AsyncTask
     */
    private String getRealTimeEstimates(String... params) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RealTimeParser estimates = new RealTimeParser();

        Log.i(TAG, "Getting estimates info...");
        String url = ApiContract.API_URL + "etd.aspx?cmd=etd&orig=" + params[0] +
                "&key=" + ApiContract.API_KEY;
        try {
            stream = ApiConnection.downloadData(url);
            estimates.parse(stream, params[1]);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return null;
    }
}
