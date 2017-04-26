package com.sanderp.bartrider.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sander Peerna on 4/4/2017.
 */

public class Utils {
    private static final String TAG = "Utils";

    /**
     * Sets up a connection and gets an input stream
     */
    public static InputStream getUrlStream(String request) throws IOException {
        URL url = new URL(request);
        Log.d(TAG, "accessing url: " + request);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        return conn.getInputStream();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(context, "No network connection.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
