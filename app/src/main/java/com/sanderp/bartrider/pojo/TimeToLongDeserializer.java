package com.sanderp.bartrider.pojo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Custom Jackson POJO deserializer for converting trip origin and destination times to epoch.
 */
public class TimeToLongDeserializer extends StdDeserializer<Long> {
    private static final SimpleDateFormat df = new SimpleDateFormat("h:mm a", Locale.US);

    public TimeToLongDeserializer() {
        this(null);
    }

    public TimeToLongDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Long deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        try {
            String time = jsonparser.getText();
            return df.parse(time).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0L;
    }
}