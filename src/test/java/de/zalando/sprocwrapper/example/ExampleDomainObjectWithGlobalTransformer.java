package de.zalando.sprocwrapper.example;

import java.util.List;
import java.util.Set;

import com.typemapper.annotations.DatabaseField;

/**
 * @author  jmussler
 */
public class ExampleDomainObjectWithGlobalTransformer {
    @DatabaseField
    public String a;

    @DatabaseField
    public GlobalTransformedObject b;

    @DatabaseField
    public List<GlobalTransformedObject> c;

    @DatabaseField
    public Set<GlobalTransformedObject> d;

    public String getA() {
        return a;
    }

    public void setA(final String a) {
        this.a = a;
    }

    public GlobalTransformedObject getB() {
        return b;
    }

    public void setB(final GlobalTransformedObject b) {
        this.b = b;
    }

    public void setC(final List<GlobalTransformedObject> c) {
        this.c = c;
    }

    public List<GlobalTransformedObject> getC() {
        return c;
    }

    public void setD(final Set<GlobalTransformedObject> d) {
        this.d = d;
    }

    public Set<GlobalTransformedObject> getD() {
        return d;
    }

    public ExampleDomainObjectWithGlobalTransformer() { }

    public ExampleDomainObjectWithGlobalTransformer(final String _a, final GlobalTransformedObject _b,
            final List<GlobalTransformedObject> _c, final Set<GlobalTransformedObject> _d) {
        a = _a;
        b = _b;
        c = _c;
        d = _d;
    }
}
