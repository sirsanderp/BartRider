package com.sanderp.bartrider.asynctask;

import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.pojo.stationlist.Station;
import com.sanderp.bartrider.pojo.stationlist.StationListPojo;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.Utils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Implementation of the AsyncTask to download station data from the BART Station Api.
 */
public class StationListAsyncTask extends AsyncTask<String, Void, String> {
    private static final String TAG = "StationListAsyncTask";

    private AsyncTaskResponse delegate;
    private Context context;

    public StationListAsyncTask(AsyncTaskResponse delegate, Context context) {
        this.context = context;
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... params) {
        try {
            getStations();
        } catch (IOException e) {
            Log.d(TAG, "Input stream failed.");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        delegate.processFinish(result);
    }

    private void getStations() throws IOException {
        InputStream stream = null;
        String url = Constants.Api.URL + "stn.aspx?cmd=stns"
                + "&key=" + Constants.Api.KEY
                + "&json=y";
        try {
            Log.i(TAG, "Parsing station lists...");
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
            stream = Utils.getUrlStream(url);
            StationListPojo stationList = mapper.readValue(stream, StationListPojo.class);

            ContentValues values = new ContentValues();
            for (Station station : stationList.getRoot().getStations().getStation()) {
                values.clear();
                values.put(BartRiderContract.Stations.Column.ID, station.getId());
                values.put(BartRiderContract.Stations.Column.NAME, station.getName());
                values.put(BartRiderContract.Stations.Column.ABBREVIATION, station.getAbbr());
                values.put(BartRiderContract.Stations.Column.LATITUDE, station.getGtfsLatitude());
                values.put(BartRiderContract.Stations.Column.LONGITUDE, station.getGtfsLongitude());
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
