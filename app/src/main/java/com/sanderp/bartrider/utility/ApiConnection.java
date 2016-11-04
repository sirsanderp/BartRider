package com.sanderp.bartrider.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Sander on 4/8/2016.
 */
public class ApiConnection {

    /**
     * Sets up a connection and gets an input stream
     */
    public static InputStream downloadData(String bartUrl) throws IOException {
        URL url = new URL(bartUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10000);
        conn.setConnectTimeout(15000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        conn.connect();

        return conn.getInputStream();
    }
}
