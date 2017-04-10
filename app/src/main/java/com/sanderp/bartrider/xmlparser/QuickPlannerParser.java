package com.sanderp.bartrider.xmlparser;

import android.util.Log;
import android.util.Xml;

import com.sanderp.bartrider.structure.Trip;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Returns schedule information based on specified depart time.
 */
public class QuickPlannerParser {
    private static final String TAG = "QuickPlannerParser";

    // Important XML field names
    private static final String SCHEDULE = "schedule";
    private static final String TRIP = "trip";
    private static final String LEG = "leg";

    private static final String ns = null;

    public List<Trip> parse(InputStream in) throws XmlPullParserException, IOException {
        Log.i(TAG, "Parsing QuickPlanner (Depart) API...");
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

    private List<Trip> readAPI(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Trip> trips = new ArrayList<>();

        Log.i(TAG, "Parsing <schedule> tag...");
        parser.require(XmlPullParser.START_TAG, ns, SCHEDULE);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(TRIP)) trips.add(readTrip(parser));
        }
        return trips;
    }

    private Trip readTrip(XmlPullParser parser) throws XmlPullParserException, IOException {
        Trip trip = new Trip();

        Log.i(TAG, "Parsing <trip> tag...");
        parser.require(XmlPullParser.START_TAG, ns, TRIP);
        int attr_count = parser.getAttributeCount();
        for (int i = 0; i < attr_count; i++){
            String attr = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            switch (attr) {
                case "origin":
                    trip.setOrigin(value);
                    break;
                case "destination":
                    trip.setDestination(value);
                    break;
                case "fare":
                    trip.setFare(Double.parseDouble(value));
                    break;
                case "origTimeMin":
                    trip.setOrigTimeMin(value);
                    trip.setEstOrigDeparture(value);
                    break;
                case "origTimeDate":
                    trip.setOrigTimeDate(value);
                    break;
                case "destTimeMin":
                    trip.setDestTimeMin(value);
                    trip.setEstDestArrival(value);
                    break;
                case "destTimeDate":
                    trip.setDestTimeDate(value);
                    break;
                case "clipper":
                    trip.setClipper(Double.parseDouble(value));
                    break;
                case "tripTime":
                    trip.setTripTime(Integer.parseInt(value));
                    break;
                case "co2":
                    trip.setCo2(Double.parseDouble(value));
                    break;
            }
            Log.d(TAG, attr + ": " + value);
        }

        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.END_TAG) {
                if (parser.getName().equals(TRIP)) break;
                else continue;
            }
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(LEG)) trip.addLeg(readTripLeg(parser));
        }
        return trip;
    }

    private Trip.TripLeg readTripLeg(XmlPullParser parser) throws XmlPullParserException, IOException {
        Trip.TripLeg tripLeg = new Trip.TripLeg();

        Log.i(TAG, "Parsing <leg> tag...");
        parser.require(XmlPullParser.START_TAG, ns, LEG);
        int attr_count = parser.getAttributeCount();
        for (int i = 0; i < attr_count; i++){
            String attr = parser.getAttributeName(i);
            String value = parser.getAttributeValue(i);
            Log.d(TAG, attr + ": " + value);
            switch (attr) {
                case "order":
                    tripLeg.setOrder(Integer.parseInt(value));
                    break;
                case "transferCode":
                    tripLeg.setTransferCode(value);
                    break;
                case "origin":
                    tripLeg.setOrigin(value);
                    break;
                case "destination":
                    tripLeg.setDestination(value);
                    break;
                case "origTimeMin":
                    tripLeg.setOrigTimeMin(value);
                    tripLeg.setEstLegDeparture(value);
                    break;
                case "origTimeDate":
                    tripLeg.setOrigTimeDate(value);
                    break;
                case "destTimeMin":
                    tripLeg.setDestTimeMin(value);
                    tripLeg.setEstLegArrival(value);
                    break;
                case "destTimeDate":
                    tripLeg.setOrigTimeDate(value);
                    break;
                case "line":
                    tripLeg.setLine(value);
                    break;
                case "bikeFlag":
                    tripLeg.setBikeFlag(Boolean.parseBoolean(value));
                    break;
                case "trainHeadStation":
                    tripLeg.setTrainHeadStation(value);
                    break;
                case "load":
                    tripLeg.setLoad(Integer.parseInt(value));
                    break;
                case "trainId":
                    tripLeg.setTrainId(value);
                    break;
                case "trainIdx":
                    tripLeg.setTrainIdx(Integer.parseInt(value));
                    break;
            }
        }
        return tripLeg;
    }
}
