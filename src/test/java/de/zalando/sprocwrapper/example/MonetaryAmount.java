package de.zalando.sprocwrapper.example;

import java.math.BigDecimal;

import de.zalando.typemapper.annotations.DatabaseField;
import de.zalando.typemapper.annotations.DatabaseType;

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
