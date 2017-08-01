package com.sanderp.bartrider;

import android.animation.ObjectAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.sanderp.bartrider.adapter.TripAdapter;
import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.StationListAsyncTask;
import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.service.AdvisoryService;
import com.sanderp.bartrider.service.QuickPlannerService;
import com.sanderp.bartrider.service.RealTimeEtdService;
import com.sanderp.bartrider.pojo.quickplanner.Fare;
import com.sanderp.bartrider.pojo.quickplanner.Leg;
import com.sanderp.bartrider.pojo.quickplanner.QuickPlannerPojo;
import com.sanderp.bartrider.pojo.quickplanner.Trip;
import com.sanderp.bartrider.pojo.realtimeetd.RealTimeEtdPojo;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.PrefContract;
import com.sanderp.bartrider.utility.Utils;

import io.fabric.sdk.android.Fabric;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Displays information about the selected trip:
 *      - a schedule for the trip listing the time for the next trains
 *          - updated after a train has left the station
 *      - an estimated arrival timer of the next train for the trip
 *          - updated every 15 seconds
 *      - trip fare information
 *  Access to multiple action bar items:
 *      - favorite a trip
 *      - view BART advisories
 *      - view BART map
 *      - reverse trip
 *      - refresh trip
 *      - dynamic links ot @SFBART & @SFBARTalert twitters
 */
