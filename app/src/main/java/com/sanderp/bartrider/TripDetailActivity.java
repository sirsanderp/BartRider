package com.sanderp.bartrider;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.sanderp.bartrider.adapter.TripLegAdapter;
import com.sanderp.bartrider.pojo.quickplanner.Trip;

/**
 * Displays in-depth details about the selected trip.
 */
public class TripDetailActivity extends AppCompatActivity {
    private static final String TAG = "TripDetailActivity";

    public static final String ORIG = "origin";
    public static final String DEST = "destination";
    public static final String TRIP = "trip";
    public static final String TRIP_LEG = "tripLeg";

    private ListView mListView;
    private TextView mCo2;
    private Toolbar mToolbar;
//    private TrainRouteView mTrainRoute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_detail);

        // Set up Toolbar to replace ActionBar.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("Trip Details");
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mListView = (ListView) findViewById(R.id.trip_detail_list_view);
        mCo2 = (TextView) findViewById(R.id.trip_co2);
//        mTrainRoute = (TrainRouteView) findViewById(R.id.train_route);

        Intent data = getIntent();
        Trip trip = (Trip) data.getSerializableExtra(TRIP);
        mCo2.setText(String.format(getResources().getString(R.string.co2_saved), trip.getCo2()));
//        mTrainRoute.setTrainRoutes(trip.getLegs().size(), trip.getRouteColors());

        TripLegAdapter adapter = new TripLegAdapter(this, trip.getLegs(), trip.getRouteColors());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent tripDetailIntent = new Intent(TripDetailActivity.this, TripOverviewActivity.class)
                        .putExtra(TripDetailActivity.TRIP_LEG, ((Trip) getIntent().getSerializableExtra(TRIP)).getLeg(position));
                startActivity(tripDetailIntent);
            }
        });
    }
}
