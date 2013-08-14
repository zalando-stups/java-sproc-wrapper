package de.zalando.typemapper.namedresult.results;

import java.math.BigDecimal;

import de.zalando.typemapper.annotations.DatabaseField;

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
