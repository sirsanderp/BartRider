package com.sanderp.bartrider;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.sanderp.bartrider.adapter.TripLegAdapter;
import com.sanderp.bartrider.structure.Trip;

public class TripDetailActivity extends AppCompatActivity {
    private static final String TAG = "TripDetailActivity";

    private TextView mOrig;
    private TextView mDest;
    private TextView mCo2;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trip_detail);

        mOrig = (TextView) findViewById(R.id.trip_orig);
        mDest = (TextView) findViewById(R.id.trip_dest);
        mCo2 = (TextView) findViewById(R.id.trip_co2);
        mListView = (ListView) findViewById(R.id.trip_detail_list_view);

        Trip trip = (Trip) this.getIntent().getSerializableExtra("trip");
        mOrig.setText(trip.getOrigFull());
        mDest.setText(trip.getDestFull());
        mCo2.setText("CO2 Saved: " + trip.getCo2());

        TripLegAdapter adapter = new TripLegAdapter(TripDetailActivity.this, trip.getTripLegs());
        mListView.setAdapter(adapter);
    }
}
