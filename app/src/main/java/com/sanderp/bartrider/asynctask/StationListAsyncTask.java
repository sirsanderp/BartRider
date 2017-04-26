package com.sanderp.bartrider.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.structure.Station;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;
import com.sanderp.bartrider.xmlparser.StationListParser;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Implementation of the AsyncTask to download station data from the BART Station Api.
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

    private void getStations() throws XmlPullParserException, IOException {
        InputStream stream = null;
        StationListParser stationList = new StationListParser();
        String url = Constants.Api.URL + "stn.aspx?cmd=stns"
                + "&key=" + Constants.Api.KEY;
        try {
            Log.i(TAG, "Parsing stations...");
            stream = Utils.getUrlStream(url);
            stations = stationList.parse(stream);

            ContentValues values = new ContentValues();
            for (Station station : stations) {
                values.clear();
                values.put(BartRiderContract.Stations.Column.ID, station.getId());
                values.put(BartRiderContract.Stations.Column.NAME, station.getName());
                values.put(BartRiderContract.Stations.Column.ABBREVIATION, station.getAbbr());
                values.put(BartRiderContract.Stations.Column.LATITUDE, station.getLatitude());
                values.put(BartRiderContract.Stations.Column.LONGITUDE, station.getLongitude());
                values.put(BartRiderContract.Stations.Column.ADDRESS, station.getAddress());
                values.put(BartRiderContract.Stations.Column.CITY, station.getCity());
                values.put(BartRiderContract.Stations.Column.COUNTY, station.getCounty());
                values.put(BartRiderContract.Stations.Column.STATE, station.getState());
                values.put(BartRiderContract.Stations.Column.ZIPCODE, station.getZipcode());

                Uri uri = context.getContentResolver().insert(BartRiderContract.Stations.CONTENT_URI, values);
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
