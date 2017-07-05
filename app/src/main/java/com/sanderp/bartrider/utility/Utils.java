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
import java.util.HashMap;
import java.util.Map;

/**
 * Utility methods that are used throughout the application.
 */
public class Utils {
    private static final String TAG = "Utils";

    static final Map<String, String> STATION_ABBR;

    static {
        STATION_ABBR = new HashMap<>();
        STATION_ABBR.put("12TH", "12th St. Oakland City Center");
        STATION_ABBR.put("16TH", "16th St. Mission");
        STATION_ABBR.put("19TH", "19th St. Oakland");
        STATION_ABBR.put("24TH", "24th St. Mission");
        STATION_ABBR.put("ASHB", "Ashby");
        STATION_ABBR.put("BALB", "Balboa Park");
        STATION_ABBR.put("BAYF", "Bay Fair");
        STATION_ABBR.put("CAST", "Castro Valley");
        STATION_ABBR.put("CIVC", "Civic Center/UN Plaza");
        STATION_ABBR.put("COLS", "Coliseum");
        STATION_ABBR.put("COLM", "Colma");
        STATION_ABBR.put("CONC", "Concord");
        STATION_ABBR.put("DALY", "Daly City");
        STATION_ABBR.put("DBRK", "Downtown Berkeley");
        STATION_ABBR.put("DUBL", "Dublin/Pleasanton");
        STATION_ABBR.put("DELN", "El Cerrito del Norte");
        STATION_ABBR.put("PLZA", "El Cerrito Plaza");
        STATION_ABBR.put("EMBR", "Embarcadero");
        STATION_ABBR.put("FRMT", "Fremont");
        STATION_ABBR.put("FTVL", "Fruitvale");
        STATION_ABBR.put("GLEN", "Glen Park");
        STATION_ABBR.put("HAYW", "Hayward");
        STATION_ABBR.put("LAFY", "Lafayette");
        STATION_ABBR.put("LAKE", "Lake Merritt");
        STATION_ABBR.put("MCAR", "MacArthur");
        STATION_ABBR.put("MLBR", "Millbrae");
        STATION_ABBR.put("MONT", "Montgomery St.");
        STATION_ABBR.put("NBRK", "North Berkeley");
        STATION_ABBR.put("NCON", "North Concord/Martinez");
        STATION_ABBR.put("OAKL", "Oakland International Airport");
        STATION_ABBR.put("ORIN", "Orinda");
        STATION_ABBR.put("PITT", "Pittsburg/Bay Point");
        STATION_ABBR.put("PHIL", "Pleasant Hill/Contra Costa Centre");
        STATION_ABBR.put("POWL", "Powell St.");
        STATION_ABBR.put("RICH", "Richmond");
        STATION_ABBR.put("ROCK", "Rockridge");
        STATION_ABBR.put("SBRN", "San Bruno");
        STATION_ABBR.put("SFIA", "San Francisco International Airport");
        STATION_ABBR.put("SANL", "San Leandro");
        STATION_ABBR.put("SHAY", "South Hayward");
        STATION_ABBR.put("SSAN", "South San Francisco");
        STATION_ABBR.put("UCTY", "Union City");
        STATION_ABBR.put("WCRK", "Walnut Creek");
        STATION_ABBR.put("WDUB", "West Dublin/Pleasanton");
        STATION_ABBR.put("WOAK", "West Oakland");
        STATION_ABBR.put("WARM", "Warm Springs/South Fremont");
    }

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
        Log.i(TAG, "accessing url: " + request);
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
                    context.getResources().getString(R.string.cognitoIdentityPoolId),
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
     * Mappings of station abbreviations to full names.
     * @param abbr A station abbreviation.
     * @return A station full name.
     */
    public static String getStationFull(String abbr) {
        return STATION_ABBR.get(abbr);
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
