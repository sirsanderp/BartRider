package com.sanderp.bartrider;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.sanderp.bartrider.database.BartRiderContract;

/**
 * A simple {@link DialogFragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentListener} interface
 * to handle interaction events.
 */
public class TripPlannerFragment extends DialogFragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "TripPlannerFragment";

    public static final int LOADER_ID = 0;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final String[] PROJECTION = {
            BartRiderContract.Stations.Column.ID,
            BartRiderContract.Stations.Column.NAME,
            BartRiderContract.Stations.Column.ABBREVIATION,
            BartRiderContract.Stations.Column.LATITUDE,
            BartRiderContract.Stations.Column.LONGITUDE
    };
    private static final String[] FROM = {BartRiderContract.Stations.Column.NAME};
    private static final int [] TO = {android.R.id.text1};

    private boolean mLocationPermissionGranted;
    private Button mConfirm;
    private Button mCancel;
    private Cursor mCursor;
    private ImageView mLocation;
    private SimpleCursorAdapter mAdapter;
    private Spinner mOrigSpinner;
    private Spinner mDestSpinner;

    private String origAbbr;
    private String destAbbr;

    private FusedLocationProviderClient mFusedLocationClient;
//    private GoogleApiClient mGoogleApiClient;
//    private Location mLastKnownLocation;
    private OnFragmentListener mFragmentListener;

    public interface OnFragmentListener {
        void onConfirm(String origAbbr, String origFull, String destAbbr, String destFull);
    }

    public TripPlannerFragment() {}

    public static TripPlannerFragment newInstance() {
        TripPlannerFragment fragment = new TripPlannerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, 0);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.trip_planner_fragment, container, false);

//        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();

        // Initialize buttons
        mConfirm = (Button) view.findViewById(R.id.confirm);
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFragmentListener.onConfirm(
                        getAbbr((Cursor) mOrigSpinner.getSelectedItem()),
                        getFull((Cursor) mOrigSpinner.getSelectedItem()),
                        getAbbr((Cursor) mDestSpinner.getSelectedItem()),
                        getFull((Cursor) mDestSpinner.getSelectedItem())
                );
//                resetSpinners();
                dismiss();
            }
        });
        mCancel = (Button) view.findViewById(R.id.cancel);
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                resetSpinners();
                dismiss();
            }
        });
        mLocation = (ImageView) view.findViewById(R.id.my_location);
        mLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setNearestStation();
//                mGoogleApiClient.connect();
            }
        });

        // Initialize spinners
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.simple_spinner_item, null, FROM, TO, 0);
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        mOrigSpinner = (Spinner) view.findViewById(R.id.orig_spinner);
        mOrigSpinner.setAdapter(mAdapter);
        mDestSpinner = (Spinner) view.findViewById(R.id.dest_spinner);
        mDestSpinner.setAdapter(mAdapter);
        getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;
    }

    @Override
    public void onResume() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.rounded_corners);
        getDialog().getWindow().setLayout((int) (displayMetrics.widthPixels * 0.95), ViewGroup.LayoutParams.WRAP_CONTENT);
        super.onResume();
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
//        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
    }

//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//
//        if (mLocationPermissionGranted) {
//            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//            mLastKnownLocation.getLatitude();
//            mLastKnownLocation.getLongitude();
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int cause) {
//        Log.d(TAG, "Play services connection suspended");
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult result) {
//        Log.d(TAG, "Play services connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
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
        if (loader.getId() == LOADER_ID && data != null) {
//            Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
            mAdapter.swapCursor(data);
            mCursor = data;
//            setSpinnersToTrip();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_ID) mAdapter.swapCursor(null);
    }

    private void setNearestStation() {
        Log.i(TAG, "Setting nearest location...");
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (mLocationPermissionGranted) {
            mFusedLocationClient.getLastLocation().addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        mCursor.moveToFirst();
                        double deviceLat = location.getLatitude();
                        double deviceLon = location.getLongitude();
                        Log.v(TAG, "dLat: " + deviceLat + " dLon: " + deviceLon);
                        int nearestStationId = 1;
                        double nearestStationDist = 1;
                        while (!mCursor.isAfterLast()) {
                            double stationLat = Double.parseDouble(getLatitude(mCursor));
                            double stationLon = Double.parseDouble(getLongitude(mCursor));
                            Log.v(TAG, "sLat: " + stationLat + " sLon: " + stationLon);
                            double distance = Math.sqrt(Math.pow((deviceLat - stationLat), 2) + Math.pow((deviceLon - stationLon), 2));
                            Log.v(TAG, "Station: " + getAbbr(mCursor) + " Distance: " + distance);
                            if (distance < nearestStationDist) {
                                nearestStationId = getId(mCursor);
                                nearestStationDist = distance;
                            }
                            mCursor.moveToNext();
                        }
                        Log.v(TAG, "Nearest Station ID: " + nearestStationId);
                        Log.v(TAG, "Nearest Station Distance: " + nearestStationDist);
                        mOrigSpinner.setSelection(nearestStationId - 1);
                    } else {
                        Toast.makeText(getContext(), "Location unavailable.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private int getId(Cursor c) {
        return c.getInt(c.getColumnIndex(BartRiderContract.Stations.Column.ID));
    }

    private String getAbbr(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.ABBREVIATION));
    }

    private String getFull(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.NAME));
    }

    private String getLatitude(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.LATITUDE));
    }

    private String getLongitude(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Stations.Column.LONGITUDE));
    }

    private void resetSpinners() {
        mOrigSpinner.setSelection(0);
        mDestSpinner.setSelection(0);
    }

    public void setSpinners(String origAbbr, String destAbbr) {
        this.origAbbr = origAbbr;
        this.destAbbr = destAbbr;
    }

    private void setSpinnersToTrip() {
        if (origAbbr != null || destAbbr != null) {
            Cursor cOrig = getContext().getContentResolver().query(BartRiderContract.Stations.CONTENT_URI, PROJECTION,
                    BartRiderContract.Stations.Column.ABBREVIATION + "=?", new String[] {origAbbr}, BartRiderContract.Stations.DEFAULT_SORT);
            if (cOrig != null) {
                cOrig.moveToFirst();
                mOrigSpinner.setSelection(cOrig.getInt(cOrig.getColumnIndex(BartRiderContract.Stations.Column.ID)) - 1);
                cOrig.close();
            }

            Cursor cDest = getActivity().getContentResolver().query(BartRiderContract.Stations.CONTENT_URI, PROJECTION,
                    BartRiderContract.Stations.Column.ABBREVIATION + "=?", new String[] {destAbbr}, BartRiderContract.Stations.DEFAULT_SORT);
            if (cDest != null) {
                cDest.moveToFirst();
                mDestSpinner.setSelection(cDest.getInt(cDest.getColumnIndex(BartRiderContract.Stations.Column.ID)) - 1);
                cDest.close();
            }
        }
    }
}
