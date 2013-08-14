package de.zalando.sprocwrapper.example;

import java.util.Map;

import de.zalando.typemapper.annotations.DatabaseField;

public class ExampleDomainObjectWithMap {
    @DatabaseField(name = "a")
    public String a;

    @DatabaseField(name = "b")
    public Map<String, String> b;

    public String getA() {
        return a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public Map<String, String> getB() {
        return b;
    }

    public void setB(final Map<String, String> b) {
        this.b = b;
    }

    public ExampleDomainObjectWithMap() { }

    public ExampleDomainObjectWithMap(final String _a, final Map<String, String> _b) {
        a = _a;
        b = _b;
    }

}
