package org.zalando.sprocwrapper.example;

import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.annotations.DatabaseType;

import java.math.BigDecimal;

/**
 * @author  jmussler
 */
@DatabaseType
public class MonetaryAmount {

    public MonetaryAmount() { }

    public MonetaryAmount(final BigDecimal a, final String c) {
        currency = c;
        amount = a;
    }

    @DatabaseField
    public String currency;

    @DatabaseField
    public BigDecimal amount;
}
