package com.sanderp.bartrider;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sanderp.bartrider.adapter.TripAdapter;
import com.sanderp.bartrider.asynctask.AdvisoryAsyncTask;
import com.sanderp.bartrider.asynctask.AsyncTaskResponse;
import com.sanderp.bartrider.asynctask.QuickPlannerAsyncTask;
import com.sanderp.bartrider.asynctask.StationListAsyncTask;
import com.sanderp.bartrider.database.BartRiderContract;
import com.sanderp.bartrider.structure.Trip;

import java.util.List;

/**
 * Created by Sander Peerna on 8/23/2015.
 */
public class TripOverviewActivity extends AppCompatActivity
        implements TripPlannerFragment.OnFragmentListener, TripDrawerFragment.OnFragmentListener {
    private static final String TAG = "TripOverviewActivity";

    private static final String PREFS_NAME = "BartRiderPrefs";
    private static final String FIRST_RUN = "FIRST_RUN";

    private FragmentManager fragmentManager;
    private TripPlannerFragment plannerFragment;
    private TripDrawerFragment drawerFragment;
    private List<Trip> trips;
    private SharedPreferences prefs;

    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private FloatingActionButton mFab;
    private Drawable mDrawable;
    private ListView mListView;
    private RelativeLayout mRelativeLayout;
    private TextView mTextView;
    private Toolbar mToolbar;

    private int favoriteTrip;
    private String origAbbr;
    private String origFull;
    private String destAbbr;
    private String destFull;

    public TripOverviewActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_overview);

        // Set the toolbar to replace the ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setHomeButtonEnabled(true);

        fragmentManager = getSupportFragmentManager();
        drawerFragment = (TripDrawerFragment) fragmentManager.findFragmentById(R.id.trip_planner_drawer_fragment);
        plannerFragment = (TripPlannerFragment) fragmentManager.findFragmentById(R.id.trip_planner_fragment);

        // MAIN ACTIVITY
        prefs = getSharedPreferences(PREFS_NAME, 0);
        if (prefs.getBoolean(FIRST_RUN, true) && isNetworkConnected()) {
            new StationListAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object output) {
                    prefs.edit().putBoolean(FIRST_RUN, false).commit();
                }
            }, this).execute();
        }

        // Open the TripDetailActivity based on the list item that was clicked
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
                showPlannerFragment();
            }
        });

        mRelativeLayout = (RelativeLayout) findViewById(R.id.trip_overview_layout);
        mRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hidePlannerFragment();
            }
        });

        hidePlannerFragment();
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
        mDrawable = menu.getItem(0).getIcon();
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
                updateTripResults();
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfirm(String origFull, String origAbbr, String destFull, String destAbbr) {
        updateTrip(origFull, origAbbr, destFull, destAbbr);
        updateFavoriteIcon(0);
        hidePlannerFragment();
    }

    @Override
    public void onCancel() {
        hidePlannerFragment();
    }

    @Override
    public void onFavoriteClick(int id, String origAbbr, String origFull, String destAbbr, String destFull) {
        updateTrip(origAbbr, origFull, destAbbr, destFull);
        updateFavoriteIcon(id);
        mDrawerLayout.closeDrawer(GravityCompat.START);
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
            updateFavoriteIcon(-1);
        }
    }

    private void updateFavoriteIcon(int id) {
        favoriteTrip = ((id == 0) ? favoriteTrip = drawerFragment.isFavoriteTrip(origAbbr, destAbbr) : id);

        if (favoriteTrip <= 0) mDrawable.clearColorFilter();
        else mDrawable.setColorFilter(ContextCompat.getColor(this, R.color.bart_primary2), PorterDuff.Mode.SRC_ATOP);
    }

    private void updateTrip(String origAbbr, String origFull, String destAbbr, String destFull) {
        if (isValidTrip(origAbbr, destAbbr)) {
            this.origAbbr = origAbbr;
            this.origFull = origFull;
            this.destAbbr = destAbbr;
            this.destFull = destFull;
            updateTripResults();
        }
    }

    private void updateTripResults() {
        if (isTripSet() && isNetworkConnected()) {
            new QuickPlannerAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object result) {
                    trips = (List<Trip>) result;
                    for (Trip t : trips) {
                        t.setOrigFull(origFull);
                        t.setDestFull(destFull);
                    }

                    Trip header = new Trip();
                    header.setOrigFull(origFull);
                    header.setDestFull(destFull);
                    trips.add(0, header);

                    TripAdapter adapter = new TripAdapter(TripOverviewActivity.this, trips);
                    mListView.setAdapter(adapter);
                }
            }).execute(origAbbr, destAbbr);
            updateAdvisories();
        }
    }

    private void updateAdvisories() {
        if (isNetworkConnected()) {
            new AdvisoryAsyncTask(new AsyncTaskResponse() {
                @Override
                public void processFinish(Object result) {
                    String advisory_text = "";
                    for (String s : (List<String>) result) {
                        advisory_text = advisory_text + "\n\n" + s;
                    }
                    Log.d(TAG, advisory_text);
                    mTextView.setText(advisory_text.trim());
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
            Toast.makeText(getApplicationContext(), "No network connection.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean isTripSet() {
        if (origAbbr == null || destAbbr == null) {
            Toast.makeText(getApplicationContext(), "Please select stations in the trip planner.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isValidTrip(String origStation, String destStation) {
        if (origStation.equals(destStation)) {
            Toast.makeText(getApplicationContext(), "Origin and destination cannot be the same.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void hidePlannerFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.hide(plannerFragment);
        transaction.commit();
        mFab.show();
    }

    private void showPlannerFragment() {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.show(plannerFragment);
        transaction.commit();
        mFab.hide();
    }
}
