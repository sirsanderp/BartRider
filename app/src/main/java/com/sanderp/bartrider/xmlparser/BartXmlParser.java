package com.sanderp.bartrider.xmlparser;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander Peerna on 8/30/2015.
 */
public class BartXmlParser {
    private static final String TAG = "BartXmlParser";

    private static final String STATION = "station";
    private static final String ESTIMATE_TIME_TO_DEPARTURE = "etd";
    private static final String ESTIMATE = "estimate";
    private static final String DESTINATION = "destination";
    private static final String MINUTES = "minutes";
    private static final String PLATFORM = "platform";

    // Namespace is not used for this parser
    public static final String ns = null;

    public static class StationDeparture {
        public final String destination;
        public final String platform;
        public final String minutes;

        private StationDeparture(String destination, String platform, String minutes) {
            this.destination = destination;
            this.platform = platform;
            this.minutes = minutes;
        }
    }

    public List<StationDeparture> parse(InputStream in)
            throws XmlPullParserException, IOException {

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

    public List<StationDeparture> readAPI(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<StationDeparture> departures = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, STATION);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            if (name.equals(ESTIMATE_TIME_TO_DEPARTURE)) {
                departures = readEstimatedDepartures(parser);
            }
        }
        return departures;
    }

    public List<StationDeparture> readEstimatedDepartures(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<StationDeparture> departures = new ArrayList();
        String destination = "";

        parser.require(XmlPullParser.START_TAG, ns, ESTIMATE_TIME_TO_DEPARTURE);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            if (name.equals(DESTINATION)) {
                destination = readTag(parser, DESTINATION);
                Log.i(TAG, destination);
            } else if (name.equals(ESTIMATE)) {
                departures.add(readEstimate(parser, destination));
            }
        }
        return departures;
    }

    public StationDeparture readEstimate(XmlPullParser parser, String destination)
            throws XmlPullParserException, IOException {

        String minutes = "";
        String platform = "";

        parser.require(XmlPullParser.START_TAG, ns, ESTIMATE);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(ESTIMATE)) break;
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String name = parser.getName();
            if (name.equals(MINUTES)) {
                minutes = readTag(parser, MINUTES);
                Log.i(TAG, minutes);
            } else if (name.equals(PLATFORM)) {
                platform = readTag(parser, PLATFORM);
                Log.i(TAG, platform);
            }
        }
        return new StationDeparture(destination, platform, minutes);
    }

    public String readTag(XmlPullParser parser, String tag)
            throws XmlPullParserException, IOException {

        String value = "";

        parser.require(XmlPullParser.START_TAG, ns, tag);
        if (parser.next() == XmlPullParser.TEXT) {
            value = parser.getText();
            parser.nextTag();
        }
        parser.require(XmlPullParser.END_TAG, ns, tag);
        return value;
    }
}