public class TripOverviewActivity extends AppCompatActivity
        implements TripPlannerFragment.OnFragmentListener, TripDrawerFragment.OnFragmentListener {
    private static final String TAG = "TripOverviewActivity";

    private static final int NEXT_DEPARTURE_MAX = 3600;
    private static final int ETD_FAILURE = -101;
    private static final int SCHED_FAILURE = -102;

    private static final DateFormat DF;
    static {
        DF = new SimpleDateFormat("hh:mm:ss a", Locale.US);
        DF.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
    }

    private FragmentManager fragmentManager;
    private TripAdvisoryFragment advisoryFragment;
    private TripDrawerFragment drawerFragment;
    private TripPlannerFragment plannerFragment;
    private SharedPreferences sharedPrefs;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFab;
    private Drawable mAdvisoryIcon;
    private Drawable mFavoriteIcon;
    private ListView mTripSchedules;
    private ProgressBar mLoadingProgressBar;
    private ProgressBar mNextDepartureProgressBar;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mEmptyTripSchedules;
    private TextView mNextDeparture;
    private TextView mNextDepartureWindow;
    private TextView mTripHeader;
    private TextView mTripFare;
    private Toolbar mToolbar;

    private HashMap<String, RealTimeEtdPojo> etdResults;
    private QuickPlannerPojo scheduleResults;
    private List<Trip> currTrips;

    private boolean ranOnCreate = false;
    private int favoriteTrip = -1;
    private String origAbbr;
    private String origFull;
    private String destAbbr;
    private String destFull;

    private AlarmManager alarmManager;
    private BroadcastReceiver broadcastReceiver;
    private CountDownTimer nextDepartureCountdown;
    private PendingIntent quickPlannerPendingIntent;
    private PendingIntent realTimeEtdPendingIntent;

    public TripOverviewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BartTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);

        if (BuildConfig.USE_CRASHLYTICS) {
            Fabric.with(this, new Crashlytics());
        }

        // Set up Toolbar to replace ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Set up station database on first run.
        sharedPrefs = getSharedPreferences(PrefContract.PREFS_NAME, 0);
        if (Utils.isNetworkConnected(this) && getContentResolver().query(BartRiderContract.Stations.CONTENT_URI, null , null, null, null).getCount() == 0) {
            new StationListAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object output) {
                    // Do nothing.
                }
            }, this).execute();
        }

        // Start advisory notification service if it's not running.
        if (PendingIntent.getService(this, -1, new Intent(this, AdvisoryService.class), PendingIntent.FLAG_NO_CREATE) == null) {
            Log.i(TAG, "Starting advisory notification service...");
            sendBroadcast(new Intent(Constants.Broadcast.ADVISORY_SERVICE));
        }

        // Configurations for scheduling repeating jobs.
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(TAG, "Received broadcast...");
                if (intent.getAction().equals(Constants.Broadcast.ADVISORY_SERVICE)) {
                    Log.d(TAG, "onReceive(): delegate to onReceiveAdvisory()");
                    onReceiveAdvisory();
                } else if (intent.getAction().equals(Constants.Broadcast.QUICK_PLANNER_SERVICE)) {
                    Log.d(TAG, "onReceive(): delegate to onReceiveTripSchedules()");
                    onReceiveTripSchedules(intent);
                } else if (intent.getAction().equals(Constants.Broadcast.REAL_TIME_ETD_SERVICE)) {
                    Log.d(TAG, "onReceive(): delegate to onReceiveTripRealTimeEtd()");
                    onReceiveTripRealTimeEtd(intent);
                }
            }
        };

        fragmentManager = getSupportFragmentManager();
        advisoryFragment = TripAdvisoryFragment.newInstance();
        drawerFragment = (TripDrawerFragment) fragmentManager.findFragmentById(R.id.trip_drawer_fragment);
        plannerFragment = TripPlannerFragment.newInstance();

        // MAIN ACTIVITY
        // Open the TripDetailActivity based on the list item that was clicked
        mTripHeader = (TextView) findViewById(R.id.trip_header);
        mTripFare = (TextView) findViewById(R.id.trip_fare);
        mEmptyTripSchedules = (TextView) findViewById(R.id.empty_overview_list);
        mTripSchedules = (ListView) findViewById(R.id.trip_list_view);
        mTripSchedules.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent tripDetailIntent = new Intent(TripOverviewActivity.this, TripDetailActivity.class)
                        .putExtra(TripDetailActivity.ORIG, origFull)
                        .putExtra(TripDetailActivity.DEST, destFull)
                        .putExtra(TripDetailActivity.TRIP, currTrips.get(position));
                startActivity(tripDetailIntent);
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                cancelAlarms();
                getTripSchedules();
            }
        });

        mLoadingProgressBar = (ProgressBar) findViewById(R.id.loading);
        mNextDepartureProgressBar = (ProgressBar) findViewById(R.id.next_departure_progress_bar);
        mNextDeparture = (TextView) findViewById(R.id.next_departure);
        mNextDepartureWindow = (TextView) findViewById(R.id.next_departure_window);
        setOverviewVisibility(View.GONE);

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
                if (!plannerFragment.isAdded() && Utils.isNetworkConnected(TripOverviewActivity.this)) {
                    plannerFragment.setSpinners(origAbbr, destAbbr);
                    plannerFragment.show(fragmentManager, "Trip Planner Fragment");
                }
            }
        });

        if (getIntent() != null && getIntent().getSerializableExtra(TripDetailActivity.TRIP_LEG) != null) {
            Leg tripLeg = (Leg) getIntent().getSerializableExtra(TripDetailActivity.TRIP_LEG);
            setTrip(
                    -1,
                    tripLeg.getOrigin(),
                    tripLeg.getOriginFull(),
                    tripLeg.getDestination(),
                    tripLeg.getDestinationFull()
            );
        } else if (sharedPrefs.getBoolean(PrefContract.LAST_TRIP, false)) {
            Log.d(TAG, "onCreate(): Retrieving last trip information...");
            setTrip(
                    sharedPrefs.getInt(PrefContract.LAST_ID, -1),
                    sharedPrefs.getString(PrefContract.LAST_ORIG_ABBR, null),
                    sharedPrefs.getString(PrefContract.LAST_ORIG_FULL, null),
                    sharedPrefs.getString(PrefContract.LAST_DEST_ABBR, null),
                    sharedPrefs.getString(PrefContract.LAST_DEST_FULL, null)
            );
        } else {
            mTripSchedules.setEmptyView(mEmptyTripSchedules);
            favoriteTrip = 0;
        }
        ranOnCreate = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelAlarms();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        if (isTripSet()) {
            Log.d(TAG, "onPause(): Saving last trip information...");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(PrefContract.LAST_TRIP, true)
                    .putInt(PrefContract.LAST_ID, favoriteTrip)
                    .putString(PrefContract.LAST_ORIG_ABBR, origAbbr)
                    .putString(PrefContract.LAST_ORIG_FULL, origFull)
                    .putString(PrefContract.LAST_DEST_ABBR, destAbbr)
                    .putString(PrefContract.LAST_DEST_FULL, destFull)
                    .apply();
        }
        ranOnCreate = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.Broadcast.ADVISORY_SERVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.Broadcast.QUICK_PLANNER_SERVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.Broadcast.REAL_TIME_ETD_SERVICE));
        if (isTripSet() && !ranOnCreate) {
            Log.d(TAG, "onResume(): Refreshing trip schedules...");
            setTripLoading();
            getTripSchedules();
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bart_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        mAdvisoryIcon = menu.getItem(1).getIcon();
        mFavoriteIcon = menu.getItem(0).getIcon();
        setFavoriteIcon();
        return super.onPrepareOptionsMenu(menu);
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
            case R.id.action_advisory:
                getAdvisory();
                return true;
            case R.id.action_bart_map:
                startActivity(new Intent(this, BartMapActivity.class));
                return true;
            case R.id.action_refresh:
                cancelAlarms();
                getTripSchedules();
                return true;
            case R.id.action_reverse_trip:
                setTrip(-1, destAbbr, destFull, origAbbr, origFull);
                return true;
            case R.id.action_sfbart_twitter:
                showTwitter("SFBART");
                return true;
            case R.id.action_sfbartalert_twitter:
                showTwitter("SFBARTalert");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfirm(String origAbbr, String origFull, String destAbbr, String destFull) {
        setTrip(-1, origAbbr, origFull, destAbbr, destFull);
        setRecentTrip();
    }

    @Override
    public void onTripClick(int id, String origAbbr, String origFull, String destAbbr, String destFull) {
        setTrip(id, origAbbr, origFull, destAbbr, destFull);
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    protected void setTrip(int id, String origAbbr, String origFull, String destAbbr, String destFull) {
        if (!isTripSame(origAbbr, destAbbr) && isTripValid(origAbbr, destAbbr)) {
            cancelAlarms();
            favoriteTrip = id;
            this.origAbbr = origAbbr;
            this.origFull = origFull;
            this.destAbbr = destAbbr;
            this.destFull = destFull;
            setTripLoading();
            invalidateOptionsMenu();
            getTripSchedules();
        }
    }

    private void setTripLoading() {
        setOverviewVisibility(View.INVISIBLE);
        mEmptyTripSchedules.setVisibility(View.GONE);
        mTripHeader.setText(origFull + " - " + destFull);
        mTripHeader.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
    }

    private void setAdvisoryIcon() {
        // TODO: Change color of icon based on the delays.
    }

    private void setFavoriteIcon() {
        // -1: Not checked yet, 0: Checked, not a favorite, 1+: Checked, a favorite
        if (favoriteTrip == -1) favoriteTrip = drawerFragment.isFavoriteTrip(origAbbr, destAbbr);
        if (favoriteTrip == 0 || mTripHeader.getVisibility() == View.GONE)
            mFavoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.material_light), PorterDuff.Mode.SRC_ATOP);
        else
            mFavoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.bart_primary2), PorterDuff.Mode.SRC_ATOP);
    }

    private void getAdvisory() {
        if (Utils.isNetworkConnected(this))
            startService(new Intent(this, AdvisoryService.class)
                    .putExtra(AdvisoryService.NOTIFY, false));
    }

    private void onReceiveAdvisory() {
        Log.d(TAG, "onReceiveAdvisory(): Received callback from broadcast.");
        showAdvisory();
    }

    private void getTripSchedules() {
        if (isTripSet()) {
            Intent quickPlannerIntent = new Intent(this, QuickPlannerService.class);
            quickPlannerIntent.putExtra(QuickPlannerService.ORIG, origAbbr)
                    .putExtra(QuickPlannerService.DEST, destAbbr);
            quickPlannerPendingIntent = PendingIntent.getService(this, -1, quickPlannerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            startService(quickPlannerIntent);
        } else {
            setTripOverview(SCHED_FAILURE);
        }
    }

    private void onReceiveTripSchedules(Intent intent) {
        Log.d(TAG, "onReceiveTripSchedules(): Received callback from broadcast.");
        scheduleResults = (QuickPlannerPojo) intent.getSerializableExtra(QuickPlannerService.RESULT);
        if (scheduleResults == null || scheduleResults.getRoot() == null || scheduleResults.getRoot().getSchedule().getRequest().getTrips().isEmpty()) {
            setTripOverview(SCHED_FAILURE);
        } else {
            getTripRealTimeEtd();
        }
    }

    private void getTripRealTimeEtd() {
        if (isTripSet() && Utils.isNetworkConnected(this)) {
            // Use bundle to pass serialized objects using the AlarmManager.
            Bundle bundle = new Bundle();
            bundle.putSerializable(RealTimeEtdService.OBJECT_LIST, scheduleResults.getRoot().getSchedule().getRequest().buildBatchLoad());
            Intent realTimeEtdIntent = new Intent(this, RealTimeEtdService.class);
            realTimeEtdIntent.putExtra(RealTimeEtdService.OBJECT_LIST, bundle);
            realTimeEtdPendingIntent = PendingIntent.getService(this, -1, realTimeEtdIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            startService(realTimeEtdIntent);
        } else {
            setTripOverview(ETD_FAILURE);
        }
    }

    private void onReceiveTripRealTimeEtd(Intent intent) {
        Log.d(TAG, "onReceiveTripRealTimeEtd(): Received callback from broadcast.");
        etdResults = (HashMap<String, RealTimeEtdPojo>) intent.getSerializableExtra(RealTimeEtdService.RESULT);
        currTrips = new ArrayList<>(scheduleResults.getRoot().getSchedule().getRequest().getTrips());
        if (etdResults == null || etdResults.isEmpty()) {
            removeTripsPastNow();
            setTripOverview(ETD_FAILURE);
        } else {
            setTripOverview(mergeSchedulesAndEtd());
        }
    }

    private int mergeSchedulesAndEtd() {
        Date now = new Date();
        boolean isEtdApiDown = true;
        int nextDeparture = NEXT_DEPARTURE_MAX;
        for (int i = 0; i < currTrips.size(); i++) {
            Trip trip = currTrips.get(i);
            long prevEstLegDestArrival = 0;
            for (int j = 0; j < trip.getLegs().size(); j++) {
                Leg tripLeg = trip.getLeg(j);
                String headAbbr = tripLeg.getTrainHeadStation();
//                Log.v(TAG, tripLeg.getOrigin() + " -> " + headAbbr);

                // Check if the real-time etds for the head station exist.
                // Mainly applies for Oakland Airport station since it has no estimates.
                if (etdResults.get(headAbbr) == null || etdResults.get(headAbbr).getTrains().isEmpty()) {
                    if (tripLeg.getOrigTimeEpoch() < now.getTime()) {
//                        Log.v(TAG, "Trip originates before current time.");
                        currTrips.remove(i--);
                        break;
                    } else {
//                        Log.v(TAG, "Trip originates after current time.");
                        if (i == 0 && j == 0) nextDeparture = tripLeg.getUntilOrigDepart();
                        prevEstLegDestArrival = tripLeg.getEtdDestTime();
                        continue;
                    }
                }

//                Log.v(TAG, trip.getOrigTimeDate() + " " + trip.getOrigTimeMin() + " | "  + etdResults.get(headAbbr).getPrevDepart());
//                Log.v(TAG, trip.getOrigTimeEpoch() + " | "  + etdResults.get(headAbbr).getPrevDepartEpoch());
//                Log.v(TAG, "Current Time: " + DF.format(now) + " | API Time: " + etdResults.get(headAbbr).getApiUpdate());
//                Log.v(TAG, "Actual seconds: " + etdResults.get(headAbbr).getEtdSeconds(0));
                // Check if the trip has already left the station.
                if (j == 0 && trip.getOrigTimeEpoch() < etdResults.get(headAbbr).getPrevDepartEpoch()) {
//                    Log.v(TAG, "Removing trip: " + trip.getOrigin() + " -> " + headAbbr);
//                    Log.v(TAG, trip.getOrigTimeDate() + "" + trip.getOrigTimeMin() + " | "  + etdResults.get(headAbbr).getPrevDepart());
//                    Log.v(TAG, trip.getOrigTimeEpoch() + " | "  + etdResults.get(headAbbr).getPrevDepartEpoch());
                    currTrips.remove(i--);
                    break;
                }

                // Check if the real-time etds are recent.
                int sinceLastUpdateSeconds = etdResults.get(headAbbr).getSinceApiUpdate();
//                Log.v(TAG, "sinceLastUpdate: " + sinceLastUpdateSeconds);
                if (sinceLastUpdateSeconds > 120) {
//                    Log.v(TAG, "It's been too long since last update.");
                    prevEstLegDestArrival = tripLeg.getEtdDestTime();
                    continue;
                }
                isEtdApiDown = false;

//                boolean foundTripLeg = false;
                while (etdResults.get(headAbbr).getTrains().size() > 0) {
//                    Log.d(TAG, "Checking trip leg real-time etds...");
                    List<Integer> trainInfo = etdResults.get(headAbbr).getTrains().remove(0);

                    int etdSeconds = trainInfo.get(1);
//                    Log.v(TAG, "Estimated seconds: " + etdSeconds);
                    if (etdSeconds <= sinceLastUpdateSeconds) etdSeconds = 0;
                    else etdSeconds -= sinceLastUpdateSeconds;

                    long estLegOrigDeparture = now.getTime() + (etdSeconds * 1000);
//                    Log.v(TAG, "Leg : " + DF.format(estLegOrigDeparture) + " | " + DF.format(prevEstLegDestArrival));
//                    Log.v(TAG, "Leg : " + estLegOrigDeparture + " | " + prevEstLegDestArrival);
                    if (estLegOrigDeparture < prevEstLegDestArrival) {
//                        prevEstLegDestArrival = tripLeg.getEtdDestTime();  // Makes no sense, forgot why I put it here.
                        continue;
                    }

//                    foundTripLeg = true;
                    long diffSeconds = (estLegOrigDeparture - tripLeg.getOrigTimeEpoch()) / 1000;
                    long estLegDestArrival = tripLeg.getDestTimeEpoch() + (diffSeconds * 1000);
                    prevEstLegDestArrival = estLegDestArrival;

                    if (j == 0) {
                        if (i == 0) nextDeparture = etdSeconds;
                    } else {
                        etdResults.get(headAbbr).getTrains().add(0, trainInfo);
                    }

//                    Log.v(TAG, "Adjusted seconds: " + etdSeconds + " | Offset seconds: " + sinceLastUpdateSeconds);
//                    Log.v(TAG, "Trip Leg (planned): " + tripLeg.getOrigTimeMin() + " - " + tripLeg.getDestTimeMin());
//                    Log.v(TAG, "Trip Leg (estimated): " + DF.format(estLegOrigDeparture) + " - " + DF.format(estLegDestArrival));
//                    Log.v(TAG, "Difference: " + diffSeconds + " seconds");
                    tripLeg.setEtdOrigTime(estLegOrigDeparture);
                    tripLeg.setEtdDestTime(estLegDestArrival);
                    tripLeg.setLength(trainInfo.get(2));
                    break;
                }

//                if (!foundTripLeg) {
////                     Add to trip leg (last trip leg - curr trip leg) until it's > prevEstLegDestArrival
//                }
            }
            trip.setEtdOrigTime(trip.getLeg(0).getEtdOrigTime());
            trip.setEtdDestTime(trip.getLeg(trip.getLegs().size() - 1).getEtdDestTime());
        }

        if (isEtdApiDown) {
            removeTripsPastNow();
            return ETD_FAILURE;
        } else {
            Collections.sort(currTrips);
            return nextDeparture;
        }
    }

    private void removeTripsPastNow() {
        Date now = new Date();
        for (int i = 0; i < currTrips.size(); i++) {
            if (currTrips.get(i).getOrigTimeEpoch() < now.getTime())
                currTrips.remove(i--);
        }
    }

    private void setTripOverview(int nextDeparture) {
        mLoadingProgressBar.setVisibility(View.GONE);
        mSwipeRefreshLayout.setRefreshing(false);
        if ((currTrips == null || currTrips.isEmpty()) && nextDeparture == SCHED_FAILURE) {
            Log.i(TAG, "BART API is unavailable...");
            setOverviewVisibility(View.GONE);
            setOfflineLayout("BART schedules are currently unavailable.");
            alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15 * 1000, quickPlannerPendingIntent);
        } else if (currTrips.size() > 0 && nextDeparture != NEXT_DEPARTURE_MAX) {
            Log.i(TAG, "Building trip view...");
            List<Fare> fares = currTrips.get(0).getFares().getFare();
            mTripFare.setText(String.format(getResources().getString(R.string.fares), fares.get(0).getAmount(), fares.get(1).getAmount()));
            mTripSchedules.setAdapter(new TripAdapter(this, currTrips));
            mEmptyTripSchedules.setVisibility(View.GONE);
            setOverviewVisibility(View.VISIBLE);

            if (nextDeparture == ETD_FAILURE) {
                Log.i(TAG, "Real-time estimates are unavailable...");
                mNextDepartureWindow.setText(R.string.time_window_api_error);
                setNextDepartureProgressBar((int) ((currTrips.get(0).getEtdOrigTime() - new Date().getTime()) / 1000));
            } else if (nextDeparture == SCHED_FAILURE) {
                removeTripsPastNow();
                if (currTrips.isEmpty()) {
                    Log.i(TAG, "BART API is unavailable...");
                    setOverviewVisibility(View.GONE);
                    setOfflineLayout("BART schedules are currently unavailable.");
                } else {
                    Log.i(TAG, "BART API is unavailable, using local trip data...");
                    mNextDepartureWindow.setText(R.string.time_window_api_error);
                    setNextDepartureProgressBar((int) ((currTrips.get(0).getEtdOrigTime() - new Date().getTime()) / 1000));
                }
            } else {
                mNextDepartureWindow.setText(R.string.time_window);
                setNextDepartureProgressBar(nextDeparture);
            }

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            ViewGroup.LayoutParams swipeRefreshLayoutParams = mSwipeRefreshLayout.getLayoutParams();
            swipeRefreshLayoutParams.height = (int) (displayMetrics.heightPixels * 0.35);
            mSwipeRefreshLayout.requestLayout();

            if (nextDeparture <= 0)
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15 * 1000, quickPlannerPendingIntent);
            else
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15 * 1000, realTimeEtdPendingIntent);
        } else {
            Log.i(TAG, "BART is offline for the night...");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

            String offlineStatus;
            if (dayOfWeek == Calendar.SUNDAY)
                offlineStatus = String.format(getResources().getString(R.string.offline_status), "8:00");
            else if (dayOfWeek == Calendar.SATURDAY)
                offlineStatus = String.format(getResources().getString(R.string.offline_status), "6:00");
            else
                offlineStatus = String.format(getResources().getString(R.string.offline_status), "4:00");

            setOverviewVisibility(View.GONE);
            setOfflineLayout(offlineStatus);
        }
    }

    private void setNextDepartureProgressBar(int seconds) {
//        Log.v(TAG, "Next departure: " + seconds);
        seconds = (seconds <= 0) ? 0 : seconds;
        ObjectAnimator nextDepartureAnimator = ObjectAnimator.ofInt(mNextDepartureProgressBar, "progress", seconds, 0);
        nextDepartureAnimator.setDuration(seconds * 1000);
        nextDepartureAnimator.setInterpolator(new LinearInterpolator());
        nextDepartureAnimator.setAutoCancel(true);
        nextDepartureAnimator.start();
        mNextDeparture.setTextSize(42);

        if (nextDepartureCountdown != null) nextDepartureCountdown.cancel();
        nextDepartureCountdown = new CountDownTimer(seconds * 1000, 1000) {
            public void onTick(long millisUntilFinished) {
                String timeLeft = String.format(Locale.US, "%d:%02d", millisUntilFinished / (60 * 1000), millisUntilFinished / 1000 % 60);
                mNextDeparture.setText(timeLeft);
            }

            public void onFinish() {
                mNextDeparture.setTextSize(30);
                mNextDeparture.setText(R.string.status_leaving);
            }
        }.start();
    }

    private void setOfflineLayout(String text) {
        mTripHeader.setVisibility(View.VISIBLE);
        mEmptyTripSchedules.setText(text);
        mEmptyTripSchedules.setVisibility(View.VISIBLE);
        invalidateOptionsMenu();
    }

    private void setOverviewVisibility(int visibility) {
        mTripHeader.setVisibility(visibility);
        mTripSchedules.setVisibility(visibility);
        mNextDepartureProgressBar.setVisibility(visibility);
        mNextDeparture.setVisibility(visibility);
        mNextDepartureWindow.setVisibility(visibility);
        mTripFare.setVisibility(visibility);
        findViewById(R.id.trip_fare_info).setVisibility(visibility);
    }

    private void showAdvisory() {
        if (!advisoryFragment.isAdded()) advisoryFragment.show(fragmentManager, "Trip Advisory Fragment");
    }

    private void showTwitter(String username) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + username)));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + username)));
        }
    }

    private void toggleFavorite() {
        if (!isTripSet() || mTripHeader.getVisibility() == View.GONE) return;
        if (favoriteTrip == -1) favoriteTrip = drawerFragment.isFavoriteTrip(origAbbr, destAbbr);

        if (favoriteTrip == 0) {
            ContentValues values = new ContentValues();
            values.put(BartRiderContract.Favorites.Column.ORIG_ABBR, origAbbr);
            values.put(BartRiderContract.Favorites.Column.ORIG_FULL, origFull);
            values.put(BartRiderContract.Favorites.Column.DEST_ABBR, destAbbr);
            values.put(BartRiderContract.Favorites.Column.DEST_FULL, destFull);

            Uri uri = getContentResolver().insert(BartRiderContract.Favorites.CONTENT_URI, values);
//            if (uri != null) {
//                Log.v(TAG, String.format("Added to favorites: %s - %s", origAbbr, destAbbr));
//            }
            favoriteTrip = Integer.parseInt(uri.getLastPathSegment());
        } else {
            Uri uri = Uri.parse(BartRiderContract.Favorites.CONTENT_URI + "/" + favoriteTrip);
            getContentResolver().delete(uri, null, null);
            favoriteTrip = 0;
        }
        invalidateOptionsMenu();
    }

    private void setRecentTrip() {
        if (isTripSet() && !drawerFragment.isRecentTrip(origAbbr, destAbbr)) {
            Cursor c = getContentResolver().query(BartRiderContract.Recents.CONTENT_URI,
                    new String[] {BartRiderContract.Recents.Column.ID} , null, null, null);
            if (c != null && c.getCount() > 2) {
                c.moveToLast();
                Uri uri = Uri.parse(BartRiderContract.Recents.CONTENT_URI + "/" + c.getInt(c.getColumnIndex(BartRiderContract.Recents.Column.ID)));
                getContentResolver().delete(uri, null, null);
                c.close();
            }

            ContentValues values = new ContentValues();
            values.put(BartRiderContract.Recents.Column.ORIG_ABBR, origAbbr);
            values.put(BartRiderContract.Recents.Column.ORIG_FULL, origFull);
            values.put(BartRiderContract.Recents.Column.DEST_ABBR, destAbbr);
            values.put(BartRiderContract.Recents.Column.DEST_FULL, destFull);

            Uri uri = getContentResolver().insert(BartRiderContract.Recents.CONTENT_URI, values);
//            if (uri != null) {
//                Log.v(TAG, String.format("Added to recents: %s - %s", origAbbr, destAbbr));
//            }
        }
    }

    private void cancelAlarms() {
        Log.i(TAG, "Cancelling all alarms...");
        if (quickPlannerPendingIntent != null) {
            Log.d(TAG, "Cancel quickPlanner alarm...");
            alarmManager.cancel(quickPlannerPendingIntent);
            quickPlannerPendingIntent.cancel();
        }
        if (realTimeEtdPendingIntent != null) {
            Log.d(TAG, "Cancel realTimeEtd alarm...");
            alarmManager.cancel(realTimeEtdPendingIntent);
            realTimeEtdPendingIntent.cancel();
        }
    }

    public boolean isTripSame(String orig, String dest) {
        return isTripSet() && orig.equals(origAbbr) && dest.equals(destAbbr);
    }

    public boolean isTripSet() {
        return !(origAbbr == null || destAbbr == null);
    }

    public boolean isTripValid(String orig, String dest) {
        if (orig == null || dest == null) return false;
        if (orig.equals(dest)) {
            Toast.makeText(this, "Origin and destination cannot be the same.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
