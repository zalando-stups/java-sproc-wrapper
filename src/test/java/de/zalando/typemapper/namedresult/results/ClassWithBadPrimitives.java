package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithBadPrimitives {

    @DatabaseField(name = "j")
    public int i;

    @DatabaseField(name = "l")
    public long l;

    @DatabaseField(name = "c")
    public char c;

    public int getI() {
        return i;
    }

    public void setI(final int i) {
        this.i = i;
    }

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
