package org.zalando.sprocwrapper.example.transformer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;
import org.zalando.typemapper.core.ValueTransformer;

import java.util.Date;

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
