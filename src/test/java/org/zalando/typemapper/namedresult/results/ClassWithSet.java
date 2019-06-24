package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.Set;

public class ClassWithSet {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "arr")
    private Set<ClassWithPrimitives> array;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public Set<ClassWithPrimitives> getArray() {
        return array;
    }

    public void setArray(final Set<ClassWithPrimitives> array) {
        this.array = array;
    }

}
