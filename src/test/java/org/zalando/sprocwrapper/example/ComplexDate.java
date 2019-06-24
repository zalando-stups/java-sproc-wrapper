package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.Date;

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
