package de.zalando.typemapper.namedresult.results;

import java.util.Map;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(name = "additional_type_with_positions")
public class ClassWithPrimitivesAndMap {

    @DatabaseField(name = "i", position = 1)
    public int i;

    @DatabaseField(name = "l", position = 2)
    public long l;

    @DatabaseField(name = "c", position = 3)
    public char c;

    @DatabaseField(name = "h", position = 4)
    public Map<String, String> h;

    public ClassWithPrimitivesAndMap(final int i, final long l, final char c, final Map<String, String> h) {
        this.i = i;
        this.l = l;
        this.c = c;
        this.h = h;
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

    public Map<String, String> getH() {
        return h;
    }

    public void setH(final Map<String, String> h) {
        this.h = h;
    }

}
