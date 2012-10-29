package de.zalando.sprocwrapper;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.zalando.sprocwrapper.example.ComplexDate;
import de.zalando.sprocwrapper.example.ExampleSProcServiceDateConversionImpl;
import de.zalando.sprocwrapper.example.ExampleSProcServiceDateConversionOtherTimeZoneImpl;

import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:backendContextTest.xml"})
public class DateConversionIT {

    @Autowired
    private ExampleSProcServiceDateConversionImpl exampleSProcServiceDateConversion;

    @Autowired
    private ExampleSProcServiceDateConversionOtherTimeZoneImpl exampleSProcServiceDateConversionOtherTimeZoneImpl;

    @Test
    public void testCheckDateWithoutTimeZone() {
        final Date date = new Date();
        final Date checkDateWithoutTimeZone = exampleSProcServiceDateConversion.checkDateWithoutTimeZone(date);

        Assert.assertEquals(date, checkDateWithoutTimeZone);
    }

    @Test
    public void testCheckDateWithTimeZone() {
        final Date date = new Date();
        final Date checkDateWithTimeZone = exampleSProcServiceDateConversion.checkDateWithTimeZone(date);

        Assert.assertEquals(date, checkDateWithTimeZone);
    }

    @Test
    public void testCheckDateWithoutTimeZone2() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        final Date date = calendar.getTime();
        final Date checkDateWithTimeZone = exampleSProcServiceDateConversion.checkDateWithTimeZone(date);

        Assert.assertEquals(date, checkDateWithTimeZone);
    }

    @Test
    public void testCheckDateWithoutTimeZoneTransformation() {
        final Date date = new Date();
        final Date checkDateWithTimeZone = exampleSProcServiceDateConversion.checkDateWithoutTimeZoneTransformed(date);

        Assert.assertEquals(date, checkDateWithTimeZone);
    }

    @Test
    public void testCheckDateWithoutTimeZoneOtherPostgresZone() {
        final Date date = new Date();
        final Date checkDateWithoutTimeZone =
            exampleSProcServiceDateConversionOtherTimeZoneImpl.checkDateWithoutTimeZone(date);

        Assert.assertEquals(date, checkDateWithoutTimeZone);
    }

    @Test
    public void testCheckDateWithTimeZoneOtherPostgresZone() {
        final Date date = new Date();
        final Date checkDateWithTimeZone = exampleSProcServiceDateConversionOtherTimeZoneImpl.checkDateWithTimeZone(
                date);

        Assert.assertEquals(date, checkDateWithTimeZone);
    }

    @Test
    public void testCheckDateWithoutTimeZone2OtherPostgresZone() {
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Los_Angeles"));
        final Date date = calendar.getTime();
        final Date checkDateWithTimeZone = exampleSProcServiceDateConversionOtherTimeZoneImpl.checkDateWithTimeZone(
                date);

        Assert.assertEquals(date, checkDateWithTimeZone);
    }

    @Test
    public void testCheckDateWithoutTimeZoneTransformationOtherPostgresZone() {
        final Date date = new Date();
        final Date checkDateWithTimeZone =
            exampleSProcServiceDateConversionOtherTimeZoneImpl.checkDateWithoutTimeZoneTransformed(date);

        Assert.assertTrue(!date.equals(checkDateWithTimeZone));
    }

    @Test
    @Ignore
    public void checkDateComplexDate() {
        final ComplexDate complexDate = new ComplexDate();
        final Date date = new Date();
        complexDate.dateWithoutTimeZone = (Date) date.clone();
        complexDate.dateWithoutTimeZoneTransformed = (Date) date.clone();
        complexDate.dateWithTimeZone = (Date) date.clone();
        complexDate.dateWithTimeZoneTransformed = (Date) date.clone();

        final ComplexDate complexDateReturned = exampleSProcServiceDateConversion.checkDateComplexDate(complexDate);

        Assert.assertEquals(complexDate.dateWithoutTimeZone, complexDateReturned.dateWithoutTimeZone);
        Assert.assertEquals(complexDate.dateWithoutTimeZoneTransformed,
            complexDateReturned.dateWithoutTimeZoneTransformed);
        Assert.assertEquals(complexDate.dateWithTimeZone, complexDateReturned.dateWithTimeZone);
        Assert.assertEquals(complexDate.dateWithTimeZoneTransformed, complexDateReturned.dateWithTimeZoneTransformed);
    }

}
