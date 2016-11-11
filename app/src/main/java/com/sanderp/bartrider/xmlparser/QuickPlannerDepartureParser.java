package com.sanderp.bartrider.xmlparser;

import android.util.Log;
import android.util.Xml;

import com.sanderp.bartrider.structure.Departure;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sander on 10/12/2015.
 */
public class QuickPlannerDepartureParser {
    private static final String TAG = "QPDepartureParser";

    private static final String SCHEDULE = "schedule";
    private static final String TRIP = "trip";
    private static final String ORIGIN = "origin";
    private static final String ORIGIN_TIME = "origTimeMin";
    private static final String DESTINATION = "destination";
    private static final String DESTINATION_TIME = "destTimeMin";
    private static final String FARE = "fare";

    // Namespace is not used for this parser
    public static final String ns = null;

    public List<Departure> parse(InputStream in)
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

    public List<Departure> readAPI(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<Departure> departures = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, SCHEDULE);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(TRIP)) departures.add(readTrip(parser));
        }
        return departures;
    }

    public Departure readTrip(XmlPullParser parser)
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
                case ORIGIN:
                    origin = value;
                    break;
                case ORIGIN_TIME:
                    origin_time = value;
                    break;
                case DESTINATION:
                    destination = value;
                    break;
                case DESTINATION_TIME:
                    destination_time = value;
                    break;
                case FARE:
                    fare = value;
                    break;
            }
            Log.d(TAG, "ATTR: " + attr);
            Log.d(TAG, "VALUE: " + value);
        }
        return new Departure(origin, origin_time, destination, destination_time, fare);
    }
}
