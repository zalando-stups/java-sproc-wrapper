package de.zalando.sprocwrapper.example;

import de.zalando.typemapper.annotations.DatabaseField;

/**
 * @author  jmussler
 */
public class TestInheritance {
    @DatabaseField
    private int a;

    public TestInheritance(final int _a) {
        a = _a;
    }
}
