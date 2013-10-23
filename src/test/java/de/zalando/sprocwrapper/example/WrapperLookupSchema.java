package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;

public class WrapperLookupSchema {

    @DatabaseField
    public int count;

    @DatabaseField
    public List<LookupTypeSchema> schema1;

    @DatabaseField
    public List<LookupTypeSchema> schema2;

}
