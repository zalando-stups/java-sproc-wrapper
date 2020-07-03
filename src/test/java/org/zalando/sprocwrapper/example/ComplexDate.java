package org.zalando.sprocwrapper.example;

import java.util.Date;

import org.zalando.typemapper.annotations.DatabaseField;

public class ComplexDate {
    @DatabaseField
    public Date dateWithoutTimeZone;

    @DatabaseField
    public Date dateWithTimeZone;

    @DatabaseField
    public Date dateWithoutTimeZoneTransformed;

    @DatabaseField
    public Date dateWithTimeZoneTransformed;
}
