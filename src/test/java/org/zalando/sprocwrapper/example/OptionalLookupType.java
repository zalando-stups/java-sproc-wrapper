package org.zalando.sprocwrapper.example;

import com.google.common.base.Optional;
import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  pribeiro
 */
@DatabaseType(inheritance = true)
public class OptionalLookupType extends LookupType {

    @DatabaseField
    public Optional<Integer> c = Optional.absent();

    public OptionalLookupType() { }
}
