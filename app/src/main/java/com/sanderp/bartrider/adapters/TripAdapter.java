package com.sanderp.bartrider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.structure.Trip;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by Sander Peerna on 11/3/2016.
 */

public class TripAdapter extends BaseAdapter {
    private static final String TAG = "TripAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Trip> mDataSource;

    public TripAdapter(Context context, List<Trip> trips) {
        mContext = context;
        mDataSource = trips;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mDataSource.size();
    }

    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Trip trip = (Trip) getItem(position);
        View rowView;
        if (position == 0) {
            rowView = mInflater.inflate(R.layout.trip_overview_list_item_separator, parent, false);
            TextView mTripSeparator = (TextView) rowView.findViewById(R.id.trip_separator);

            mTripSeparator.setText(trip.getOrigFullName() + " - " + trip.getDestFullName());
        } else {
            rowView = mInflater.inflate(R.layout.trip_overview_list_item, parent, false);
            TextView mOrigTime = (TextView) rowView.findViewById(R.id.trip_orig_time);
            TextView mDestTime = (TextView) rowView.findViewById(R.id.trip_dest_time);
            TextView mFare = (TextView) rowView.findViewById(R.id.trip_fare);

            mOrigTime.setText(trip.getOrigTimeMin());
            mDestTime.setText(trip.getDestTimeMin());
            mFare.setText("$" + new DecimalFormat("#.00").format(trip.getFare()));
        }

        return rowView;
    }
}
