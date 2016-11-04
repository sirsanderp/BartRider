package com.sanderp.bartrider.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.R;
import com.sanderp.bartrider.structure.Departure;

import java.util.List;

/**
 * Created by Sander Peerna on 11/3/2016.
 */

public class QuickPlannerDepartureAdapter extends BaseAdapter {
    private static final String TAG = "QuickPlannerDepartureAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Departure> mDataSource;

    public QuickPlannerDepartureAdapter(Context context, List<Departure> departures) {
        mContext = context;
        mDataSource = departures;
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
        View rowView = mInflater.inflate(R.layout.list_item, parent, false);

        // Initialize text fields
        TextView mOrigTime = (TextView) rowView.findViewById(R.id.destination);
        TextView mDestTime = (TextView) rowView.findViewById(R.id.platform);
        TextView mFare = (TextView) rowView.findViewById(R.id.minutes);

        Departure d = (Departure) getItem(position);
        mOrigTime.setText(d.originTime);
        mDestTime.setText(d.destinationTime);
        mFare.setText(d.fare);

        return rowView;
    }
}
