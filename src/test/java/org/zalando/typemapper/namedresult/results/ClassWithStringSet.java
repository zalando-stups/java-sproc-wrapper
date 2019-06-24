package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.Set;

public class ClassWithStringSet {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "arr")
    private Set<String> array;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public Set<String> getArray() {
        return array;
    }

    public void setArray(final Set<String> array) {
        this.array = array;
    }

}
