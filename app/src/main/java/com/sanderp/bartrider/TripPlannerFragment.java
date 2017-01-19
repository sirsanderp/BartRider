package com.sanderp.bartrider;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.sanderp.bartrider.database.StationContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentListener} interface
 * to handle interaction events.
 */
public class TripPlannerFragment extends Fragment {
    private static final String TAG = "TripPlannerFragment";

    private static final String[] FROM = {StationContract.Column.NAME};
    private static final int [] TO = {android.R.id.text1};

    private FragmentManager fragmentManager;
    private TripPlannerFragment fragment;

    private Button mConfirm;
    private Button mCancel;
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
        void onConfirm();
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

        fragmentManager = getFragmentManager();
        fragment = (TripPlannerFragment) fragmentManager.findFragmentById(R.id.trip_planner_fragment);

        // Initialize buttons
        mConfirm = (Button) view.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentListener.onConfirm();
            }
        });
        mCancel = (Button) view.findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentListener.onCancel();
            }
        });

        // Initialize spinners
        mOrigSpinner = (Spinner) view.findViewById(R.id.orig_spinner);
        mDestSpinner = (Spinner) view.findViewById(R.id.dest_spinner);

        // Populate the spinners with stations
        String[] projection = {StationContract.Column.ID, StationContract.Column.NAME, StationContract.Column.ABBREVIATION};

        Cursor c = getActivity().getContentResolver().query(StationContract.CONTENT_URI, projection,
                null, null, StationContract.DEFAULT_SORT);

        Log.d(TAG, DatabaseUtils.dumpCursorToString(c));

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_spinner_item, c, FROM, TO, 0);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mOrigSpinner.setAdapter(adapter);
        mDestSpinner.setAdapter(adapter);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentListener) {
            mFragmentListener = (OnFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragmentListener = null;
    }
}
