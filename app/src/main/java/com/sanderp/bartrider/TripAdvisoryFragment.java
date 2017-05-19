package com.sanderp.bartrider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sanderp.bartrider.utility.PrefContract;

import java.util.Set;

/**
 * A simple {@link DialogFragment} subclass.
 */
public class TripAdvisoryFragment extends DialogFragment {
    private static final String TAG = "TripAdvisoryFragment";

    private SharedPreferences sharedPrefs;
    private TextView mAdvisory;

    public TripAdvisoryFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
    }

    public static TripAdvisoryFragment newInstance() {
        TripAdvisoryFragment fragment = new TripAdvisoryFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.trip_advisory_fragment, container, false);

        sharedPrefs = getActivity().getSharedPreferences(PrefContract.PREFS_NAME, 0);
        mAdvisory = (TextView) view.findViewById(R.id.advisory);
        mAdvisory.setText(sharedPrefs.getString(PrefContract.ADVISORY, getResources().getString(R.string.default_advisory)));

        return view;
    }

    @Override
    public void onResume() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        getDialog().getWindow().setLayout((int) (displayMetrics.widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        super.onResume();
    }
}
