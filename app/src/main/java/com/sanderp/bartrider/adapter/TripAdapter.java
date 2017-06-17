package com.sanderp.bartrider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.pojo.quickplanner.Trip;
import com.sanderp.bartrider.view.TrainRouteView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for presenting trip overall schedules in a list view.
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
        TrainRouteView mTrainRoute = (TrainRouteView) rowView.findViewById(R.id.train_route);

        Trip trip = (Trip) getItem(position);
        mOrigTime.setText(df.format(trip.getEtdOrigTime()));
        mDestTime.setText(df.format(trip.getEtdDestTime()));
        mTrainRoute.setTrainRoutes(trip.getLegs().size(), trip.getRouteColors());

        return rowView;
    }
}
