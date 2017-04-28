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
 * Returns the current BART Service Advisories (BSA).
 */
@Deprecated
public class AdvisoryParser {
    private static final String TAG = "AdvisoryParser";

    // Important XML field names
    private static final String BSA = "bsa";
    private static final String DESCRIPTION = "description";

    private static final String ns = null;

    public List<String> parse(InputStream in) throws XmlPullParserException, IOException {
        List<String> advisories = new ArrayList<>();

        Log.i(TAG, "Parsing Advisories API...");
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            while (parser.next() != XmlPullParser.END_DOCUMENT) {
                if (parser.getEventType() != XmlPullParser.START_TAG) continue;
                if (parser.getName().equals(BSA)) advisories.add(readDescription(parser));
            }
            return advisories;
        } finally {
            in.close();
        }
    }

    private String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        Log.i(TAG, "Parsing <description> tag...");
        parser.require(XmlPullParser.START_TAG, ns, BSA);
        while (parser.next() !=  XmlPullParser.END_DOCUMENT) {
            if (parser.getEventType() != XmlPullParser.START_TAG) continue;
            if (parser.getName().equals(DESCRIPTION)) {
                String attr = parser.getName();
                parser.next();
                String value = parser.getText();
                Log.d(TAG, attr + ": " + value);
                return value;
            }
        }
        return null;
    }
}
