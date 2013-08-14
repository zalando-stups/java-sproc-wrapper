package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;

public class ExampleDomainObjectWithoutSetters {
    @DatabaseField
    private String a;

    @DatabaseField
    private String b;

    @DatabaseField
    private String c;

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getC() {
        return c;
    }
}
