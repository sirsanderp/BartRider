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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.sanderp.bartrider.database.BartRiderContract;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentListener} interface
 * to handle interaction events.
 */
public class TripDrawerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "TripDrawerFragment";

    public static final int LOADER_ID = 1;
    private static final String[] PROJECTION = {
            BartRiderContract.Favorites.Column.ID,
            BartRiderContract.Favorites.Column.ORIG_FULL,
            BartRiderContract.Favorites.Column.ORIG_ABBR,
            BartRiderContract.Favorites.Column.DEST_FULL,
            BartRiderContract.Favorites.Column.DEST_ABBR
    };
    private static final String[] FROM = {
            BartRiderContract.Favorites.Column.ORIG_ABBR,
            BartRiderContract.Favorites.Column.DEST_ABBR
    };
    private static final int [] TO = {
            android.R.id.text1,
            android.R.id.text1
    };

    private Cursor favoritesTable;

    private SimpleCursorAdapter mAdapter;
    private ListView mListView;

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
        void onFavoriteClick(int id, String origFull, String origAbbr, String destFull, String destAbbr);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.trip_drawer_fragment, container, false);

//        favoritesTable = getActivity().getContentResolver().query(BartRiderContract.Favorites.CONTENT_URI,
//                PROJECTION, null, null, BartRiderContract.Favorites.DEFAULT_SORT);
        mAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null, FROM, TO, 0);
        mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text1) {
                    ((TextView) view).setText(getOrigAbbr(cursor) + " - " + getDestAbbr(cursor));
                    return true;
                }
                return false;
            }
        });

        mListView = (ListView) view.findViewById(R.id.favorites_list_view);
        mListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor itemCursor = mAdapter.getCursor();
                mFragmentListener.onFavoriteClick(
                        getColumnId(itemCursor),
                        getOrigAbbr(itemCursor),
                        getOrigFull(itemCursor),
                        getDestAbbr(itemCursor),
                        getDestFull(itemCursor));
            }
        });
        mListView.setAdapter(mAdapter);
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
            return new CursorLoader(getActivity(), BartRiderContract.Favorites.CONTENT_URI,
                    PROJECTION, null, null, BartRiderContract.Favorites.DEFAULT_SORT);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == LOADER_ID && data != null && data.getCount() > 0) {
            Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
            favoritesTable = data;
            mAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == LOADER_ID) mAdapter.swapCursor(null);
    }

    public int isFavoriteTrip(String origAbbr, String destAbbr) {
        if (favoritesTable == null) return 0;

        favoritesTable.moveToFirst();
        while (!favoritesTable.isAfterLast()) {
            String orig = getOrigAbbr(favoritesTable);
            String dest = getDestAbbr(favoritesTable);
            if (orig.equals(origAbbr) && dest.equals(destAbbr)) {
                return getColumnId(favoritesTable);
            }
            favoritesTable.moveToNext();
        }
        return 0;
    }

    private int getColumnId(Cursor c) {
        return c.getInt(c.getColumnIndex(BartRiderContract.Favorites.Column.ID));
    }

    private String getOrigAbbr(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.ORIG_ABBR));
    }

    private String getOrigFull(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.ORIG_FULL));
    }

    private String getDestAbbr(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.DEST_ABBR));
    }

    private String getDestFull(Cursor c) {
        return c.getString(c.getColumnIndex(BartRiderContract.Favorites.Column.DEST_FULL));
    }
}