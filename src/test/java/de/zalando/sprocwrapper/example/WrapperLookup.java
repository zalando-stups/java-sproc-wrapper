package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;

public class WrapperLookup {

    @DatabaseField
    public int count;

    @DatabaseField
    public List<LookupType> bugs;

}
