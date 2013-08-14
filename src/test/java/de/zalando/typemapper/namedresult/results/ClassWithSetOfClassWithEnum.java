package de.zalando.typemapper.namedresult.results;

import java.util.Set;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithSetOfClassWithEnum {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "arr")
    private Set<ClassWithEnum> array;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public Set<ClassWithEnum> getArray() {
        return array;
    }

    public void setArray(final Set<ClassWithEnum> array) {
        this.array = array;
    }

}
