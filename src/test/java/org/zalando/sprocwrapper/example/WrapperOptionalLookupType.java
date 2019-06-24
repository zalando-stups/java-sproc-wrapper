package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

import java.util.List;

public class WrapperOptionalLookupType {

    @DatabaseField
    public int count;

    @DatabaseField
    public List<OptionalLookupType> list;
}
