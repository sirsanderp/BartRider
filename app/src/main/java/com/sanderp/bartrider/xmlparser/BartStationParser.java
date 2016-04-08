package com.sanderp.bartrider.xmlparser;

import android.util.Log;
import android.util.Xml;

import com.sanderp.bartrider.structure.Station;

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
    private static final String LATITUDE = "gtfs_latitude";
    private static final String LONGITUDE = "gtfs_longitude";
    private static final String ADDRESS = "address";
    private static final String CITY = "city";
    private static final String COUNTY = "county";
    private static final String STATE = "state";
    private static final String ZIP = "zipcode";

    // Namespace is not used for this parser
    public static final String ns = null;

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
                continue;
            }

            String value = parser.getText();
            switch (attr) {
                case NAME:
                    name = value;
                    break;
                case ABBR:
                    abbr = value;
                    break;
                case LATITUDE:
                    latitude = value;
                    break;
                case LONGITUDE:
                    longitude = value;
                    break;
                case ADDRESS:
                    address = value;
                    break;
                case CITY:
                    city = value;
                    break;
                case COUNTY:
                    county = value;
                    break;
                case STATE:
                    state = value;
                    break;
                case ZIP:
                    zipcode = value;
                    break;
            }
            Log.d(TAG, "ATTR: " + attr);
            Log.d(TAG, "VALUE: " + value);
        }
        return new Station(name, abbr, latitude, longitude, address, city, county, state, zipcode);
    }
}
