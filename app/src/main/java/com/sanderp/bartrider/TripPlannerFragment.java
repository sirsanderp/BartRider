package com.sanderp.bartrider;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.sanderp.bartrider.database.BartRiderContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentListener} interface
 * to handle interaction events.
 */
public class TripPlannerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "TripPlannerFragment";

    public static final int LOADER_ID = 0;
    private static final String[] PROJECTION = {
            BartRiderContract.Stations.Column.ID,
            BartRiderContract.Stations.Column.NAME,
            BartRiderContract.Stations.Column.ABBREVIATION
    };
    private static final String[] FROM = {BartRiderContract.Stations.Column.NAME};
    private static final int [] TO = {android.R.id.text1};

    private Button mConfirm;
    private Button mCancel;
    private SimpleCursorAdapter mAdapter;
    private Spinner mOrigSpinner;
    private Spinner mDestSpinner;

    private OnFragmentListener mFragmentListener;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentListener {
        void onConfirm(String origFull, String origAbbr, String destFull, String destAbbr);
        void onCancel();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_planner_fragment, container, false);

        // Initialize buttons
        mConfirm = (Button) view.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentListener.onConfirm(
                        getOrigAbbr(),
                        getOrigFull(),
                        getDestAbbr(),
                        getDestFull()
                );
                resetSpinners();
            }
        });
        mCancel = (Button) view.findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentListener.onCancel();
                resetSpinners();
            }
        });

        // Initialize spinners
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, null, FROM, TO, 0);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mOrigSpinner = (Spinner) view.findViewById(R.id.orig_spinner);
        mOrigSpinner.setAdapter(mAdapter);
        mDestSpinner = (Spinner) view.findViewById(R.id.dest_spinner);
        mDestSpinner.setAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mFragmentListener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == LOADER_ID) {
            return new CursorLoader(getActivity(), BartRiderContract.Stations.CONTENT_URI,
                    PROJECTION, null, null, BartRiderContract.Stations.DEFAULT_SORT);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID && data != null && data.getCount() > 0) {
            Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_ID) mAdapter.swapCursor(null);
    }

    private String getOrigAbbr() {
        Cursor c = (Cursor) mOrigSpinner.getSelectedItem();
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.ABBREVIATION));
    }

    private String getOrigFull() {
        Cursor c = (Cursor) mOrigSpinner.getSelectedItem();
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.NAME));
    }

    private String getDestAbbr() {
        Cursor c = (Cursor) mDestSpinner.getSelectedItem();
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.ABBREVIATION));
    }

    private String getDestFull() {
        Cursor c = (Cursor) mDestSpinner.getSelectedItem();
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.NAME));
    }

    private void resetSpinners() {
        mOrigSpinner.setSelection(0);
        mDestSpinner.setSelection(0);
    }
}
