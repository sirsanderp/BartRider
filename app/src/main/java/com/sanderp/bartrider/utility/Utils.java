package com.sanderp.bartrider.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanderp.bartrider.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sander Peerna on 4/4/2017.
 */
public class Utils {
    private static final String TAG = "Utils";

    private static AmazonDynamoDBClient dDbClient;
    private static CognitoCachingCredentialsProvider credentialsProvider;
    private static ObjectMapper mapper;

    private Utils() {}

    /**
     * Sets up a HTTP connection and gets an input stream.
     * @param request The URL of the request.
     * @return The input stream for the URL.
     * @throws IOException
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

    /**
     * Gets an instance of CognitoCachingCredentialsProvider which is
     * constructed using the given Context.
     * @param context An Context instance.
     * @return A default credential provider.
     */
    private static CognitoCachingCredentialsProvider getCredentialsProvider(Context context) {
        if (credentialsProvider == null) {
            credentialsProvider = new CognitoCachingCredentialsProvider(
                    context,
                    context.getResources().getString(R.string.identityPoolId),
                    Regions.US_WEST_2
            );
        }
        return credentialsProvider;
    }

    /**
     * Create an instance of AmazonDynamoDBClient which is
     * constructed using the given Context.
     * @param context A context instance.
     * @return A default instance.
     */
    public static AmazonDynamoDBClient getDynamoDbClient(Context context) {
        if (dDbClient == null) {
            dDbClient = new AmazonDynamoDBClient(getCredentialsProvider(context));
            dDbClient.setRegion(Region.getRegion(Regions.US_WEST_2));
        }
        return dDbClient;
    }

    /**
     * Create a static instance of the ObjectMapper.
     * @return A default Jackson object mapper.
     */
    public static ObjectMapper getObjectMapper() {
        if (mapper == null) {
            mapper = new ObjectMapper()
                    .configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        }
        return mapper;
    }

    /**
     * Checks for network connection using Android connectivity system service.
     * @param context A context instance.
     * @return <b>true</b> if there is network connection, else false and notify the user
     */
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
