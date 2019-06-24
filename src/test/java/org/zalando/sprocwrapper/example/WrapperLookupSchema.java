package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.List;

public class WrapperLookupSchema {

    @DatabaseField
    public int count;

    @DatabaseField
    public List<LookupTypeSchema> schema1;

    @DatabaseField
    public List<LookupTypeSchema> schema2;

}
