package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.List;

/**
 * @author  jmussler
 */

public class ExampleDomainObjectWithInnerObject {
    @DatabaseField(name = "a")
    public String a;

    @DatabaseField(name = "b")
    public ExampleDomainObject b;

    @DatabaseField(name = "c")
    public List<ExampleDomainObject> c;

    public String getA() {
        return a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public ExampleDomainObject getB() {
        return b;
    }

    public void setB(final ExampleDomainObject b) {
        this.b = b;
    }

    public List<ExampleDomainObject> getC() {
        return c;
    }

    public void setC(final List<ExampleDomainObject> c) {
        this.c = c;
    }

    public ExampleDomainObjectWithInnerObject() { }

    public ExampleDomainObjectWithInnerObject(final String _a, final ExampleDomainObject _b) {
        a = _a;
        b = _b;
    }

}
