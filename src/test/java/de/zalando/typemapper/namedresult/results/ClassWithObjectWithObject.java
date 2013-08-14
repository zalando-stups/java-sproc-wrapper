package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithObjectWithObject {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "obj")
    private ClassWithObject withObj;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public ClassWithObject getWithObj() {
        return withObj;
    }

    public void setWithObj(final ClassWithObject withObj) {
        this.withObj = withObj;
    }

}
