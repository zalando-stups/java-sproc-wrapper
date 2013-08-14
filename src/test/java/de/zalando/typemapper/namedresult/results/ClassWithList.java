package de.zalando.typemapper.namedresult.results;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithList {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "arr")
    private List<ClassWithPrimitives> array;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public List<ClassWithPrimitives> getArray() {
        return array;
    }

    public void setArray(final List<ClassWithPrimitives> array) {
        this.array = array;
    }

}
