package de.zalando.sprocwrapper.example.transformer;

import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

import com.typemapper.core.ValueTransformer;

import de.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;

@GlobalValueTransformer
public class GlobalDateTimeTransformer extends ValueTransformer<Date, DateTime> {

    @Override
    public DateTime unmarshalFromDb(final String value) {
        final DateTimeFormatter dateTimeFormatter = new DateTimeFormatterBuilder().appendYear(4, 4).appendLiteral('-')
                                                                                  .appendMonthOfYear(1)
                                                                                  .appendLiteral('-')
                                                                                  .appendDayOfMonth(1)
                                                                                  .appendLiteral(' ').appendHourOfDay(2)
                                                                                  .appendLiteral(':')
                                                                                  .appendMinuteOfHour(2)
                                                                                  .appendLiteral(':')
                                                                                  .appendSecondOfMinute(2)
                                                                                  .appendLiteral('.')
                                                                                  .appendMillisOfSecond(0)
                                                                                  .toFormatter();

        return dateTimeFormatter.parseDateTime(value);
    }

    @Override
    public Date marshalToDb(final DateTime bound) {
        return bound.toDate();
    }

}
