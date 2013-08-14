package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ParentClassWithPrimitives {

    @DatabaseField(name = "i")
    public int i;

    public int getI() {
        return i;
    }

    public void setI(final int i) {
        this.i = i;
    }

}
