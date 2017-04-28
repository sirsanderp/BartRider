package com.sanderp.bartrider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.pojo.quickplanner.Trip;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sander Peerna on 11/3/2016.
 */
public class TripAdapter extends BaseAdapter {
    private static final String TAG = "TripAdapter";

    private static final SimpleDateFormat df = new SimpleDateFormat("h:mm a", Locale.US);

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
        View rowView = mInflater.inflate(R.layout.trip_overview_list_item, parent, false);
        TextView mOrigTime = (TextView) rowView.findViewById(R.id.trip_orig_time);
        TextView mDestTime = (TextView) rowView.findViewById(R.id.trip_dest_time);
        TextView mFare = (TextView) rowView.findViewById(R.id.trip_fare);

        Trip trip = (com.sanderp.bartrider.pojo.quickplanner.Trip) getItem(position);
        mOrigTime.setText(df.format(trip.getEtdOrigTimeMin()));
        mDestTime.setText(df.format(trip.getEtdDestTimeMin()));
        mFare.setText(String.format("$%.2f", trip.getFare()));

        return rowView;
    }
}
