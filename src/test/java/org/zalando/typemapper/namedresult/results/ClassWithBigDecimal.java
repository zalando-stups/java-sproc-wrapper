package org.zalando.typemapper.namedresult.results;

import java.math.BigDecimal;

import org.zalando.typemapper.annotations.DatabaseField;

public class ClassWithBigDecimal {

    @DatabaseField(name = "i")
    private BigDecimal i;

    public BigDecimal getI() {
        return i;
    }

    public void setI(final BigDecimal i) {
        this.i = i;
    }

}
