package com.sanderp.bartrider;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
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
 * A simple {@link Fragment} subclass to display user favorited trips.
 * Activities that contain this fragment must implement the {@link OnFragmentListener}
 * interface to handle interaction events.
 */
public class TripDrawerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "TripDrawerFragment";

    public static final int FAVORITES_LOADER_ID = 1;
    public static final int RECENTS_LOADER_ID = 2;
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
    private Cursor recentsTable;
    private SimpleCursorAdapter mFavoritesAdapter;
    private SimpleCursorAdapter mRecentsAdapter;
    private ListView mFavoritesListView;
    private ListView mRecentsListView;
    private TextView mEmptyFavoritesView;
    private TextView mEmptyRecentsView;

    private OnFragmentListener mFragmentListener;

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

        mFavoritesAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null, FROM, TO, 0);
        mFavoritesAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text1) {
                    TextView mTextView = (TextView) view;
                    mTextView.setEllipsize(TextUtils.TruncateAt.END);
                    mTextView.setMaxLines(1);
                    mTextView.setText(normalizeText(getOrigFull(cursor), 16) + " - " + normalizeText(getDestFull(cursor), 16));
                    mTextView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                    mTextView.setTextSize(14);
                    return true;
                }
                return false;
            }
        });

        mFavoritesListView = (ListView) view.findViewById(R.id.favorites_list_view);
        mFavoritesListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor itemCursor = mFavoritesAdapter.getCursor();
                mFragmentListener.onFavoriteClick(
                        getId(itemCursor),
                        getOrigAbbr(itemCursor),
                        getOrigFull(itemCursor),
                        getDestAbbr(itemCursor),
                        getDestFull(itemCursor));
            }
        });
        mFavoritesListView.setAdapter(mFavoritesAdapter);
        mEmptyFavoritesView = (TextView) view.findViewById(R.id.empty_favorites_list);
        getLoaderManager().initLoader(FAVORITES_LOADER_ID, null, this);

        mRecentsAdapter = new SimpleCursorAdapter(getActivity(),
                android.R.layout.simple_list_item_1, null, FROM, TO, 0);
        mRecentsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == android.R.id.text1) {
                    TextView mTextView = (TextView) view;
                    mTextView.setEllipsize(TextUtils.TruncateAt.END);
                    mTextView.setMaxLines(1);
                    mTextView.setText(normalizeText(getOrigFull(cursor), 16) + " - " + normalizeText(getDestFull(cursor), 16));
                    mTextView.setTextColor(getResources().getColor(android.R.color.primary_text_light));
                    mTextView.setTextSize(14);
                    return true;
                }
                return false;
            }
        });

        mRecentsListView = (ListView) view.findViewById(R.id.recents_list_view);
        mRecentsListView.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor itemCursor = mRecentsAdapter.getCursor();
                mFragmentListener.onFavoriteClick(
                        getId(itemCursor),
                        getOrigAbbr(itemCursor),
                        getOrigFull(itemCursor),
                        getDestAbbr(itemCursor),
                        getDestFull(itemCursor));
            }
        });
        mRecentsListView.setAdapter(mRecentsAdapter);
        mEmptyRecentsView = (TextView) view.findViewById(R.id.empty_recents_list);
        getLoaderManager().initLoader(RECENTS_LOADER_ID, null, this);

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
        if (id == FAVORITES_LOADER_ID) {
            return new CursorLoader(getActivity(), BartRiderContract.Favorites.CONTENT_URI,
                    PROJECTION, null, null, BartRiderContract.Favorites.Column.ORIG_FULL + " ASC," + BartRiderContract.Favorites.Column.DEST_FULL);
        } else if (id == RECENTS_LOADER_ID) {
            return new CursorLoader(getActivity(), BartRiderContract.Recents.CONTENT_URI,
                    PROJECTION, null, null, null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            Log.d(TAG, DatabaseUtils.dumpCursorToString(data));
            if (loader.getId() == FAVORITES_LOADER_ID) {
                Log.i(TAG, "Loaded favorites table.");
                if (data.getCount() == 0) mFavoritesListView.setEmptyView(mEmptyFavoritesView);
                mFavoritesAdapter.swapCursor(data);
                favoritesTable = data;
            } else if (loader.getId() == RECENTS_LOADER_ID) {
                Log.i(TAG, "Loaded recents table.");
                if (data.getCount() == 0) mRecentsListView.setEmptyView(mEmptyRecentsView);
                mRecentsAdapter.swapCursor(data);
                recentsTable = data;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (loader.getId() == FAVORITES_LOADER_ID) mFavoritesAdapter.swapCursor(null);
        else if (loader.getId() == RECENTS_LOADER_ID) mRecentsAdapter.swapCursor(null);
    }

    public int isFavoriteTrip(String origAbbr, String destAbbr) {
        if (favoritesTable == null) return 0;

        favoritesTable.moveToFirst();
        while (!favoritesTable.isAfterLast()) {
            String orig = getOrigAbbr(favoritesTable);
            String dest = getDestAbbr(favoritesTable);
            if (orig.equals(origAbbr) && dest.equals(destAbbr)) {
                return getId(favoritesTable);
            }
            favoritesTable.moveToNext();
        }
        return 0;
    }

    public boolean isRecentTrip(String origAbbr, String destAbbr) {
        if (recentsTable == null) return false;

        recentsTable.moveToFirst();
        while (!recentsTable.isAfterLast()) {
            String orig = getOrigAbbr(recentsTable);
            String dest = getDestAbbr(recentsTable);
            if (orig.equals(origAbbr) && dest.equals(destAbbr)) {
                return true;
            }
            recentsTable.moveToNext();
        }
        return false;
    }

    private String normalizeText(String text, int maxLength) {
        if (text.length() <= maxLength) return text;
        else return text.substring(0, maxLength) + "...";
    }

    private int getId(Cursor c) {
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