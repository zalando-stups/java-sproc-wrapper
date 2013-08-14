package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.Embed;

public class ExampleDomainObjectWithEmbed {
    @DatabaseField
    public String x;

    @Embed
    public ExampleDomainObject y;

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
