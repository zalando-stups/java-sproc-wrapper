package org.zalando.typemapper.core.fieldMapper;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(Theories.class)
public class DateFieldMapperTest {

    private static final int MS_PER_HOUR = 1000*60*60;
    private static final int MS_PER_MINUTE = 1000*60;
    private static final int rawOffset = java.util.TimeZone.getDefault().getRawOffset();
    private static final int offsetHours = rawOffset / MS_PER_HOUR;
    private static final int offsetMinutes = (rawOffset % MS_PER_HOUR) / MS_PER_MINUTE;
    private static final String sign = offsetHours > 0 ? "+" : "-";

    @DataPoints
    public static final String[][] DATES_RESULTS = {
        {"2011-11-11 11:11:11", String.format("2011-11-11 11:11:11.000%s%02d%02d", sign, offsetHours, offsetMinutes)},
        {"2011-11-11", String.format("2011-11-11 00:00:00.000%s%02d%02d", sign, offsetHours, offsetMinutes)},
        {"2011-11-11 11:11:11 +02:00", "2011-11-11 10:11:11.000+0100"},
        {"2011-11-11 11:11:11 -02:00", "2011-11-11 14:11:11.000+0100"},
        {"2011-11-11 11:11:11.123 -02:00", "2011-11-11 14:11:11.123+0100"}
    };

    @DataPoints
    public static final Class<?>[] CLASSES = {Date.class, java.util.Date.class};

    private static final DateFieldMapper MAPPER = new DateFieldMapper();

    @Theory
    public void parseAble(final String[] string, final Class<?> clazz) throws Exception {
        final Object result = MAPPER.mapField(string[0], clazz);
        assertNotNull(result);
        assertTrue(clazz.isAssignableFrom(result.getClass()));

        java.util.Date shouldBe = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.GERMANY).parse(string[1]);
        if (result instanceof java.sql.Date) {
            assertEquals(((Date) result).getTime(), shouldBe.getTime());
        } else if (result instanceof java.sql.Timestamp) {
            assertEquals(((java.sql.Timestamp) result).getTime(), shouldBe.getTime());
        } else {
            fail(String.format("Unknown type for result: %s (%s)", result, result.getClass()));
        }
    }
}
