
package org.zalando.sprocwrapper.example.transformer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zalando.sprocwrapper.example.MonetaryAmount;
import org.zalando.sprocwrapper.example.OrderMonetaryAmount;
import org.zalando.sprocwrapper.example.OrderMonetaryAmountImpl;
import org.zalando.sprocwrapper.globalvaluetransformer.annotation.GlobalValueTransformer;
import org.zalando.typemapper.core.ValueTransformer;

/**
 * @author  jmussler
 */
@GlobalValueTransformer
public class OrderMonetaryAmountTransformer extends ValueTransformer<MonetaryAmount, OrderMonetaryAmount> {

    private static final Logger LOG = LoggerFactory.getLogger(OrderMonetaryAmountTransformer.class);

    @Override
    public OrderMonetaryAmount unmarshalFromDb(final String value) {
        LOG.info(value);
        return new OrderMonetaryAmountImpl();
    }

    @Override
    public MonetaryAmount marshalToDb(final OrderMonetaryAmount bound) {
        LOG.info(bound.getCurrency() + " " + bound.getAmount());
        return new MonetaryAmount(bound.getAmount(), bound.getCurrency());
    }

}
