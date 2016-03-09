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
 * Created by Sander on 3/8/2016.
 */
public class BartStationParser {
    private static final String TAG = "BartStationParser";

    private static final String STATIONS = "stations";
    private static final String STATION = "station";
    private static final String NAME = "name";
    private static final String ABBR = "abbr";
    private static final String LAT = "gtfs_latitude";
    private static final String LONG = "gtfs_longitude";
    private static final String ADDR = "address";
    private static final String CITY = "city";
    private static final String CNTY = "county";
    private static final String STATE = "state";
    private static final String ZIP = "zipcode";

    // Namespace is not used for this parser
    public static final String ns = null;

    public static class Station {
        public final String name;
        public final String abbr;
        public final String latitude;
        public final String longitude;
        public final String address;
        public final String city;
        public final String county;
        public final String state;
        public final String zipcode;

        private Station(String name, String abbr, String latitude, String longitude, String address,
                        String city, String county, String state, String zipcode) {
            this.name = name;
            this.abbr = abbr;
            this.latitude = latitude;
            this.longitude = longitude;
            this.address = address;
            this.city = city;
            this.county = county;
            this.state = state;
            this.zipcode = zipcode;
        }
    }

    public List<Station> parse(InputStream in)
            throws XmlPullParserException, IOException {

        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                if (parser.getName().equals(STATIONS)) break;
            }
            return readAPI(parser);
        } finally {
            in.close();
        }
    }

    public List<Station> readAPI(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        List<Station> stations = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, STATIONS);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(STATION)) stations.add(readStation(parser));
        }
        return stations;
    }

    public Station readStation(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        String name = "";
        String abbr = "";
        String latitude = "";
        String longitude = "";
        String address = "";
        String city = "";
        String county = "";
        String state = "";
        String zipcode = "";

        String attr = "";
        parser.require(XmlPullParser.START_TAG, ns, STATION);
        while(parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.END_TAG) {
                if (parser.getName().equals(STATION)) break;
                else continue;
            }

            if (parser.getEventType() == XmlPullParser.START_TAG) {
                attr = parser.getName();
                Log.i(TAG, "ATTR: " + attr);
                continue;
            }

            if (attr.equals(NAME)) {
                name = parser.getText();
            } else if (attr.equals(ABBR)) {
                abbr = parser.getText();
            } else if (attr.equals(LAT)) {
                latitude = parser.getText();
            } else if (attr.equals(LONG)) {
                longitude = parser.getText();
            } else if (attr.equals(ADDR)) {
                address = parser.getText();
            } else if (attr.equals(CITY)) {
                city = parser.getText();
            } else if (attr.equals(CNTY)) {
                county = parser.getText();
            } else if (attr.equals(STATE)) {
                state = parser.getText();
            } else if (attr.equals(ZIP)) {
                zipcode = parser.getText();
            }
            Log.i(TAG, parser.getText());
        }
        return new Station(name, abbr, latitude, longitude, address, city, county, state, zipcode);
    }
}
