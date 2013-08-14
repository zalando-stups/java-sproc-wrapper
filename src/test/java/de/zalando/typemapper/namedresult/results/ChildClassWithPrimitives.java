package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ChildClassWithPrimitives extends ParentClassWithPrimitives {

    @DatabaseField(name = "l")
    public long l;

    @DatabaseField(name = "c")
    public char c;

    public long getL() {
        return l;
    }

    public void setL(final long l) {
        this.l = l;
    }

    public char getC() {
        return c;
    }

    public void setC(final char c) {
        this.c = c;
    }

}
