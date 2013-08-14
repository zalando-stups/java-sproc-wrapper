package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithPrimitives2 {

    @DatabaseField(name = "i")
    public int i;

    @DatabaseField(name = "l")
    public long l;

    @DatabaseField(name = "c")
    public char c;

    @DatabaseField(name = "h")
    public char h;

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

    public char getH() {
        return h;
    }

    public void setH(final char h) {
        this.h = h;
    }

}
