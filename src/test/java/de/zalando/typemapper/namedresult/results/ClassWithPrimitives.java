package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(name = "additional_type")
public class ClassWithPrimitives {

    @DatabaseField(name = "i")
    public int i;

    @DatabaseField(name = "l")
    public long l;

    @DatabaseField(name = "c")
    public char c;

    public ClassWithPrimitives() { }

    public ClassWithPrimitives(final int i, final long l, final char c) {
        this.i = i;
        this.l = l;
        this.c = c;
    }

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
