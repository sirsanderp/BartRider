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
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanderp.bartrider.adapter.TripAdapter;
import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.StationListAsyncTask;
import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.intentservice.QuickPlannerService;
import com.sanderp.bartrider.intentservice.RealTimeEtdService;
import com.sanderp.bartrider.pojo.quickplanner.Fare;
import com.sanderp.bartrider.pojo.quickplanner.Leg;
import com.sanderp.bartrider.pojo.quickplanner.QuickPlannerPojo;
import com.sanderp.bartrider.pojo.quickplanner.Request;
import com.sanderp.bartrider.pojo.quickplanner.Trip;
import com.sanderp.bartrider.pojo.realtimeetd.RealTimeEtdPojo;
import com.sanderp.bartrider.utility.Constants;
import com.sanderp.bartrider.utility.PrefContract;
import com.sanderp.bartrider.utility.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class TripOverviewActivity extends AppCompatActivity
        implements TripPlannerFragment.OnFragmentListener, TripDrawerFragment.OnFragmentListener {
    private static final String TAG = "TripOverviewActivity";

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
    private ProgressBar mNextDepartureProgressBar;
    private TextView mNextDeparture;
    private TextView mNextDepartureWindow;
    private TextView mTripHeader;
    private TextView mTripFare;
    private Toolbar mToolbar;

    private List<Trip> currTrips;
    private List<Trip> trips;

    private int favoriteTrip;
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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);

        // Set up Toolbar to replace ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Set up advisory notifications and station database on first run.
        sharedPrefs = getSharedPreferences(PrefContract.PREFS_NAME, 0);
        if (sharedPrefs.getBoolean(PrefContract.FIRST_RUN, true)) {
            Log.i(TAG, "First run setup...");
            sendBroadcast(new Intent(Constants.Broadcast.ADVISORY_SERVICE));
            new StationListAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object output) {
                    sharedPrefs.edit().putBoolean(PrefContract.FIRST_RUN, false).apply();
                }
            }, this).execute();
        }

        // Configurations for scheduling repeating jobs.
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received broadcast.");
                if (intent.getAction().equals(Constants.Broadcast.QUICK_PLANNER_SERVICE)) {
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
                plannerFragment.show(fragmentManager, "Trip Planner Fragment");
            }
        });

        if (sharedPrefs.getBoolean(PrefContract.LAST_TRIP, false)) {
            Log.i(TAG, "onCreate(): Retrieving last trip information...");
            setTrip(
                    sharedPrefs.getString(PrefContract.LAST_ORIG_ABBR, null),
                    sharedPrefs.getString(PrefContract.LAST_ORIG_FULL, null),
                    sharedPrefs.getString(PrefContract.LAST_DEST_ABBR, null),
                    sharedPrefs.getString(PrefContract.LAST_DEST_FULL, null)
            );
            findViewById(R.id.empty_list_item).setVisibility(View.GONE);
        } else {
            mTripSchedules.setEmptyView(findViewById(R.id.empty_list_item));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isTripSet()) {
            Log.i(TAG, "onPause(): Saving last trip information...");
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putBoolean(PrefContract.LAST_TRIP, true)
                    .putInt(PrefContract.LAST_ID, favoriteTrip)
                    .putString(PrefContract.LAST_ORIG_ABBR, origAbbr)
                    .putString(PrefContract.LAST_ORIG_FULL, origFull)
                    .putString(PrefContract.LAST_DEST_ABBR, destAbbr)
                    .putString(PrefContract.LAST_DEST_FULL, destFull)
                    .apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTripSet()) {
            Log.i(TAG, "onResume(): Refreshing trip schedules...");
            getTripSchedules();
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.Broadcast.QUICK_PLANNER_SERVICE));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(Constants.Broadcast.REAL_TIME_ETD_SERVICE));
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop(): Cancelling all alarms...");
        alarmManager.cancel(quickPlannerPendingIntent);
        alarmManager.cancel(realTimeEtdPendingIntent);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestory()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bart_main, menu);
        mAdvisoryIcon = menu.getItem(1).getIcon();
        mFavoriteIcon = menu.getItem(0).getIcon();
        int id = sharedPrefs.getInt(PrefContract.LAST_ID, -1);
        if (id != 0) setFavoriteIcon(id);
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
            case R.id.action_advisory:
                showAdvisory();
                return true;
            case R.id.action_refresh:
                getTripSchedules();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfirm(String origAbbr, String origFull, String destAbbr, String destFull) {
        if (!isTripSame(origAbbr, destAbbr)) {
            setTrip(origAbbr, origFull, destAbbr, destFull);
            setFavoriteIcon(-1);
            getTripSchedules();
        }
    }

    @Override
    public void onFavoriteClick(int id, String origAbbr, String origFull, String destAbbr, String destFull) {
        if (!isTripSame(origAbbr, destAbbr)) {
            setTrip(origAbbr, origFull, destAbbr, destFull);
            setFavoriteIcon(id);
            getTripSchedules();
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }

    private void setTrip(String origAbbr, String origFull, String destAbbr, String destFull) {
        if (isValidTrip(origAbbr, destAbbr)) {
            this.origAbbr = origAbbr;
            this.origFull = origFull;
            this.destAbbr = destAbbr;
            this.destFull = destFull;
            mTripHeader.setText(origFull + " - " + destFull);
        }
    }

    private void setAdvisoryIcon() {
        // TODO: Change color of icon based on the delays.
    }

    private void setFavoriteIcon(int id) {
        favoriteTrip = ((id == -1) ? favoriteTrip = drawerFragment.isFavoriteTrip(origAbbr, destAbbr) : id);

        if (favoriteTrip == 0) mFavoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.material_light), PorterDuff.Mode.SRC_ATOP);
        else mFavoriteIcon.setColorFilter(ContextCompat.getColor(this, R.color.bart_primary2), PorterDuff.Mode.SRC_ATOP);
    }

    private void getTripSchedules() {
        if (isTripSet() && Utils.isNetworkConnected(this)) {
            Intent quickPlannerIntent = new Intent(TripOverviewActivity.this, QuickPlannerService.class);
            quickPlannerIntent.putExtra(QuickPlannerService.ORIG, origAbbr)
                    .putExtra(QuickPlannerService.DEST, destAbbr);
            quickPlannerPendingIntent = PendingIntent.getService(this, -1, quickPlannerIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            startService(quickPlannerIntent);
        }
    }

    private void onReceiveTripSchedules(Intent intent) {
        Log.d(TAG, "onReceiveTripSchedules(): Received callback from broadcast.");
        QuickPlannerPojo scheduleResults = (QuickPlannerPojo) intent.getSerializableExtra(QuickPlannerService.RESULT);
        trips = scheduleResults.getRoot().getSchedule().getRequest().getTrips();
        getTripRealTimeEtd(scheduleResults.getRoot().getSchedule().getRequest());
    }

    private void getTripRealTimeEtd(Request result) {
        if (isTripSet() && Utils.isNetworkConnected(this)) {
            // Have to use bundle to be able to pass serialized objects through the AlarmManager.
            Bundle bundle = new Bundle();
            bundle.putSerializable(RealTimeEtdService.OBJECT_LIST, result.buildBatchLoad());
            Intent realTimeEtdIntent = new Intent(TripOverviewActivity.this, RealTimeEtdService.class);
            realTimeEtdIntent.putExtra(RealTimeEtdService.OBJECT_LIST, bundle);
            realTimeEtdPendingIntent = PendingIntent.getService(this, -1, realTimeEtdIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            startService(realTimeEtdIntent);
        }
    }

    private void onReceiveTripRealTimeEtd(Intent intent) {
        Log.d(TAG, "onReceiveTripRealTimeEtd(): Received callback from broadcast.");
        HashMap<String, RealTimeEtdPojo> etdResults = (HashMap<String, RealTimeEtdPojo>) intent.getSerializableExtra(RealTimeEtdService.RESULT);
        RealTimeEtdPojo firstTrip = etdResults.get(trips.get(0).getLeg(0).getTrainHeadStation());
        mergeSchedulesAndEtd(etdResults);

        int nextDeparture = firstTrip.getEtdSeconds(0);
        if (!(nextDeparture == 0 && firstTrip.getEtdSeconds().size() == 1)) {
            List<Fare> fares = trips.get(0).getFares().getFare();
            mTripFare.setText(String.format(getResources().getString(R.string.fares), fares.get(0).getAmount(), fares.get(1).getAmount()));
            mTripSchedules.clearAnimation();
            mTripSchedules.setAdapter(new TripAdapter(TripOverviewActivity.this, currTrips));
            setNextDepartureProgressBar(nextDeparture);
            setOverviewVisibility(View.VISIBLE);

            if (nextDeparture == 0)
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15 * 1000, quickPlannerPendingIntent);
            else
                alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 15 * 1000, realTimeEtdPendingIntent);
        } else {
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

    private void mergeSchedulesAndEtd(HashMap<String, RealTimeEtdPojo> etdResults) {
        DateFormat df = new SimpleDateFormat("h:mm a", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        Date now = new Date();
        Log.d(TAG, "Current Time: " + df.format(now));

        currTrips = new ArrayList<>(trips);
        for (int i = 0; i < currTrips.size(); i++) {
            Trip trip = currTrips.get(i);
            Leg tripLeg = trip.getLeg(0);

            String headAbbr = tripLeg.getTrainHeadStation();
            Log.d(TAG, trip.getOrigTimeMin() + " | "  + etdResults.get(headAbbr).getPrevDepartTime());
            Log.d(TAG, trip.getOrigTimeEpoch() + " | "  + etdResults.get(headAbbr).getPrevDepartTimeEpoch());
            if (trip.getOrigTimeEpoch() < etdResults.get(headAbbr).getPrevDepartTimeEpoch()) {
                currTrips.remove(i--);
                continue;
            }

            Log.d(TAG, origAbbr + " -> " + headAbbr);
            int etdMinutes;
            if (!etdResults.get(headAbbr).getEtdMinutes().isEmpty()) {
                etdMinutes = etdResults.get(headAbbr).getEtdMinutes().remove(0);
            } else {
                continue;
            }

            Log.d(TAG, "Until Departure: " + etdMinutes + " minutes");
            long estOrigDeparture = now.getTime() + (etdMinutes * 60 * 1000) - (30 * 1000);
            long estDestArrival = estOrigDeparture + (trip.getTripTime() * 60 * 1000);
            Log.d(TAG, "Trip (planned): " + trip.getOrigTimeMin() + " | " + trip.getDestTimeMin());
            Log.d(TAG, "Trip (estimated): " + df.format(estOrigDeparture) + " | " + df.format(estDestArrival));

            long diffMinutes = ((estOrigDeparture - trip.getOrigTimeEpoch()) / (60 * 1000)) % 60;
            long estLegOrigDeparture = now.getTime() + (etdMinutes * 60 * 1000) - (30 * 1000);
            long estLegDestArrival = tripLeg.getDestTimeEpoch() + (diffMinutes * 60 * 1000);
            Log.d(TAG, "Trip Leg (planned): " + tripLeg.getOrigTimeMin() + " | " + tripLeg.getDestTimeMin());
            Log.d(TAG, "Trip Leg (estimated): " + df.format(estLegOrigDeparture) + " | " + df.format(estLegDestArrival));

            Log.d(TAG, "Difference: " + diffMinutes + " minutes");
            if (diffMinutes >= 1) {
                Log.d(TAG, "Updating trip and trip leg estimated times...");
                trip.setEtdOrigTime(estOrigDeparture);
                trip.setEtdDestTime(estDestArrival);
                tripLeg.setEtdOrigTime(estLegOrigDeparture);
                tripLeg.setEtdDestTime(estLegDestArrival);
            }
        }
    }

    private void setNextDepartureProgressBar(int seconds) {
        mNextDepartureProgressBar.clearAnimation();
        ObjectAnimator animator = ObjectAnimator.ofInt(mNextDepartureProgressBar, "progress", seconds, 0);
        animator.setDuration(seconds * 1000);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

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

    private void showAdvisory() {
        advisoryFragment.show(fragmentManager, "Trip Advisory Fragment");
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
            if (uri != null) {
                Log.d(TAG, uri.toString());
                Log.d(TAG, String.format("Added to favorites: %s - %s", origAbbr, destAbbr));
            }
            setFavoriteIcon(Integer.parseInt(uri.getLastPathSegment()));
        } else {
            Uri uri = Uri.parse(BartRiderContract.Favorites.CONTENT_URI + "/" + favoriteTrip);
            int count = this.getContentResolver().delete(uri, null, null);
            if (count > 0) {
                Log.d(TAG, uri.toString());
                Log.d(TAG, String.format("Removed from favorites: %s - %s", origAbbr, destAbbr));
            }
            setFavoriteIcon(0);
        }
    }

    private void setOverviewVisibility(int visibility) {
        mTripHeader.setVisibility(visibility);
        mTripFare.setVisibility(visibility);
        findViewById(R.id.trip_fare_info).setVisibility(visibility);
        mNextDepartureProgressBar.setVisibility(visibility);
        mNextDeparture.setVisibility(visibility);
        mNextDepartureWindow.setVisibility(visibility);
    }

    private void setOfflineLayout(String text) {
        RelativeLayout.LayoutParams layout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);

        mTripSchedules.setVisibility(View.GONE);
        mNextDeparture.setLayoutParams(layout);
        mNextDeparture.setText(text);
        mNextDeparture.setTextSize(18);
        mNextDeparture.setVisibility(View.VISIBLE);
    }

    private boolean isTripSame(String orig, String dest) {
        return isTripSet() && orig.equals(origAbbr) && dest.equals(destAbbr);
    }

    private boolean isTripSet() {
        return !(origAbbr == null || destAbbr == null);
    }

    private boolean isValidTrip(String orig, String dest) {
        if (orig.equals(dest)) {
            Toast.makeText(this, "Origin and destination cannot be the same.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
