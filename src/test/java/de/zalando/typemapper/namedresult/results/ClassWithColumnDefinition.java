package de.zalando.typemapper.namedresult.results;

import javax.persistence.Column;

public class ClassWithColumnDefinition {

    @Column(name = "l")
    public long l;

    @Column
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
