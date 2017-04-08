package com.sanderp.bartrider.asynctask;

import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.utility.ApiContract;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.AdvisoryParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the AsyncTask to download advisory data from the BART Station API.
 */
public class AdvisoryAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "AdvisoryAsyncTask";

    private AsyncTaskResponse delegate;

    public AdvisoryAsyncTask(AsyncTaskResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return advisoryText(getAdvisories());
        } catch (IOException e) {
            Log.d(TAG, "Failed to refresh");
        } catch (XmlPullParserException e) {
            Log.d(TAG, "XML parser failed");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

    private List<String> getAdvisories() throws XmlPullParserException, IOException {
        InputStream stream = null;
        AdvisoryParser parser = new AdvisoryParser();
        String url = ApiContract.API_URL + "bsa.aspx?cmd=bsa&key=" + ApiContract.API_KEY;
        List<String> advisories = new ArrayList<>();
        try {
            Log.i(TAG, "Parsing advisories...");
            stream = ApiConnection.downloadData(url);
            advisories = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return advisories;
    }

    private String advisoryText(List<String> advisories) {
        StringBuilder advisory = new StringBuilder();
        for (String s : advisories) {
            advisory.append(s + "\n\n");
        }
        return advisory.toString().trim();
    }
}
