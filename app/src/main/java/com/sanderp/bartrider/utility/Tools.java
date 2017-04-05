package com.sanderp.bartrider.utility;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Sander Peerna on 4/4/2017.
 */

public class Tools {
    public static boolean isNetworkConnected(Activity activity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        } else {
            Toast.makeText(activity.getApplicationContext(), "No network connection.", Toast.LENGTH_SHORT).show();
            return false;
        }
    }
}
