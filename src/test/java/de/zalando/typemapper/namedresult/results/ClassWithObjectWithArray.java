package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;

public class ClassWithObjectWithArray {

    @DatabaseField(name = "str")
    private String str;

    @DatabaseField(name = "obj")
    private ObjectWithArray obj;

    public String getStr() {
        return str;
    }

    public void setStr(final String str) {
        this.str = str;
    }

    public ObjectWithArray getObj() {
        return obj;
    }

    public void setObj(final ObjectWithArray obj) {
        this.obj = obj;
    }
}
