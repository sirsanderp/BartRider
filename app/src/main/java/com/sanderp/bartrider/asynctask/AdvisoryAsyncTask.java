package com.sanderp.bartrider.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.database.ApiContract;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.AdvisoryParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by Sander Peerna on 1/16/2017.
 */

public class AdvisoryAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "AdvisoryAsyncTask";

    private AsyncTaskResponse delegate;
    private Context context;
    private List<String> advisories;

    public AdvisoryAsyncTask(AsyncTaskResponse delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            getAdvisories();
            return null;
        } catch (IOException e) {
            return "Failed to refresh";
        } catch (XmlPullParserException e) {
            return "XML parser failed";
        }
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(advisories);
    }

    /**
     * Creates the stream for Stations AsyncTask
     */
    private void getAdvisories() throws XmlPullParserException, IOException {
        InputStream stream = null;
        AdvisoryParser parser = new AdvisoryParser();
        String url = ApiContract.API_URL + "bsa.aspx?cmd=bsa&key=" + ApiContract.API_KEY;
        Log.d(TAG, url);
        try {
            Log.i(TAG, "Parsing advisories...");
            stream = ApiConnection.downloadData(url);
            advisories = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
