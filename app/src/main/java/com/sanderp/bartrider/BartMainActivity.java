package com.sanderp.bartrider;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by Sander Peerna on 8/23/2015.
 */
public class BartMainActivity extends AppCompatActivity {

    private static final String TAG = BartMainActivity.class.getSimpleName();

    private List<BartRouteScheduleParser.RouteSchedule> departures;
    private TextView mDestination;
    private TextView mPlatform;
    private TextView mMinutes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bart_main);

        mDestination = (TextView) findViewById(R.id.destination);
        mPlatform = (TextView) findViewById(R.id.platform);
        mMinutes = (TextView) findViewById(R.id.minutes);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bart_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
//                new BartAPISyncTask().execute("http://api.bart.gov/api/etd.aspx?cmd=etd&orig=CAST&key=MW9S-E7SL-26DU-VV8V");
                new BartAPISyncTask().execute("http://api.bart.gov/api/sched.aspx?cmd=depart&orig=cast&dest=mont&a=4&b=0&key=MW9S-E7SL-26DU-VV8V");
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Implementation of the AsyncTask to download data from the BART API.
     */
    private class BartAPISyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... bartUrl) {
            try {
                return refreshData(bartUrl[0]);
            } catch (IOException e) {
                return "Failed to refresh";
            } catch (XmlPullParserException e) {
                return "XML parser failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            for (BartRouteScheduleParser.RouteSchedule d : departures) {
                mDestination.setText(d.orig_time);
                mPlatform.setText(d.dest_time);
                mMinutes.setText(d.fare);
            }
            Log.i(TAG, result);
        }
    }

    /**
     * Creates the stream for AsyncTask
     */
    private String refreshData(String bartUrl) throws XmlPullParserException, IOException {
        InputStream stream = null;
        BartRouteScheduleParser parser = new BartRouteScheduleParser();

        try {
            stream = downloadData(bartUrl);
            departures = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return departures.toString();
    }

    /**
     * Sets up a connection and gets an input stream
     */
    private InputStream downloadData(String bartUrl) throws IOException {
        URL url = new URL(bartUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);

        conn.connect();
        return conn.getInputStream();
    }
}
