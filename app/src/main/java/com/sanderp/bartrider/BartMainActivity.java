package com.sanderp.bartrider;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanderp.bartrider.database.StationContract;
import com.sanderp.bartrider.structure.RouteSchedule;
import com.sanderp.bartrider.structure.Station;
import com.sanderp.bartrider.xmlparser.BartRouteScheduleParser;
import com.sanderp.bartrider.xmlparser.BartStationParser;

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
    private static final String TAG = "BartMainActivity";

    private static final String API_URL = "http://api.bart.gov/api/";
    private static final String API_KEY = "MW9S-E7SL-26DU-VV8V";

    private List<RouteSchedule> routeSchedule;
    private List<Station> stations;

    private Spinner mOrigSpinner;
    private Spinner mDestSpinner;
    private TextView mOrigTime;
    private TextView mDestTime;
    private TextView mFare;

    public BartMainActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bart_main);

        // Initialize text fields
        mOrigTime = (TextView) findViewById(R.id.destination);
        mDestTime = (TextView) findViewById(R.id.platform);
        mFare = (TextView) findViewById(R.id.minutes);

        // Initialize spinners
        mOrigSpinner = (Spinner) findViewById(R.id.orig_spinner);
        ArrayAdapter<CharSequence> origAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.stations,
                android.R.layout.simple_spinner_item);
        origAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOrigSpinner.setAdapter(origAdapter);

        mDestSpinner = (Spinner) findViewById(R.id.dest_spinner);
        ArrayAdapter<CharSequence> destAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.stations,
                android.R.layout.simple_spinner_item);
        destAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mDestSpinner.setAdapter(destAdapter);

        Log.i(TAG, "Getting station data...");
        new BartStationSyncTask().execute(API_URL + "stn.aspx?cmd=stns&key=" + API_KEY);
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
//                new BartAPISyncTask().execute(API_URL + "etd.aspx?cmd=etd&orig=CAST&key" + API_KEY);
                new BartScheduleSyncTask().execute(
                        API_URL + "sched.aspx?cmd=depart&orig=cast&dest=mont&a=4&b=0&key=" + API_KEY
                );
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Implementation of the AsyncTask to download data from the BART Schedule API.
     */
    private class BartScheduleSyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... bartUrl) {
            try {
                return refreshSchedule(bartUrl[0]);
            } catch (IOException e) {
                return "Failed to refresh";
            } catch (XmlPullParserException e) {
                return "XML parser failed";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            for (RouteSchedule r : routeSchedule) {
                mOrigTime.setText(r.orig_time);
                mDestTime.setText(r.dest_time);
                mFare.setText(r.fare);
            }
        }
    }

    /**
     * Implementation of the AsyncTask to download data from the BART Station API.
     */
    private class BartStationSyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... bartUrl) {
            try {
                return getStations(bartUrl[0]);
            } catch (IOException e) {
                return "Failed to refresh";
            } catch (XmlPullParserException e) {
                return "XML parser failed";
            }
        }
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

    /**
     * Creates the stream for AsyncTask
     */
    private String refreshSchedule(String bartUrl) throws XmlPullParserException, IOException {
        InputStream stream = null;
        BartRouteScheduleParser parser = new BartRouteScheduleParser();

        try {
            stream = downloadData(bartUrl);
            routeSchedule = parser.parse(stream);
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return null;
    }

    /**
     * Creates the stream for Stations AsyncTask
     */
    private String getStations(String bartUrl) throws XmlPullParserException, IOException {
        InputStream stream = null;
        BartStationParser parser = new BartStationParser();

        Log.i(TAG, "Parsing stations...");
        try {
            stream = downloadData(bartUrl);
            stations = parser.parse(stream);

            ContentValues values = new ContentValues();
            for (Station station : stations) {
                values.clear();
                values.put(StationContract.Column.ID, station.getId());
                values.put(StationContract.Column.NAME, station.getName());
                values.put(StationContract.Column.ABBREVATION, station.getAbbr());
                values.put(StationContract.Column.LATITUDE, station.getLatitude());
                values.put(StationContract.Column.LONGITDUE, station.getLongitude());
                values.put(StationContract.Column.ADDRESS, station.getAddress());
                values.put(StationContract.Column.CITY, station.getCity());
                values.put(StationContract.Column.COUNTY, station.getCounty());
                values.put(StationContract.Column.STATE, station.getState());
                values.put(StationContract.Column.ZIPCODE, station.getZipcode());

                Uri uri = getContentResolver().insert(StationContract.CONTENT_URI, values);
                if (uri != null) {
                    Log.i(TAG, String.format("%s: %s", station.getId(), station.getAbbr()));
                }
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return null;
    }
}
