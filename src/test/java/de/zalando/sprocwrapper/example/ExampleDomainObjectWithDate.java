package de.zalando.sprocwrapper.example;

import java.sql.Timestamp;

import java.util.Date;

import de.zalando.typemapper.annotations.DatabaseField;

public class ExampleDomainObjectWithDate {
    @DatabaseField
    public String x;

    @DatabaseField
    public ExampleDomainObject y;

    @DatabaseField
    public Date myDate;

    @DatabaseField
    public Timestamp myTimestamp;

    public Date getMyDate() {
        return myDate;
    }

    public void setMyDate(final Date myDate) {
        this.myDate = myDate;
    }

    public Timestamp getMyTimestamp() {
        return myTimestamp;
    }

    public void setMyTimestamp(final Timestamp myTimestamp) {
        this.myTimestamp = myTimestamp;
    }

    public String getX() {
        return x;
    }

    public void setX(final String x) {
        this.x = x;
    }

    public ExampleDomainObject getY() {
        return y;
    }

    public void setY(final ExampleDomainObject y) {
        this.y = y;
    }

}
