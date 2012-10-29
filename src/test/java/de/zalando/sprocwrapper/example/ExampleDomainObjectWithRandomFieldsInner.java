package de.zalando.sprocwrapper.example;

import com.typemapper.annotations.DatabaseField;

public class ExampleDomainObjectWithRandomFieldsInner {
    @DatabaseField
    private String z;

    @DatabaseField
    private String y;

    @DatabaseField
    private String x;

    public ExampleDomainObjectWithRandomFieldsInner(final String x, final String y, final String z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getX() {
        return x;
    }

    public void setX(final String x) {
        this.x = x;
    }

    public String getY() {
        return y;
    }

    public void setY(final String y) {
        this.y = y;
    }

    public String getZ() {
        return z;
    }

    public void setZ(final String z) {
        this.z = z;
    }

}
