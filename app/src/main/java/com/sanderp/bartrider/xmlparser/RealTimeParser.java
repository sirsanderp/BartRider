package com.sanderp.bartrider.xmlparser;

import android.util.Log;
import android.util.Xml;

import com.sanderp.bartrider.structure.TripEstimate;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander Peerna on 8/30/2015.
 */
public class RealTimeParser {
    private static final String TAG = "RealTimeParser";

    // Important XML field names
    private static final String STATION = "station";
    private static final String ESTIMATE_TIME_TO_DEPARTURE = "etd";
    private static final String ABBREVIATION = "abbreviation";
    private static final String ESTIMATE = "estimate";
    private static final String ERROR = "error";

    private static final String ns = null;

    // Other variables
    private static String expectedDestination;

    public List<TripEstimate> parse(InputStream in, String destination) throws XmlPullParserException, IOException {
        Log.i(TAG, "Reading stream...");
        expectedDestination = destination;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                if (parser.getName().equals(STATION)) break;
            }
            return readAPI(parser);
        } finally {
            in.close();
        }
    }

    public List<TripEstimate> readAPI(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<TripEstimate> estimates = new ArrayList();

        Log.i(TAG, "Reading station...");
        parser.require(XmlPullParser.START_TAG, ns, STATION);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            if (name.equals(ERROR)) {
                parser.next();
                if (parser.getText().equals("Updates are temporarily unavailable.")) {
                    Log.e(TAG, "Real time estimates API is down");
                    return null;
                }
            } else if (name.equals(ESTIMATE_TIME_TO_DEPARTURE)) {
                estimates = readEstimatedDepartures(parser);
            }
        }
        return estimates;
    }

    public List<TripEstimate> readEstimatedDepartures(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<TripEstimate> departures = new ArrayList();
        boolean correctDestination = false;

        Log.i(TAG, "Reading etd...");
        parser.require(XmlPullParser.START_TAG, ns, ESTIMATE_TIME_TO_DEPARTURE);
        while (!correctDestination && parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            if (name.equals(ABBREVIATION)) {
                parser.next();
                if (parser.getText().equals(expectedDestination)) {
                    correctDestination = true;
                    Log.i(TAG, "Found the correct destination...");
                }
            }
        }

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(ESTIMATE_TIME_TO_DEPARTURE)) break;
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(ESTIMATE)) departures.add(readEstimate(parser));
        }
        return departures;
    }

    public TripEstimate readEstimate(XmlPullParser parser) throws XmlPullParserException, IOException {
        TripEstimate estimate = new TripEstimate();

        Log.i(TAG, "Reading estimate...");
        parser.require(XmlPullParser.START_TAG, ns, ESTIMATE);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(ESTIMATE)) break;
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String attr = parser.getName();
            parser.next();
            String value = parser.getText();
            Log.d(TAG, attr + ": " + value);
            switch (attr) {
                case "minutes":
                    estimate.setMinutes(Integer.parseInt(value));
                    break;
                case "platform":
                    estimate.setPlatform(Integer.parseInt(value));
                    break;
                case "direction":
                    estimate.setDirection(value);
                    break;
                case "length":
                    estimate.setLength(Integer.parseInt(value));
                    break;
                case "color":
                    estimate.setColor(value);
                    break;
                case "hexcolor":
                    estimate.setHexColor(value);
                    break;
                case "bikeflag":
                    estimate.setBikeFlag(Boolean.parseBoolean(value));
                    break;
            }
        }
        return null;
    }
}
