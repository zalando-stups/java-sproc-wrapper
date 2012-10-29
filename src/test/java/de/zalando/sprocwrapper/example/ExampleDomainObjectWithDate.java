package de.zalando.sprocwrapper.example;

import java.util.Date;

import com.typemapper.annotations.DatabaseField;

public class ExampleDomainObjectWithDate {
    @DatabaseField
    public String x;

    @DatabaseField
    public ExampleDomainObject y;

    @DatabaseField
    public Date myDate;

    @DatabaseField
    public Date myTimestamp;

    public Date getMyDate() {
        return myDate;
    }

    public void setMyDate(final Date myDate) {
        this.myDate = myDate;
    }

    public Date getMyTimestamp() {
        return myTimestamp;
    }

    public void setMyTimestamp(final Date myTimestamp) {
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
