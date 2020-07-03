package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.annotations.DatabaseType;

/**
 * @author  jmussler
 */
@DatabaseType(name = "test_inheritance", inheritance = true)
public class TestInheritanceChild extends TestInheritance {

    @DatabaseField
    private int b;
    @DatabaseField
    private int c;

    public TestInheritanceChild(final int _a, final int _b, final int _c) {
        super(_a);
        b = _b;
        c = _c;
    }
}
