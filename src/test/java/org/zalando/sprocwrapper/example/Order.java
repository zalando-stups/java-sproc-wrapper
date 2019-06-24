package org.zalando.sprocwrapper.example;

import com.google.common.base.Optional;
import org.zalando.sprocwrapper.example.transformer.MoneyObjectMapper;
import org.zalando.typemapper.annotations.DatabaseField;
import org.zalando.typemapper.annotations.DatabaseType;

import java.util.List;

/**
 * @author  jmussler
 */
@DatabaseType(name = "order_type")
public class Order {

    @DatabaseField
    public String orderNumber;

    @DatabaseField(mapper = MoneyObjectMapper.class)
    public OrderMonetaryAmount amount;

    @DatabaseField
    public List<OrderPosition> positions;

    @DatabaseField
    public Optional<AddressPojo> address;

    public Order(final String on, final OrderMonetaryAmount a) {
        this(on, a, null);
    }

    public Order(final String on, final OrderMonetaryAmount a, final AddressPojo address) {
        this.orderNumber = on;
        this.amount = a;
        this.address = Optional.fromNullable(address);
    }

    public Order() {
        this(null, null);
    }
}
