package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType
public class LookupType {

    @DatabaseField
    public int a;
    @DatabaseField
    public int b;

    public LookupType() { }
}
