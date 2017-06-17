package com.sanderp.bartrider.pojo;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMarshaller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Custom DynamoDB POJO deserializer for converting API update and previous departure times to epoch.
 */
public class TimeToLongMarshaller implements DynamoDBMarshaller<Long> {
    private static final SimpleDateFormat df = new SimpleDateFormat("hh:mm:ss a", Locale.US);

    @Override
    public String marshall(Long time) {
        return df.format(time);
    }

    @Override
    public Long unmarshall(Class<Long> clazz, String s) {
        try {
            return df.parse(s).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
