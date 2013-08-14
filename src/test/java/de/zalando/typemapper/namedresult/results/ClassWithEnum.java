package de.zalando.typemapper.namedresult.results;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(name = "simple_enumeration_type")
public class ClassWithEnum {

    @DatabaseField(name = "a")
    Enumeration value1;

    @DatabaseField(name = "b")
    Enumeration value2;

    public ClassWithEnum() { }

    public ClassWithEnum(final Enumeration value1, final Enumeration value2) {
        this.value1 = value1;
        this.value2 = value2;
    }

    public Enumeration getValue1() {
        return value1;
    }

    public void setValue1(final Enumeration value1) {
        this.value1 = value1;
    }

    public Enumeration getValue2() {
        return value2;
    }

    public void setValue2(final Enumeration value2) {
        this.value2 = value2;
    }

}
