package com.sanderp.bartrider;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.TextView;

import com.sanderp.bartrider.adapter.TripLegAdapter;
import com.sanderp.bartrider.structure.Trip;

public class TripDetailActivity extends AppCompatActivity {
    private static final String TAG = "TripDetailActivity";

    public static final String ORIG = "origin";
    public static final String DEST = "destination";
    public static final String TRIP = "trip";

    private ListView mListView;
    private TextView mOrig;
    private TextView mDest;
    private TextView mCo2;
    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_detail);

        // Set up Toolbar to replace ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Trip Details");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        setSupportActionBar(mToolbar);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeButtonEnabled(true);

        mOrig = (TextView) findViewById(R.id.trip_orig);
        mDest = (TextView) findViewById(R.id.trip_dest);
        mCo2 = (TextView) findViewById(R.id.trip_co2);
        mListView = (ListView) findViewById(R.id.trip_detail_list_view);

        Intent data= this.getIntent();
        Trip trip = (Trip) data.getSerializableExtra(TRIP);
        mOrig.setText(data.getStringExtra(ORIG));
        mDest.setText(data.getStringExtra(DEST));
        mCo2.setText("CO2 Saved: " + trip.getCo2());

        TripLegAdapter adapter = new TripLegAdapter(TripDetailActivity.this, trip.getTripLegs());
        mListView.setAdapter(adapter);
    }
}
