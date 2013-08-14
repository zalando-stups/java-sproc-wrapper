package de.zalando.typemapper.core.fieldMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.sql.Timestamp;

import java.text.SimpleDateFormat;

import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import de.zalando.typemapper.parser.DateTimeUtil;

public class DateFieldMapperValuesTest {

    private static final DateFieldMapper MAPPER = new DateFieldMapper();

    private static final String DATE_STRING = "2011-11-11 11:11:11";
    private static final String DATE_STRING_MILLISECONDS = "2011-11-11 11:11:11.123";
    private static final String DATE_STRING_MICROSECONDS = "2011-11-11 11:11:11.123456";

    @Test
    public void testHourAndMinute() throws Exception {
        final Date result = (Date) MAPPER.mapField(DATE_STRING, Date.class);
        assertEquals(DATE_STRING, new SimpleDateFormat("yyyy-MM-dd k:m:s").format(result));
    }

    @Test
    public void wrongInputShouldYieldNull() throws Exception {
        assertNull(MAPPER.mapField("foo", Date.class));
    }

    @Test
    public void nullClassDoesNotBorkMethod() throws Exception {
        final Object result = MAPPER.mapField(DATE_STRING, null);
        assertNotNull(result);
        Assert.assertTrue(Date.class.isAssignableFrom(result.getClass()));
    }

    @Test
    public void wrongClassDoesNotBorkMethod() throws Exception {
        final Object result = MAPPER.mapField(DATE_STRING, String.class);
        assertNotNull(result);
        Assert.assertTrue(Date.class.isAssignableFrom(result.getClass()));
    }

    @Test
    public void sqlDateClassReturnsCorrectObject() throws Exception {
        final Object result = MAPPER.mapField(DATE_STRING, java.sql.Date.class);
        assertNotNull(result);
        assertEquals(java.sql.Date.class, result.getClass());
    }

    @Test
    public void testMillisecondsTimestamp() throws Exception {
        final Timestamp result = (Timestamp) MAPPER.mapField(DATE_STRING_MICROSECONDS, Timestamp.class);
        assertEquals(DATE_STRING_MICROSECONDS, DateTimeUtil.format(result));
    }

    @Test
    public void testMillisecondsSqlDate() throws Exception {
        final java.sql.Date result = (java.sql.Date) MAPPER.mapField(DATE_STRING_MILLISECONDS, java.sql.Date.class);
        assertEquals(DATE_STRING_MILLISECONDS, new SimpleDateFormat("yyyy-MM-dd k:m:s.SSS").format(result));
    }

    @Test
    public void testMillisecondsDate() throws Exception {
        final Date result = (Date) MAPPER.mapField(DATE_STRING_MILLISECONDS, Date.class);
        assertEquals(DATE_STRING_MILLISECONDS, new SimpleDateFormat("yyyy-MM-dd k:m:s.SSS").format(result));
    }
}
