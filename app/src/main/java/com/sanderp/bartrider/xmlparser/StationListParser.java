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
 * Returns a list of all BART stations.
 */
@Deprecated
public class StationListParser {
    private static final String TAG = "StationListParser";

    // Important XML field names
    private static final String STATIONS = "stations";
    private static final String STATION = "station";

    private static final String ns = null;

    public List<Station> parse(InputStream in) throws XmlPullParserException, IOException {
        Log.i(TAG, "Parsing Station List API...");
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

    private List<Station> readAPI(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Station> stations = new ArrayList<>();

        Log.i(TAG, "Parsing <stations> tag...");
        parser.require(XmlPullParser.START_TAG, ns, STATIONS);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(STATION)) stations.add(readStation(parser));
        }
        return stations;
    }

    private Station readStation(XmlPullParser parser) throws XmlPullParserException, IOException {
        Station station = new Station();

        Log.i(TAG, "Parsing <station> tag...");
        parser.require(XmlPullParser.START_TAG, ns, STATION);
        while(parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() == XmlPullParser.END_TAG && parser.getName().equals(STATION)) break;
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;

            String attr = parser.getName();
            parser.next();
            String value = parser.getText();
            Log.d(TAG, attr + ": " + value);
            switch (attr) {
                case "name":
                    station.setName(value);
                    break;
                case "abbr":
                    station.setAbbr(value);
                    break;
                case "gtfs_latitude":
                    station.setLatitude(value);
                    break;
                case "gtfs_longitude":
                    station.setLongitude(value);
                    break;
                case "address":
                    station.setAddress(value);
                    break;
                case "city":
                    station.setCity(value);
                    break;
                case "county":
                    station.setCounty(value);
                    break;
                case "state":
                    station.setState(value);
                    break;
                case "zipcode":
                    station.setZipcode(value);
                    break;
            }
        }
        return station;
    }
}
