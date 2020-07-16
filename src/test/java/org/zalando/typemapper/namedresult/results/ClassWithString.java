package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.DatabaseField;

public class ClassWithString {

    @DatabaseField(name = "gender")
    private String field;

    public String getField() {
        return field;
    }

    public void setField(final String field) {
        this.field = field;
    }

}
