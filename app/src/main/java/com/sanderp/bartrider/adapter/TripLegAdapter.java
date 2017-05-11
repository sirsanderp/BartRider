package com.sanderp.bartrider.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.pojo.quickplanner.Leg;
import com.sanderp.bartrider.view.TrainRouteView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Sander Peerna on 11/16/2016.
 */

public class TripLegAdapter extends BaseAdapter {
    private static final String TAG = "TripLegAdapter";

    private static final SimpleDateFormat df = new SimpleDateFormat("h:mm a", Locale.US);

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Leg> mDataSource;

    private int[] colors;

    public TripLegAdapter(Context context, List<Leg> tripLegs, int[] colors) {
        mContext = context;
        mDataSource = tripLegs;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.colors = colors;
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
        TrainRouteView mTrainRoute = (TrainRouteView) rowView.findViewById(R.id.train_route);

        Leg tripLeg = (Leg) getItem(position);
        mOrigName.setText(tripLeg.getOrigin());
        mDestName.setText(tripLeg.getDestination());
        mOrigTime.setText(df.format(tripLeg.getEtdOrigTimeMin()));
        mDestTime.setText(df.format(tripLeg.getEtdDestTimeMin()));
        mTrainRoute.setTrainRoutes(1, new int[]{colors[position]});

        return rowView;
    }
}
