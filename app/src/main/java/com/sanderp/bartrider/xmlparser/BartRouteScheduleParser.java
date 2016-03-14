package com.sanderp.bartrider.xmlparser;

import android.util.Log;
import android.util.Xml;

import com.sanderp.bartrider.structure.RouteSchedule;

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
            String value = parser.getAttributeValue(i);

            switch (attr) {
                case ATTR_ORIGIN:
                    origin = value;
                    break;
                case ATTR_ORIGIN_TIME:
                    origin_time = value;
                    break;
                case ATTR_DEST:
                    destination = value;
                    break;
                case ATTR_DEST_TIME:
                    destination_time = value;
                    break;
                case ATTR_FARE:
                    fare = value;
                    break;
            }
            Log.i(TAG, "ATTR: " + attr);
            Log.i(TAG, "VALUE: " + value);
        }
        return new RouteSchedule(origin, origin_time, destination, destination_time, fare);
    }
}
