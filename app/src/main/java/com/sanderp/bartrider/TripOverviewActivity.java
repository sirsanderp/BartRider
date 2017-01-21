package com.sanderp.bartrider;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sanderp.bartrider.adapter.TripAdapter;
import com.sanderp.bartrider.asynctask.AdvisoryAsyncTask;
import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.QuickPlannerAsyncTask;
import com.sanderp.bartrider.asynctask.StationListAsyncTask;
import com.sanderp.bartrider.database.StationContract;
import com.sanderp.bartrider.structure.Trip;

import java.util.List;

/**
 * Created by Sander Peerna on 8/23/2015.
 */
public class TripOverviewActivity extends AppCompatActivity
        implements TripPlannerFragment.OnFragmentListener {
    private static final String TAG = "TripOverviewActivity";

    private static final String PREFS_NAME = "BartRiderPrefs";
    private static final String FIRST_RUN = "first_Run";

    private FragmentManager fragmentManager;
    private TripPlannerFragment fragment;
    private List<Trip> trips;
    private SharedPreferences prefs;

    private FloatingActionButton mFab;
    private ListView mListView;
    private RelativeLayout mRelativeLayout;
    private TextView mTextView;

    public TripOverviewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);

        prefs = getSharedPreferences(PREFS_NAME, 0);

        if (prefs.getBoolean(FIRST_RUN, true) && isNetworkConnected()) {
            new StationListAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object output) {
                    prefs.edit().putBoolean(FIRST_RUN, false).commit();
                }
            }, this).execute();
        }

        mListView = (ListView) findViewById(R.id.trip_list_view);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    Trip selectedTrip = trips.get(position);
                    Intent tripDetailIntent = new Intent(TripOverviewActivity.this, TripDetailActivity.class);
                    tripDetailIntent.putExtra("trip", selectedTrip);
                    startActivity(tripDetailIntent);
                }
            }
        });

        mTextView = (TextView) findViewById(R.id.advisory);

        fragmentManager = getSupportFragmentManager();
        fragment = (TripPlannerFragment) fragmentManager.findFragmentById(R.id.trip_planner_fragment);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFragment();
            }
        });

        mRelativeLayout = (RelativeLayout) findViewById(R.id.trip_overview);
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideFragment();
            }
        });

        hideFragment();
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
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_refresh:
                updateListItems();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateListItems() {
        if (isNetworkConnected()) {
            final Spinner o = (Spinner) fragment.getView().findViewById(R.id.orig_spinner);
            final Spinner d = (Spinner) fragment.getView().findViewById(R.id.dest_spinner);
            final String originFull = getName((Cursor) o.getSelectedItem());
            final String destinationFull = getName((Cursor) d.getSelectedItem());
            String origin = getAbbreviation((Cursor) o.getSelectedItem());
            String destination = getAbbreviation((Cursor) d.getSelectedItem());
            if (!origin.equals(destination)) {
                new QuickPlannerAsyncTask(new AsyncTaskResponse() {
                    @Override
                    public void processFinish(Object result) {
                        trips = (List<Trip>) result;
                        for (Trip t : trips) {
                            t.setOrigFullName(originFull);
                            t.setDestFullName(destinationFull);
                        }

                        Trip header = new Trip();
                        header.setOrigFullName(originFull);
                        header.setDestFullName(destinationFull);
                        trips.add(0, header);

                        TripAdapter adapter = new TripAdapter(TripOverviewActivity.this, trips);
                        mListView.setAdapter(adapter);
                    }
                }).execute(origin, destination);

                updateAdvisories();
            }
        }
    }

    private void updateAdvisories() {
        if (isNetworkConnected()) {
            new AdvisoryAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object result) {
                    if (result == null) {
                        mTextView.setText("No delays reported.");
                    } else {
                        String advisory_text = (String) result;
                        mTextView.setText(advisory_text);
                    }
                }
            }, this).execute();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(getApplicationContext(), "No network connection.", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private void hideFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(fragment);
        transaction.commit();
        mFab.show();
    }

    private void showFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(fragment);
        transaction.commit();
        mFab.hide();
    }

    private String getName(Cursor c) {
        return c.getString(c.getColumnIndex(StationContract.Column.NAME));
    }

    private String getAbbreviation(Cursor c) {
        return c.getString(c.getColumnIndex(StationContract.Column.ABBREVIATION));
    }

    @Override
    public void onConfirm() {
        hideFragment();
        updateListItems();
    }

    @Override
    public void onCancel() {
        hideFragment();
    }
}
