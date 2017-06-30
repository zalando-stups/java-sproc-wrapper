package de.zalando.typemapper.namedresult.results;

import javax.persistence.Column;

public class ClassWithColumnDefinitionWithObject {

    @Column(name = "name")
    public String name;

    @Column(name = "nested")
    public ClassWithColumnDefinition nested;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public ClassWithColumnDefinition getNested() {
        return nested;
    }

    public void setNested(final ClassWithColumnDefinition nested) {
        this.nested = nested;
    }
}
