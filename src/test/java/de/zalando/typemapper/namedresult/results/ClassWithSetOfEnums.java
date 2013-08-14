package de.zalando.typemapper.namedresult.results;

import java.util.Set;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithSetOfEnums {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "arr")
    private Set<Enumeration> array;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public Set<Enumeration> getArray() {
        return array;
    }

    public void setArray(final Set<Enumeration> array) {
        this.array = array;
    }
}
