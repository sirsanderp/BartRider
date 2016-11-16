package com.sanderp.bartrider.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.database.ApiContract;
import com.sanderp.bartrider.database.StationContract;
import com.sanderp.bartrider.structure.Station;
import com.sanderp.bartrider.utility.ApiConnection;
import com.sanderp.bartrider.xmlparser.StationListParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of the AsyncTask to download data from the BART Station API.
 */
public class StationListAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "StationListAsyncTask";

    private AsyncTaskResponse delegate;
    private Context context;
    private List<Station> stations;

    public StationListAsyncTask(AsyncTaskResponse delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            getStations();
            return null;
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
     * Creates the stream for Stations AsyncTask
     */
    private void getStations() throws XmlPullParserException, IOException {
        InputStream stream = null;
        StationListParser stationList = new StationListParser();
        String url = ApiContract.API_URL + "stn.aspx?cmd=stns&key=" + ApiContract.API_KEY;
        try {
            Log.i(TAG, "Parsing stations...");
            stream = ApiConnection.downloadData(url);
            stations = stationList.parse(stream);

            ContentValues values = new ContentValues();
            for (Station station : stations) {
                values.clear();
                values.put(StationContract.Column.ID, station.getId());
                values.put(StationContract.Column.NAME, station.getName());
                values.put(StationContract.Column.ABBREVIATION, station.getAbbr());
                values.put(StationContract.Column.LATITUDE, station.getLatitude());
                values.put(StationContract.Column.LONGITUDE, station.getLongitude());
                values.put(StationContract.Column.ADDRESS, station.getAddress());
                values.put(StationContract.Column.CITY, station.getCity());
                values.put(StationContract.Column.COUNTY, station.getCounty());
                values.put(StationContract.Column.STATE, station.getState());
                values.put(StationContract.Column.ZIPCODE, station.getZipcode());

                Uri uri = context.getContentResolver().insert(StationContract.CONTENT_URI, values);
                if (uri != null) {
                    Log.d(TAG, String.format("%s: %s", station.getId(), station.getAbbr()));
                }
            }
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
