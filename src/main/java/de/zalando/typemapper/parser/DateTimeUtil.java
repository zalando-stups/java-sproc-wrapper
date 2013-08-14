package de.zalando.typemapper.parser;

import java.sql.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

public class DateTimeUtil {

    public static final String FORMAT_PATTERN = "yyyy-MM-dd kk:mm:ss.SSS";

    public static Date parse(final String ds) {

        String[] tokens = ds.split("\\.");
        if (tokens.length != 2) {
            throw new RuntimeException("Invalid date string: " + ds);
        }

        int fractionalSecs = Integer.parseInt(tokens[1]) / 1000;
        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd k:m:s.SSS").parse(String.format("%s.%03d", tokens[0],
                        fractionalSecs));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return date;
    }

    public static String format(final Date date) {
        String milliseconds = new SimpleDateFormat(FORMAT_PATTERN).format(date);
        if (date instanceof Timestamp) {
            Timestamp timestamp = (Timestamp) date;
            final int nanos = timestamp.getNanos();
            int microseconds = nanos / 1000;
            milliseconds = milliseconds.substring(0, milliseconds.indexOf(".") + 1)
                    + String.format("%06d", microseconds);
        }

        return milliseconds;
    }

}
