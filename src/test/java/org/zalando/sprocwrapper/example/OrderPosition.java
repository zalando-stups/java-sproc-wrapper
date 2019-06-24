package org.zalando.sprocwrapper.example;

import com.google.common.base.Optional;
import org.zalando.sprocwrapper.example.transformer.MoneyObjectMapper;
import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.annotations.DatabaseType;

@DatabaseType(name = "order_position_type")
public class OrderPosition {
    @DatabaseField(mapper = MoneyObjectMapper.class)
    public OrderMonetaryAmount amount;

    @DatabaseField(mapper = MoneyObjectMapper.class)
    public Optional<OrderMonetaryAmount> optionalAmount;

    @DatabaseField
    public Optional<AddressPojo> address = Optional.absent();

    public OrderPosition() {
        this(null);
    }

    public OrderPosition(final OrderMonetaryAmount amount) {
        this(amount, null);
    }

    public OrderPosition(final OrderMonetaryAmount amount, final OrderMonetaryAmount optionalAmount) {
        this(amount, optionalAmount, null);
    }

    public OrderPosition(final OrderMonetaryAmount amount, final OrderMonetaryAmount optionalAmount,
            final AddressPojo address) {
        this.amount = amount;
        this.optionalAmount = Optional.fromNullable(optionalAmount);
        this.address = Optional.fromNullable(address);
    }

}
