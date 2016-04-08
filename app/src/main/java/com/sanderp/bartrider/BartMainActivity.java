package com.sanderp.bartrider;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.BartDepartureSyncTask;
import com.sanderp.bartrider.asynctask.BartStationSyncTask;
import com.sanderp.bartrider.database.StationContract;
import com.sanderp.bartrider.structure.Departure;

import java.util.List;

/**
 * Created by Sander Peerna on 8/23/2015.
 */
public class BartMainActivity extends AppCompatActivity {
    private static final String TAG = "BartMainActivity";

    private static final String API_URL = "http://api.bart.gov/api/";
    private static final String API_KEY = "MW9S-E7SL-26DU-VV8V";

    private Spinner mOrigSpinner;
    private Spinner mDestSpinner;
    private TextView mOrigTime;
    private TextView mDestTime;
    private TextView mFare;

    private static final String[] FROM = {StationContract.Column.NAME};
    private static final int [] TO = {android.R.id.text1};

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
        mDestSpinner = (Spinner) findViewById(R.id.dest_spinner);

        // Populate the database with station info
        new BartStationSyncTask(new AsyncTaskResponse() {
            @Override
            public void processFinish(Object output) {
                // Populate the spinners with stations
                String[] projection = {StationContract.Column.ID, StationContract.Column.NAME};

                Cursor c = getContentResolver().query(StationContract.CONTENT_URI, projection,
                        null, null, StationContract.DEFAULT_SORT);

                Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

                SimpleCursorAdapter adapter = new SimpleCursorAdapter(BartMainActivity.this,
                        android.R.layout.simple_spinner_item, c, FROM, TO, 0);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mOrigSpinner.setAdapter(adapter);
                mDestSpinner.setAdapter(adapter);
            }
        }, this).execute(API_URL + "stn.aspx?cmd=stns&key=" + API_KEY);
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
                new BartDepartureSyncTask(new AsyncTaskResponse() {
                    @Override
                    public void processFinish(Object result) {
                        List<Departure> departures = (List<Departure>) result;
                        for (Departure d : departures) {
                            mOrigTime.setText(d.originTime);
                            mDestTime.setText(d.destinationTime);
                            mFare.setText(d.fare);
                        }
                    }
                }).execute(API_URL + "sched.aspx?cmd=depart&orig=cast&dest=mont&a=4&b=0&key=" + API_KEY);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
