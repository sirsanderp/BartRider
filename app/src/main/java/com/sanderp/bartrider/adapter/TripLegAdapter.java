package com.sanderp.bartrider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.structure.Trip;

import java.util.List;

/**
 * Created by Sander Peerna on 11/16/2016.
 */

public class TripLegAdapter extends BaseAdapter {
    private static final String TAG = "TripLegAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Trip.TripLeg> mDataSource;

    public TripLegAdapter(Context context, List<Trip.TripLeg> tripLegs) {
        mContext = context;
        mDataSource = tripLegs;
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
        View rowView = mInflater.inflate(R.layout.trip_detail_list_item, parent, false);
        TextView mOrigName = (TextView) rowView.findViewById(R.id.trip_leg_orig_name);
        TextView mOrigTime = (TextView) rowView.findViewById(R.id.trip_leg_orig_time);
        TextView mDestName = (TextView) rowView.findViewById(R.id.trip_leg_dest_name);
        TextView mDestTime = (TextView) rowView.findViewById(R.id.trip_leg_dest_time);

        Trip.TripLeg tripLeg = (Trip.TripLeg) getItem(position);
        mOrigName.setText(tripLeg.getOrigin());
        mOrigTime.setText(tripLeg.getOrigTimeMin());
        mDestName.setText(tripLeg.getDestination());
        mDestTime.setText(tripLeg.getDestTimeMin());

        return rowView;
    }
}
