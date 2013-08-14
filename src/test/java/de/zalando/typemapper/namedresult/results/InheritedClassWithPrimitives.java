package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(inheritance = true)
public class InheritedClassWithPrimitives extends ParentClassWithPrimitives {

    @DatabaseField
    public long l;

    @DatabaseField
    public String cc;

    public InheritedClassWithPrimitives(final long l, final String cc, final int i) {
        this.l = l;
        this.cc = cc;
        this.i = i;
    }

    public long getL() {
        return l;
    }

    public void setL(final long l) {
        this.l = l;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(final String cc) {
        this.cc = cc;
    }
}
