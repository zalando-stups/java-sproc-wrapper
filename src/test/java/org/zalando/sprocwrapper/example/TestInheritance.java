package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;

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
