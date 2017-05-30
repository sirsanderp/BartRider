package com.sanderp.bartrider.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;
import com.sanderp.bartrider.xmlparser.AdvisoryParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the AsyncTask to download advisory data from the BART Station Api.
 */
@Deprecated
public class AdvisoryAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "AdvisoryAsyncTask";

    private AsyncTaskResponse delegate;
    private Context context;

    public AdvisoryAsyncTask(AsyncTaskResponse delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            return advisoryText(getAdvisories());
        } catch (IOException e) {
            Log.e(TAG, "Failed to refresh.");
        } catch (XmlPullParserException e) {
            Log.e(TAG, "XML parser failed.");
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
        String url = Constants.Bart.API_URL + "bsa.aspx?cmd=bsa"
                + "&key=" + context.getResources().getString(R.string.bartApiKey);
        List<String> advisories = new ArrayList<>();
        try {
            Log.i(TAG, "Parsing advisories...");
            stream = Utils.getUrlStream(url);
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
