package de.zalando.typemapper.core.fieldMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.sql.Date;

import java.text.SimpleDateFormat;

import java.util.Locale;

import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;

import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class DateFieldMapperTest {

    @DataPoints
    public static final String[][] DATES_RESULTS = {
        {"2011-11-11 11:11:11", "2011-11-11 11:11:11.000+0100"},
        {"2011-11-11", "2011-11-11 00:00:00.000+0100"},
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

        assertEquals(string[1], new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.GERMANY).format(result));
    }
}
