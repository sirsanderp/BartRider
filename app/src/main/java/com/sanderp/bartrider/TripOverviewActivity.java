package com.sanderp.bartrider;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.sanderp.bartrider.adapters.TripAdapter;
import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.QuickPlannerAsyncTask;
import com.sanderp.bartrider.asynctask.StationListAsyncTask;
import com.sanderp.bartrider.database.StationContract;
import com.sanderp.bartrider.structure.Trip;

import java.util.List;

/**
 * Created by Sander Peerna on 8/23/2015.
 */
public class TripOverviewActivity extends AppCompatActivity {
    private static final String TAG = "TripOverviewActivity";

    private static final String PREFS_NAME = "BartRiderPrefs";
    private static final String FIRST_RUN = "first_Run";
    private static final String[] FROM = {StationContract.Column.NAME};
    private static final int [] TO = {android.R.id.text1};

    private List<Trip> trips;
    private SharedPreferences prefs;

    private ListView mListView;
    private Spinner mOrigSpinner;
    private Spinner mDestSpinner;

    public TripOverviewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);

        prefs = getSharedPreferences(PREFS_NAME, 0);

        // Initialize spinners
        mOrigSpinner = (Spinner) findViewById(R.id.orig_spinner);
        mDestSpinner = (Spinner) findViewById(R.id.dest_spinner);

        // Initialize list view
        mListView = (ListView) findViewById(R.id.trip_list_view);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
             @Override
             public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 Trip selectedTrip = trips.get(position);

                 Intent tripDetailIntent = new Intent(TripOverviewActivity.this, TripDetailActivity.class);
                 tripDetailIntent.putExtra("trip", selectedTrip);
                 startActivity(tripDetailIntent);
             }
         });

        if (prefs.getBoolean(FIRST_RUN, true)) {
            // Populate the database with station info
            new StationListAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object output) {
                    setSpinners();
                    prefs.edit().putBoolean(FIRST_RUN, false).commit();
                }
            }, this).execute();
        }
        else {
            setSpinners();
        }
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
                String origin = getAbbreviation((Cursor) mOrigSpinner.getSelectedItem());
                String destination = getAbbreviation((Cursor) mDestSpinner.getSelectedItem());
                if (!origin.equals(destination)) {
                    new QuickPlannerAsyncTask(new AsyncTaskResponse() {
                        @Override
                        public void processFinish(Object result) {
                            trips = (List<Trip>) result;
                            for (Trip t : trips) {
                                t.setOrigFullName(getName((Cursor) mOrigSpinner.getSelectedItem()));
                                t.setDestFullName(getName((Cursor) mDestSpinner.getSelectedItem()));
                            }
                            TripAdapter adapter = new TripAdapter(TripOverviewActivity.this, trips);
                            mListView.setAdapter(adapter);
                        }
                    }).execute(origin, destination);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setSpinners() {
        // Populate the spinners with stations
        String[] projection = {StationContract.Column.ID, StationContract.Column.NAME, StationContract.Column.ABBREVIATION};

        Cursor c = getContentResolver().query(StationContract.CONTENT_URI, projection,
                null, null, StationContract.DEFAULT_SORT);

        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(TripOverviewActivity.this,
                android.R.layout.simple_spinner_item, c, FROM, TO, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOrigSpinner.setAdapter(adapter);
        mDestSpinner.setAdapter(adapter);
    }

    private String getName(Cursor c) {
        return c.getString(c.getColumnIndex(StationContract.Column.NAME));
    }

    private String getAbbreviation(Cursor c) {
        return c.getString(c.getColumnIndex(StationContract.Column.ABBREVIATION));
    }
}
