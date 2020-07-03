package org.zalando.sprocwrapper.example;

import java.util.List;

import org.zalando.typemapper.annotations.DatabaseField;

public class WrapperOptionalLookupType {

    @DatabaseField
    public int count;

    @DatabaseField
    public List<OptionalLookupType> list;
}
