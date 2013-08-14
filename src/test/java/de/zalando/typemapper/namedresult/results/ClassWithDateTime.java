package de.zalando.typemapper.namedresult.results;

import java.util.Date;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithDateTime {

    @DatabaseField(name = "lt")
    public Date dateWithoutTimezone;

    @DatabaseField(name = "gt")
    public Date dateWithTimezone;

    @DatabaseField(name = "zone")
    public String zone;

    public ClassWithDateTime() { }

    public Date getDateWithoutTimezone() {
        return dateWithoutTimezone;
    }

    public void setDateWithoutTimezone(final Date dateWithoutTimezone) {
        this.dateWithoutTimezone = dateWithoutTimezone;
    }

    public Date getDateWithTimezone() {
        return dateWithTimezone;
    }

    public void setDateWithTimezone(final Date dateWithTimezone) {
        this.dateWithTimezone = dateWithTimezone;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(final String zone) {
        this.zone = zone;
    }

}
