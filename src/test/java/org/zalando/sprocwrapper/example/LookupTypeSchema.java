package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType
public class LookupTypeSchema {

    @DatabaseField
    public int a;
    @DatabaseField
    public int b;

    public LookupTypeSchema() { }
}
