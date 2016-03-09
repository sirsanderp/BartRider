package com.sanderp.bartrider;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander on 10/12/2015.
 */
public class BartRouteScheduleParser {

    private static final String TAG = "BartRouteScheduleParser";

    private static final String SCHEDULE = "schedule";
    private static final String REQUEST = "request";
    private static final String TRIP = "trip";
    private static final String LEG = "leg";
    private static final String ATTR_ORIGIN = "origin";
    private static final String ATTR_ORIGIN_TIME = "origTimeMin";
    private static final String ATTR_DEST = "destination";
    private static final String ATTR_DEST_TIME = "destTimeMin";
    private static final String ATTR_FARE = "fare";

    // Namespace is not used for this parser
    public static final String ns = null;

    public static class RouteSchedule {
        public final String orig;
        public final String orig_time;
        public final String dest;
        public final String dest_time;
        public final String fare;

        private RouteSchedule(String orig, String orig_time, String dest, String dest_time, String fare) {
            this.orig = orig;
            this.orig_time = orig_time;
            this.dest = dest;
            this.dest_time = dest_time;
            this.fare = fare;
        }
    }

    public List<RouteSchedule> parse(InputStream in)
            throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                if (parser.getName().equals(SCHEDULE)) break;
            }
            return readAPI(parser);
        } finally {
            in.close();
        }
    }

    public List<RouteSchedule> readAPI(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<RouteSchedule> departures = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, SCHEDULE);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(TRIP)) departures.add(readTrip(parser));
        }
        return departures;
    }

    public RouteSchedule readTrip(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        String origin = "";
        String origin_time = "";
        String destination = "";
        String destination_time = "";
        String fare = "";

        parser.require(XmlPullParser.START_TAG, ns, TRIP);

        int attr_count = parser.getAttributeCount();
        for (int i = 0; i < attr_count; i++){
            String attr = parser.getAttributeName(i);
            Log.i(TAG, "ATTR: " + attr);
            if (attr.equals(ATTR_ORIGIN)) {
                origin = parser.getAttributeValue(i);
                Log.i(TAG, origin);
            } else if (attr.equals(ATTR_ORIGIN_TIME)) {
                origin_time = parser.getAttributeValue(i);
                Log.i(TAG, origin_time);
            } else if (attr.equals(ATTR_DEST)) {
                destination = parser.getAttributeValue(i);
                Log.i(TAG, destination);
            } else if (attr.equals(ATTR_DEST_TIME)) {
                destination_time = parser.getAttributeValue(i);
                Log.i(TAG, destination_time);
            } else if (attr.equals(ATTR_FARE)) {
                fare = parser.getAttributeValue(i);
                Log.i(TAG, fare);
            }
        }
        return new RouteSchedule(origin, origin_time, destination, destination_time, fare);
    }
}
