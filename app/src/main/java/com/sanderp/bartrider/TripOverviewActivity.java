package com.sanderp.bartrider;

import android.animation.ObjectAnimator;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sanderp.bartrider.adapter.TripAdapter;
import com.sanderp.bartrider.asynctask.AdvisoryAsyncTask;
import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.QuickPlannerAsyncTask;
import com.sanderp.bartrider.asynctask.RealTimeAsyncTask;
import com.sanderp.bartrider.asynctask.StationListAsyncTask;
import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.structure.Trip;
import com.sanderp.bartrider.structure.TripEstimate;
import com.sanderp.bartrider.utility.PrefContract;
import com.sanderp.bartrider.utility.Tools;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Sander Peerna on 8/23/2015.
 */
public class TripOverviewActivity extends AppCompatActivity
        implements TripPlannerFragment.OnFragmentListener, TripDrawerFragment.OnFragmentListener {
    private static final String TAG = "TripOverviewActivity";

    private FragmentManager fragmentManager;
    private TripDrawerFragment drawerFragment;
    private TripPlannerFragment plannerFragment;
    private SharedPreferences sharedPrefs;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFab;
    private Drawable mFavoriteIcon;
    private ListView mTripSchedules;
    private ProgressBar mNextDepartureProgress;
    private TextView mAdvisory;
    private TextView mNextDeparture;
    private TextView mTripHeader;
    private Toolbar mToolbar;

    private List<Trip> trips;
    private List<TripEstimate> tripEstimates;
    private int favoriteTrip;
    private String origAbbr;
    private String origFull;
    private String destAbbr;
    private String destFull;

    private CountDownTimer nextDepartureCountdown;

    public TripOverviewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);

        sharedPrefs = getSharedPreferences(PrefContract.PREFS_NAME, 0);
        if (sharedPrefs.getBoolean(PrefContract.FIRST_RUN, true)) {
            Log.i(TAG, "First run setup...");
            sendBroadcast(new Intent("com.sanderp.bartrider.action.START_ADVISORY_SERVICE"));
            new StationListAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object output) {
                    sharedPrefs.edit().putBoolean(PrefContract.FIRST_RUN, false).apply();
                }
            }, this).execute();
        }

        // Set the toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        fragmentManager = getSupportFragmentManager();
        drawerFragment = (TripDrawerFragment) fragmentManager.findFragmentById(R.id.trip_drawer_fragment);
        plannerFragment = TripPlannerFragment.newInstance();

        // MAIN ACTIVITY
        // Open the TripDetailActivity based on the list item that was clicked
        mTripHeader = (TextView) findViewById(R.id.trip_header);
        mTripSchedules = (ListView) findViewById(R.id.trip_list_view);
        mTripSchedules.setEmptyView(findViewById(R.id.empty_list_item));
        mTripSchedules.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Trip selectedTrip = trips.get(position);
                Intent tripDetailIntent = new Intent(TripOverviewActivity.this, TripDetailActivity.class);
                tripDetailIntent.putExtra("origin", origFull);
                tripDetailIntent.putExtra("destination", destFull);
                tripDetailIntent.putExtra("trip", selectedTrip);
                startActivity(tripDetailIntent);
            }
        });
        mNextDepartureProgress = (ProgressBar) findViewById(R.id.next_train_progress);
        mNextDeparture = (TextView) findViewById(R.id.next_train);
        mAdvisory = (TextView) findViewById(R.id.advisory);

        // Drawer portion of the main activity
        mDrawerLayout = (DrawerLayout) findViewById(R.id.trip_overview_drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerLayout.addDrawerListener(mDrawerToggle);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                plannerFragment.show(fragmentManager, "Trip Planner Fragment");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (isTripSet()) {
            Log.i(TAG, "Saving last trip information...");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(PrefContract.LAST_TRIP, true);
            editor.putInt(PrefContract.LAST_ID, favoriteTrip);
            editor.putString(PrefContract.LAST_ORIG_ABBR, origAbbr);
            editor.putString(PrefContract.LAST_ORIG_FULL, origFull);
            editor.putString(PrefContract.LAST_DEST_ABBR, destAbbr);
            editor.putString(PrefContract.LAST_DEST_FULL, destFull);
            editor.apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sharedPrefs.getBoolean(PrefContract.LAST_TRIP, false)) {
            Log.i(TAG, "Retrieving last trip information...");
            setTrip(
                    sharedPrefs.getString(PrefContract.LAST_ORIG_ABBR, null),
                    sharedPrefs.getString(PrefContract.LAST_ORIG_FULL, null),
                    sharedPrefs.getString(PrefContract.LAST_DEST_ABBR, null),
                    sharedPrefs.getString(PrefContract.LAST_DEST_FULL, null)
            );
            updateTripSchedule();
            updateAdvisory();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bart_main, menu);
        mFavoriteIcon = menu.getItem(0).getIcon();
        int id = sharedPrefs.getInt(PrefContract.LAST_ID, -1);
        if (id != 0) updateFavoriteIcon(id);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_favorite:
                toggleFavorite();
                return true;
            case R.id.action_refresh:
                updateTripSchedule();
                updateAdvisory();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfirm(String origAbbr, String origFull, String destAbbr, String destFull) {
        setTrip(origAbbr, origFull, destAbbr, destFull);
        updateFavoriteIcon(-1);
        updateTripSchedule();
        updateAdvisory();
    }

    @Override
    public void onFavoriteClick(int id, String origAbbr, String origFull, String destAbbr, String destFull) {
        setTrip(origAbbr, origFull, destAbbr, destFull);
        updateFavoriteIcon(id);
        updateTripSchedule();
        updateAdvisory();
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setTrip(String origAbbr, String origFull, String destAbbr, String destFull) {
        if (isValidTrip(origAbbr, destAbbr)) {
            this.origAbbr = origAbbr;
            this.origFull = origFull;
            this.destAbbr = destAbbr;
            this.destFull = destFull;
        }
    }

    private void updateFavoriteIcon(int id) {
        favoriteTrip = ((id == -1) ? favoriteTrip = drawerFragment.isFavoriteTrip(origAbbr, destAbbr) : id);

        if (favoriteTrip == 0) mFavoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.material_light), PorterDuff.Mode.SRC_ATOP);
        else mFavoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.bart_primary2), PorterDuff.Mode.SRC_ATOP);
    }

    private void updateTripSchedule() {
        if (isTripSet() && Tools.isNetworkConnected(this)) {
            new QuickPlannerAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object result) {
                    trips = (List<Trip>) result;
                    updateTripRealTimeEstimates();
                }
            }).execute(origAbbr, destAbbr);
        }
    }

    private void updateTripRealTimeEstimates() {
        if (isTripSet() && Tools.isNetworkConnected(this)) {
            new RealTimeAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object result) {
                    // Get estimates for each leg of the trip, not just the first one...
                    tripEstimates = (List<TripEstimate>) result;
                    mergeTripDetails();
                    mTripHeader.setText(origFull + " - " + destFull);
                    mTripSchedules.setAdapter(new TripAdapter(TripOverviewActivity.this, trips));
                    if (tripEstimates.size() > 0) {
                        int nextTrain = tripEstimates.get(0).getMinutes();
                        mNextDepartureProgress.clearAnimation();
                        ObjectAnimator animator = ObjectAnimator.ofInt(mNextDepartureProgress, "progress", nextTrain * 60, 0);
                        animator.setDuration(nextTrain * 60 * 1000);
                        animator.setInterpolator(new LinearInterpolator());
                        animator.start();

                        mNextDeparture.setTextSize(42);
                        if (nextDepartureCountdown != null) nextDepartureCountdown.cancel();
                        nextDepartureCountdown = new CountDownTimer(nextTrain * 60 * 1000, 1000) {
                            public void onTick(long millisUntilFinished) {
                                String timeLeft = String.format("%d:%02d", millisUntilFinished / (60 * 1000), millisUntilFinished / 1000 % 60);
                                mNextDeparture.setText(timeLeft);
                            }

                            public void onFinish() {
                                mNextDeparture.setTextSize(30);
                                mNextDeparture.setText("Leaving...");
                            }
                        }.start();
                    }
                }
            }).execute(origAbbr, trips.get(0).getTripLegs().get(0).getTrainHeadStation());
        }
    }

    private void mergeTripDetails() {
        Date now = new Date();
        DateFormat df = new SimpleDateFormat("h:mm a");
        Log.d(TAG, "Current Time: " + df.format(now));
        for (int i = 3 - tripEstimates.size(); i < trips.size(); i++) {
            Trip trip = trips.get(i);
            Trip.TripLeg tripLeg = trip.getTripLegs().get(0);
            int minUntilDeparture = tripEstimates.get(i).getMinutes();
            Log.d(TAG, "Until Departure: " + minUntilDeparture + " minutes");
            try {
                Date origDeparture = df.parse(trip.getOrigTimeMin());
                Date destArrival = df.parse(trip.getDestTimeMin());
                Date estOrigDeparture = new Date(now.getTime() + minUntilDeparture * 60 * 1000);
                long diff = (estOrigDeparture.getTime() - origDeparture.getTime()) / (60 * 1000) % 60;
                Date estDestArrival = new Date(destArrival.getTime() + diff * 60 * 1000L);
                Log.d(TAG, "Trip (planned): " + df.format(origDeparture) + " | " + df.format(destArrival));
                Log.d(TAG, "Trip (estimated): " + df.format(estOrigDeparture) + " | " + df.format(estDestArrival));

                Date legDeparture = df.parse(tripLeg.getOrigTimeMin());
                Date legArrival = df.parse(tripLeg.getDestTimeMin());
                Date estLegOrigDeparture = new Date(now.getTime() + minUntilDeparture * 60 * 1000);
                Date estLegDestArrival = new Date(legArrival.getTime() + diff * 60 * 1000);
                Log.d(TAG, "Trip Leg (planned): " + df.format(legDeparture) + " | " + df.format(legArrival));
                Log.d(TAG, "Trip Leg (estimated): " + df.format(estLegOrigDeparture) + " | " + df.format(estLegDestArrival));

                Log.d(TAG, "Difference: " + diff + " minutes");
                if (diff >= 1) {
                    Log.d(TAG, "Updating trip and trip leg estimated times...");
                    trip.setEstOrigDeparture(df.format(estOrigDeparture));
                    trip.setEstDestArrival(df.format(estDestArrival));
                    tripLeg.setEstLegOrigDeparture(df.format(estLegOrigDeparture));
                    tripLeg.setEstLegDestArrival(df.format(estLegDestArrival));
                }
            } catch (ParseException e) {
                Log.e(TAG, "Invalid date was entered.");
            }
        }
    }

    private void updateAdvisory() {
        if (isTripSet() && Tools.isNetworkConnected(this)) {
            new AdvisoryAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object result) {
                    mAdvisory.setText((String) result);
                }
            }).execute();
        }
    }

    private void toggleFavorite() {
        if (!isTripSet()) return;

        if (favoriteTrip <= 0) {
            ContentValues values = new ContentValues();
            values.put(BartRiderContract.Favorites.Column.ORIG_ABBR, origAbbr);
            values.put(BartRiderContract.Favorites.Column.ORIG_FULL, origFull);
            values.put(BartRiderContract.Favorites.Column.DEST_ABBR, destAbbr);
            values.put(BartRiderContract.Favorites.Column.DEST_FULL, destFull);

            Uri uri = this.getContentResolver().insert(BartRiderContract.Favorites.CONTENT_URI, values);
            Log.d(TAG, uri.toString());
            if (uri != null) {
                Log.d(TAG, String.format("Added to favorites: %s - %s", origAbbr, destAbbr));
            }
            updateFavoriteIcon(Integer.parseInt(uri.getLastPathSegment()));
        } else {
            Uri uri = Uri.parse(BartRiderContract.Favorites.CONTENT_URI + "/" + favoriteTrip);
            int count = this.getContentResolver().delete(uri, null, null);
            if (count > 0) {
                Log.d(TAG, String.format("Removed from favorites: %s - %s", origAbbr, destAbbr));
            }
            updateFavoriteIcon(0);
        }
    }

    private boolean isTripSet() {
        if (origAbbr == null || destAbbr == null) {
            Toast.makeText(this, "Please select stations in the trip planner.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidTrip(String origAbbr, String destAbbr) {
        if (origAbbr.equals(destAbbr)) {
            Toast.makeText(this, "Origin and destination cannot be the same.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
