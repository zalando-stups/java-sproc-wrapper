package de.zalando.sprocwrapper.example;

import java.util.List;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(name = "ztest_shard.bug_lookup_type_schema")
public class WrapperLookupSchema {

    @DatabaseField
    public int count;

    @DatabaseField
    public List<LookupTypeSchema> bugs;

}
