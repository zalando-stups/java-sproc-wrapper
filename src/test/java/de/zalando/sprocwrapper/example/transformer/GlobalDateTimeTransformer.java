package de.zalando.sprocwrapper.example.transformer;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;

import de.zalando.typemapper.core.ValueTransformer;

@GlobalValueTransformer
public class GlobalDateTimeTransformer extends ValueTransformer<Date, DateTime> {

    // DateTimeFormatterBuilder itself is mutable and not thread-safe, but the formatters that it builds are
    // thread-safe and immutable.
    private static final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendYear(4, 4)
                                                                                             .appendLiteral('-')
                                                                                             .appendMonthOfYear(1)
                                                                                             .appendLiteral('-')
                                                                                             .appendDayOfMonth(1)
                                                                                             .appendLiteral(' ')
                                                                                             .appendHourOfDay(2)
                                                                                             .appendLiteral(':')
                                                                                             .appendMinuteOfHour(2)
                                                                                             .appendLiteral(':')
                                                                                             .appendSecondOfMinute(2)
                                                                                             .appendOptional(
            new DateTimeFormatterBuilder().appendLiteral('.').appendFractionOfSecond(0, 3).toParser()).toFormatter();

    @Override
    public DateTime unmarshalFromDb(final String value) {
        return dateTimeFormatter.parseDateTime(value);
    }

    @Override
    public Date marshalToDb(final DateTime bound) {
        return bound.toDate();
    }

}
