package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithObject {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "obj")
    private ClassWithPrimitives primitives;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public ClassWithPrimitives getPrimitives() {
        return primitives;
    }

    public void setPrimitives(final ClassWithPrimitives primitives) {
        this.primitives = primitives;
    }

}
