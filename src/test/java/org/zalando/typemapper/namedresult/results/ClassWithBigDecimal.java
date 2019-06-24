package org.zalando.typemapper.namedresult.results;

import org.zalando.typemapper.annotations.DatabaseField;

import java.math.BigDecimal;

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
